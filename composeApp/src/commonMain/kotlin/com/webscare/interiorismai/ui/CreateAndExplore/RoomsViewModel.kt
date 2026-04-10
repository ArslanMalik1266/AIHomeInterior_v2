package com.webscare.interiorismai.ui.CreateAndExplore

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import com.webscare.interiorismai.data.local.entities.DraftEntity
import com.webscare.interiorismai.data.local.entities.RecentGeneratedEntity
import com.webscare.interiorismai.data.mapper.toUi
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.GenerateRoomRequest
import com.webscare.interiorismai.domain.model.InteriorStyle
import com.webscare.interiorismai.domain.repo.DraftsRepository
import com.webscare.interiorismai.domain.repo.InteriorsRepository
import com.webscare.interiorismai.domain.repo.RecentGeneratedRepository
import com.webscare.interiorismai.domain.repo.RoomsRepository
import com.webscare.interiorismai.domain.usecase.AddCreditsUseCase
import com.webscare.interiorismai.domain.usecase.FetchGeneratedRoomUseCase
import com.webscare.interiorismai.domain.usecase.GenerateRoomUseCase
import com.webscare.interiorismai.domain.usecase.SpendCreditsUseCase
import com.webscare.interiorismai.domain.usecase.SpendCreditsUseCaseGuest
import com.webscare.interiorismai.domain.usecase.StartImageTrackingUseCase
import com.webscare.interiorismai.ui.Generate.UiScreens.ColorPalette
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import com.webscare.interiorismai.ui.authentication.register.RegisterEvent
import com.webscare.interiorismai.ui.common.base.CommonUiEvent
import com.webscare.interiorismai.ui.common.base.CommonUiEvent.ShowError
import com.webscare.interiorismai.utils.GenerationStatus
import com.webscare.interiorismai.utils.NotificationManager
import com.webscare.interiorismai.utils.downloadAndCacheImage
import com.webscare.interiorismai.utils.executeApiCall
import com.webscare.interiorismai.utils.getDeviceId
import com.webscare.interiorismai.utils.readLocalFile
import com.webscare.interiorismai.utils.saveImageBytes
import com.webscare.interiorismai.utils.toBase64
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RoomsViewModel(
    val roomsRepository: RoomsRepository,
    private val addCreditsUseCase: AddCreditsUseCase,
    private val authViewModel: AuthViewModel,
    private val draftsRepository: DraftsRepository,
    private val recentGeneratedRepository: RecentGeneratedRepository,
    private val interiorsRepository: InteriorsRepository,
    private val spendCreditsUseCase: SpendCreditsUseCase,
    private val generateRoomUseCase: GenerateRoomUseCase,
    private val fetchGeneratedRoomUseCase: FetchGeneratedRoomUseCase,
    private val httpClient: io.ktor.client.HttpClient,
    private val spendCreditsUseCaseGuest: SpendCreditsUseCaseGuest,
    private val startImageTrackingUseCase: StartImageTrackingUseCase,
) : ViewModel() {
    private val listLock = Mutex()


    private val generationJobs = mutableMapOf<String, kotlinx.coroutines.Job>()
    private val _state = MutableStateFlow(RoomUiState())
    val state: StateFlow<RoomUiState> = _state.asStateFlow()
    private val _tasksProgress = MutableStateFlow<Map<String, Float>>(emptyMap())

    private var billingHelper: com.webscare.interiorismai.billing.BillingHelper? = null

    val tasksProgress = _tasksProgress.asStateFlow()
    // Har TaskID ka apna status track karne ke liye
    private val _tasksStatus = MutableStateFlow<Map<String, GenerationStatus>>(emptyMap())
    val tasksStatus = _tasksStatus.asStateFlow()
    private val _selectedBundleId = MutableStateFlow<String?>(null)
    val selectedBundleId = _selectedBundleId.asStateFlow()


    fun selectBundle(bundleId: String?) {
        _selectedBundleId.value = bundleId
    }

    private val _isDbLoaded = MutableStateFlow(false)
    val isDbLoaded = _isDbLoaded.asStateFlow()
    @OptIn(ExperimentalTime::class)
    private fun generateTaskId() = Clock.System.now().toEpochMilliseconds().toString()
    private val _taskQueue = MutableStateFlow<List<String>>(emptyList())
    val taskQueue = _taskQueue.asStateFlow()

    private val _generationProgress = MutableStateFlow(0f)
    val generationProgress = _generationProgress.asStateFlow()
    private var timerJob: kotlinx.coroutines.Job? = null
    private val _uiEvent = MutableSharedFlow<CommonUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private val _selectedGeneratedImage = MutableStateFlow<String?>(null)
    val selectedGeneratedImage: StateFlow<String?> = _selectedGeneratedImage.asStateFlow()
    val draftImages: StateFlow<List<DraftEntity>> = draftsRepository.getAllDrafts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private var currentDraftId: Long? = null
    fun selectDraftImage(draft: DraftEntity) {
        currentDraftId = draft.id

        onRoomEvent(
            RoomEvent.SetImageBytes(
                bytes = draft.userImageBytes ?: byteArrayOf(),
                fileName = "draft_${draft.id}.jpg"
            )
        )
        _state.update {
            it.copy(
                selectedRoomType = draft.roomType,
                selectedStyleName = draft.style,
                selectedPaletteId = draft.paletteId,
                currentPage = draft.currentPage,
                selectedImage = "draft_picked"
            )
        }
    }
    val dbGeneratedImages: StateFlow<List<RecentGeneratedEntity>> =
        recentGeneratedRepository.getRecentGenerated()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    @OptIn(ExperimentalTime::class)
    fun saveOrUpdateDraft() {
        val currentState = _state.value
        val currentImage = currentState.selectedImageBytes ?: return

        // Room Entity object banayein
        val newDraft = DraftEntity(
            id = currentDraftId ?: 0L, // Agar ID hai to update hoga, 0 hai to naya banega
            userImageBytes = currentImage,
            roomType = currentState.selectedRoomType ?: "Living Room",
            style = currentState.selectedStyleName ?: "Modern",
            paletteId = currentState.selectedPaletteId ?: 0,
            currentPage = currentState.currentPage,
            createdAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
        )

        viewModelScope.launch {
            draftsRepository.saveDraft(newDraft)
            currentDraftId = null
            resetGenerationState()
        }
    }

    fun selectDraftForEditing(draft: DraftEntity) {
        currentDraftId = draft.id

        _state.update {
            it.copy(
                selectedImageBytes = draft.userImageBytes,
                selectedRoomType = draft.roomType,
                selectedStyleName = draft.style,
                selectedPaletteId = draft.paletteId,
                currentPage = draft.currentPage,
                selectedImage = "draft_picked"
            )
        }
    }

    fun onGeneratedImageClick(imageUrl: String) {
        _selectedGeneratedImage.value = imageUrl
    }

    fun resetSelectedGeneratedImage() {
        _selectedGeneratedImage.value = null
    }

    init {
        initBilling()
        getRooms()
        loadInteriorsData()
        viewModelScope.launch {
            dbGeneratedImages.collect { images ->
                _isDbLoaded.value = true
                if (images.isEmpty()) {
                    println("🟢 FETCH_FLOW: ⚠️ Database is EMPTY")
                } else {
                    images.forEachIndexed { index, entity ->
                        println("🟢 FETCH_FLOW: [$index] ID = ${entity.id}")
                    }
                }
            }
        }
    }

    fun resetGenerationState() {
        _state.update {
            it.copy(
                selectedRoomType = null,
                selectedStyleName = null,
                selectedPaletteId = null,
                currentPage = 0, // <--- YE LINE ADD KAREIN
                errorMessage = null,
                isGenerating = false,
                generatedImages = emptyList()
            )
        }
        currentDraftId = null
    }

    @OptIn(ExperimentalTime::class, ExperimentalEncodingApi::class)
    fun onRoomEvent(event: RoomEvent) {
        when (event) {
            is RoomEvent.OnSearchQueryChange -> {
                _state.value = _state.value.copy(searchQuery = event.query)
                applyFiltersAndSearch()
            }

            is RoomEvent.SetImageBytes -> {
                viewModelScope.launch {
                    val savedPath = saveImageBytes(
                        bytes = event.bytes,
                        fileName = "original_${generateTaskId()}.jpg"
                    )
                    _state.update {
                        it.copy(
                            selectedImageBytes = event.bytes,
                            selectedFileName = savedPath ?: event.fileName,
                            selectedImage = "image_picked",
                            currentTaskId = null,
                            isFetchingImages = false
                        )
                    }
                }
            }

            is RoomEvent.GoToPage -> {
                _state.update { it.copy(currentPage = event.page) }
            }

            is RoomEvent.SetEditMode -> {
                _state.update { it.copy(isEditMode = event.isEdit) }
            }
            is RoomEvent.OnApplyFilters -> {
                val tempFilter = _state.value.tempFilterState
                val count = calculateFilterCount(tempFilter)
                _state.value = _state.value.copy(
                    filterState = tempFilter,
                    filterCount = count,
                    tempFilterCount = count,
                    showFilterSheet = false
                )
                applyFiltersAndSearch()
            }

            RoomEvent.OnFilterClick -> {
                _state.value = _state.value.copy(
                    showFilterSheet = true,
                    tempFilterState = _state.value.filterState,
                    tempFilterCount = _state.value.filterCount
                )
            }

            RoomEvent.OnResetLoading -> {
                _state.update { it.copy(isLoading = false) }
            }

            RoomEvent.OnDismissFilterSheet -> {
                _state.value = _state.value.copy(
                    showFilterSheet = false,
                    tempFilterCount = _state.value.filterCount
                )
            }

            RoomEvent.OnClearFilters -> {
                _state.value = _state.value.copy(
                    tempFilterState = FilterState(),
                    tempFilterCount = 0
                )
            }

            is RoomEvent.OnTempFilterChange -> {
                val newCount = calculateFilterCount(event.filterState)
                _state.value = _state.value.copy(
                    tempFilterState = event.filterState,
                    tempFilterCount = newCount
                )
            }

            is RoomEvent.OnToggleFilterSection -> {
                _state.value = when (event.section) {
                    FilterSection.ROOM_TYPE -> _state.value.copy(
                        expandedRoomType = !_state.value.expandedRoomType
                    )

                    FilterSection.STYLE -> _state.value.copy(
                        expandedStyle = !_state.value.expandedStyle
                    )

                    FilterSection.COLOR -> _state.value.copy(
                        expandedColor = !_state.value.expandedColor
                    )

                    FilterSection.FORMAT -> _state.value.copy(
                        expandedFormat = !_state.value.expandedFormat
                    )

                    FilterSection.PRICE -> _state.value.copy(
                        expandedPrice = !_state.value.expandedPrice
                    )
                }
            }
            is RoomEvent.SetImage -> {
                _state.value = _state.value.copy(
                    selectedImage = event.imageDetails.uri
                )
                println("DEBUG_VM: SelectedImage URI = ${event.imageDetails.uri}")
            }

            // Pagination events
            is RoomEvent.OnPageChange -> {
                _state.value = _state.value.copy(currentPage = event.page)
            }

            RoomEvent.OnNextPage -> {
                val currentPage = _state.value.currentPage
                if (currentPage < _state.value.pageCount - 1) {
                    _state.value = _state.value.copy(currentPage = currentPage + 1)
                }
            }

            RoomEvent.OnPreviousPage -> {
                val currentPage = _state.value.currentPage
                if (currentPage > 0) {
                    _state.value = _state.value.copy(currentPage = currentPage - 1)
                }
            }

            // Room type / style / palette selection events
            is RoomEvent.OnRoomTypeSelected -> {
                _state.value = _state.value.copy(selectedRoomType = event.roomType)
            }

            is RoomEvent.OnRoomSearchQueryChange -> {
                _state.value = _state.value.copy(roomSearchQuery = event.query)
            }

            is RoomEvent.OnRoomSearchExpandedChange -> {
                _state.value = _state.value.copy(isRoomSearchExpanded = event.isExpanded)
            }

            is RoomEvent.OnStyleSelected -> {
                val style = _state.value.availableInteriorStyles.firstOrNull { it.id == event.styleId }
                _state.update {
                    it.copy(
                        selectedStyleId = event.styleId,
                        selectedStyleName = style?.name
                    )
                }
            }

            is RoomEvent.OnStyleSearchQueryChange -> {
                _state.value = _state.value.copy(styleSearchQuery = event.query)
            }

            is RoomEvent.OnStyleSearchExpandedChange -> {
                _state.value = _state.value.copy(isStyleSearchExpanded = event.isExpanded)
            }

            is RoomEvent.OnPaletteSelected -> {
                _state.value = _state.value.copy(selectedPaletteId = event.paletteId)
            }

            is RoomEvent.OnGenerateClick -> {
                val newTaskId = generateTaskId()
                _tasksStatus.update { it + (newTaskId to GenerationStatus.RUNNING) }
                _tasksProgress.update { it + (newTaskId to 0.0f) }
                val capturedPrompt = buildPromptFromState(_state.value)
                val capturedImageBytes = event.imageBytes

                // Credits check (Aapka existing logic)
                val currentCredits = if (authViewModel.state.value.email.isNullOrBlank()) {
                    authViewModel.guestSession.value?.totalCredits ?: 0
                } else {
                    authViewModel.state.value.totalCredits
                }

                if (currentCredits <= 0) {
                    viewModelScope.launch { _uiEvent.emit(ShowError("Not enough credits")) }
                    return
                }

                // Task count barhayein taake UI mein loader dikhayi de
                _state.update { it.copy(
                    activeTasksCount = it.activeTasksCount + 1,
                    isFetchingImages = true,
                    isGenerating = true,
                    errorMessage = null,
                    generatedCount = 3,
                    generatedImagesEntity = it.generatedImagesEntity + RecentGeneratedEntity(
                        imageUrls = emptyList(),
                        localPaths = emptyList(),
                        bundleId = newTaskId
                    )
                )}


                generationJobs[newTaskId] = viewModelScope.launch {
                    try {
                        // Credits spend logic... (Existing code)
                        val email = authViewModel.state.value.email ?: ""
                        val deviceId = getDeviceId()
                        val creditResult = if (email.isBlank()) spendCreditsUseCaseGuest(deviceId, 1) else spendCreditsUseCase(email, deviceId, 1)

                        if (creditResult.isFailure) {
                            _state.update { it.copy(activeTasksCount = (it.activeTasksCount - 1).coerceAtLeast(0)) }
                            return@launch
                        }

                        // Generation request (captured variables ke saath)
                        val base64Image = withContext(Dispatchers.Default) {
                            "data:image/jpeg;base64,${capturedImageBytes.toBase64()}"
                        }
                        val request = GenerateRoomRequest(initImage = base64Image, prompt = capturedPrompt)

                        // 3 Parallel Calls
                        // Instead of awaitAll (which cancels all if one fails):
                        val results = listOf(
                            async {
                                try { generateRoomUseCase(request) }
                                catch (e: CancellationException) { throw e } // rethrow coroutine cancellation
                                catch (e: Exception) { ResultState.Error(e.message ?: "Failed") }
                            },
                            async {
                                try { generateRoomUseCase(request) }
                                catch (e: CancellationException) { throw e }
                                catch (e: Exception) { ResultState.Error(e.message ?: "Failed") }
                            },
                            async {
                                try { generateRoomUseCase(request) }
                                catch (e: CancellationException) { throw e }
                                catch (e: Exception) { ResultState.Error(e.message ?: "Failed") }
                            }
                        ).awaitAll()
                        results.forEachIndexed { index, result ->
                            if (result is ResultState.Success) {
                                println("DEBUG_ETA: Task $index eta = ${result.data.eta}")
                                println("DEBUG_ETA: Task $index fetchUrl = ${result.data.fetchUrl}")
                                println("DEBUG_ETA: Task $index isProcessing = ${result.data.isProcessing}")
                            }
                        }
                        _state.update { it.copy(
                            isGenerating = false,
                            isFetchingImages = true,
                            generatedImagesEntity = listOf(
                                RecentGeneratedEntity(
                                    imageUrls = emptyList(),
                                    localPaths = emptyList(),
                                    bundleId = newTaskId
                                )
                            )
                        )}

                        val maxEta = results.filter { it is ResultState.Success }
                            .map { (it as ResultState.Success).data.eta ?: 30 }
                            .maxOrNull() ?: 30
                        // ✅ Har task ka apna independent timer launch karein
                        launch {
                            for (seconds in 1..200) {
                                kotlinx.coroutines.delay(1000L)
                                val progress = (seconds.toFloat() / maxEta.toFloat()).coerceAtMost(0.99f)
                                _tasksProgress.update { it + (newTaskId to progress) }
                                val currentStatus = _tasksStatus.value[newTaskId]
                                if (currentStatus == GenerationStatus.SUCCESS || !_tasksStatus.value.containsKey(newTaskId)) break
                            }
                        }

                        val allGeneratedUrls = mutableListOf<String>()
                        val allLocalPaths = mutableListOf<String>()
                        var successCount = 0

                        // Fetching Logic
                        results.forEachIndexed { index, result ->
                            if (result is ResultState.Success) {
                                val response = result.data
                                if (response.isProcessing && response.fetchUrl != null) {
                                    startImageTrackingUseCase(newTaskId, (maxEta * 0.8).toLong(), results.filter { it is ResultState.Success }.mapNotNull { (it as ResultState.Success).data.fetchUrl })
                                    launch {
                                        delay(index * 2000L)
                                        var retries = 0
                                        while (retries < 30) {
                                            val fetchResult = fetchGeneratedRoomUseCase(response.fetchUrl)
                                            if (fetchResult is ResultState.Success) {
                                                val data = fetchResult.data
                                                if (!data.isProcessing && data.availableImages.isNotEmpty()) {
                                                    val imageUrl = data.availableImages.first()
                                                    val localPath = downloadAndCacheImage(imageUrl, "room_${newTaskId}_$index.jpg")

                                                    listLock.withLock {
                                                        allGeneratedUrls.add(imageUrl)
                                                        allLocalPaths.add(localPath ?: "")
                                                        successCount++
                                                    }

                                                    _state.update { s ->
                                                        s.copy(
                                                            generatedImagesEntity = listOf(
                                                                RecentGeneratedEntity(
                                                                    imageUrls = allGeneratedUrls.toList(),
                                                                    localPaths = allLocalPaths.toList(),
                                                                    bundleId = newTaskId
                                                                )
                                                            )
                                                        )
                                                    }
                                                    if (successCount >= 3) {
                                                        // Bundle Complete! Save to DB
                                                        val     bundleToSave = RecentGeneratedEntity(
                                                            imageUrls = allGeneratedUrls.toList(),
                                                            localPaths = allLocalPaths.toList(),
                                                            bundleId = newTaskId,
                                                            originalImagePath = _state.value.selectedFileName,
                                                            prompt = capturedPrompt,
                                                            roomType = _state.value.selectedRoomType,
                                                            style = _state.value.selectedStyleName,
                                                            paletteId = _state.value.selectedPaletteId
                                                        )
                                                        println("DEBUG_BUNDLE_SAVE: originalImagePath = ${bundleToSave.originalImagePath}")
                                                        println("DEBUG_BUNDLE_SAVE: prompt = ${bundleToSave.prompt}")
                                                        println("DEBUG_BUNDLE_SAVE: roomType = ${bundleToSave.roomType}")
                                                        recentGeneratedRepository.saveGenerated(bundleToSave)
                                                        if (NotificationManager.isAppInBackground()) { NotificationManager.notifyIfBackground() }

                                                        // Clean up this task
                                                        _tasksStatus.update { it + (newTaskId to GenerationStatus.SUCCESS) }
                                                        _state.update { s -> s.copy(
                                                            activeTasksCount = (s.activeTasksCount - 1).coerceAtLeast(0),
                                                            isFetchingImages = s.activeTasksCount > 1,
                                                        )}
                                                        delay(5000L)
                                                        _tasksStatus.update { it - newTaskId }
                                                        _tasksProgress.update { it - newTaskId }
                                                    }
                                                    break
                                                }
                                            }
                                            retries++
                                            kotlinx.coroutines.delay(5000L)
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _state.update { it.copy(activeTasksCount = (it.activeTasksCount - 1).coerceAtLeast(0)) }
                    _uiEvent.emit(ShowError("Generation failed: ${e.message}"))
                }
                }
            }
            is RoomEvent.OnCancelGeneration -> {
                val taskId = event.taskId

                // Sirf is task ki job cancel karo
                generationJobs[taskId]?.cancel()
                generationJobs.remove(taskId)

                // Sirf is task ko maps se remove karo
                _tasksProgress.update { it - taskId }
                _tasksStatus.update { it - taskId }

                // Sirf is task ki entity remove karo, baaki rehne do
                _state.update {
                    it.copy(
                        activeTasksCount = (it.activeTasksCount - 1).coerceAtLeast(0),
                        generatedImagesEntity = it.generatedImagesEntity.filter { e -> e.bundleId != taskId }
                    )
                }
            }

            is RoomEvent.OnGenerationComplete -> {
                // Sirf UI state reset karo (selection etc.)
                // Generating bundles mat chhuao
                _state.update {
                    it.copy(
                        selectedImageBytes = null,
                        selectedFileName = null,
                        selectedImage = null,
                        generatedImages = emptyList(),
                        isGenerating = false,
                        selectedRoomType = null,
                        isFetchingImages = it.activeTasksCount > 0, // agar aur tasks hain to true rakho
                        generatedCount = 0,
                        selectedStyleName = null,
                        selectedPaletteId = null,
                        currentPage = 0
                        // ❌ generatedImagesEntity = emptyList() -- yeh hatao
                    )
                }
                // ❌ _tasksProgress.update { emptyMap() } -- yeh bhi hatao
            }
            is RoomEvent.ShowSelectedBundle -> {
                _state.update {
                    it.copy(
                        generatedImagesEntity   = event.bundle,
                        isGenerating = false,
                    )
                }
            }

            is RoomEvent.ClearNavigateToLogin -> {
                _state.update { it.copy(navigateToLogin = false) }
            }

            else -> {}
        }
    }
    private fun applyFiltersAndSearch() {
        val state = _state.value
        var filtered = state.allRooms

        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter { room ->
                room.roomType.contains(state.searchQuery, ignoreCase = true)
            }
        }

        if (state.filterState.selectedRoomTypes.isNotEmpty() && !state.filterState.selectedRoomTypes.contains("All")) {
            filtered = filtered.filter { room ->
                state.filterState.selectedRoomTypes.contains(room.roomType)
            }
        }

        if (state.filterState.selectedStyles.isNotEmpty() && !state.filterState.selectedStyles.contains("All")) {
            filtered = filtered.filter { room ->
                state.filterState.selectedStyles.any { style ->
                    room.roomStyle.contains(style, ignoreCase = true)
                }
            }
        }

        if (state.filterState.selectedColors.isNotEmpty()) {
            // Pehle select ki hui IDs se actual color palettes nikaalein
            val selectedPalettes = state.filterColors
                .filter { it.id in state.filterState.selectedColors }
                .map { it.colors }

            filtered = filtered.filter { room ->
                selectedPalettes.any { it == room.colors }
            }
        }

        _state.value = _state.value.copy(filteredRooms = filtered)
    }
    private fun calculateFilterCount(filterState: FilterState): Int {
        var count = 0
        if (filterState.selectedRoomTypes.isNotEmpty() && !filterState.selectedRoomTypes.contains("All")) count++
        if (filterState.selectedStyles.isNotEmpty() && !filterState.selectedStyles.contains("All")) count++
        if (filterState.selectedColors.isNotEmpty()) count++
        if (filterState.selectedFormats.isNotEmpty() && !filterState.selectedFormats.contains("All")) count++
        if (filterState.selectedPrices.isNotEmpty()) count++
        return count
    }
    private fun extractDynamicFilters(rooms: List<com.webscare.interiorismai.domain.model.RoomUi>) {
        val roomTypes = rooms.map { it.roomType }
            .filter { it.isNotBlank() }
            .distinct()

        val uniqueStyles = rooms.map { room ->
            com.webscare.interiorismai.domain.model.InteriorStyle(
                name = room.roomStyle,
                image = room.imageUrl,
                id = room.id
            )
        }.distinctBy { it.name }

        val colorPalettesForFilter = rooms.map { room ->
        ColorPalette(
                colors = room.colors,
                id = room.id,
            name = room.paletteName,
            )
        }.distinctBy { it.colors }

        _state.update { currentState ->
            currentState.copy(
                availableRoomTypes = roomTypes,
                availableStyles = uniqueStyles,
                filterColors = colorPalettesForFilter
            )
        }
    }
    fun getRooms() {
        println("DEBUG_VM: 1. getRooms() called")
        viewModelScope.launch {
            executeApiCall(
                updateState = { result ->
                    _state.update { it.copy(getRoomsResponse = result) }                },
                apiCall = {
                    println("DEBUG_VM: 2. Launching API Call...")
                    roomsRepository.getRoomsList()
                },
                onSuccess = { response ->
                    println("DEBUG_VM: 3. Success! Rooms Count: ${response.rooms.size}")
                    if (response.success) {
                        val finalList = response.rooms.map { it.toUi() }
                        val trending = finalList.filter { it.isTrending == 1 }
                        _state.update { currentState ->
                            currentState.copy(
                                trendingRooms = trending,
                                allRooms = finalList,
                                filteredRooms = finalList,
                                isLoading = false
                            )
                        }
                        extractDynamicFilters(finalList)
                    } else {
                        println("DEBUG_VM: 4. API Success was False.")
                        _state.update { it.copy(isLoading = false) }
                        viewModelScope.launch { _uiEvent.emit(ShowError("Something went wrong")) }
                    }
                },
                onError = { errorMessage ->
                    println("DEBUG_VM: 5. API Error: $errorMessage")
                    _state.update { it.copy(isLoading = false) }
                    viewModelScope.launch { _uiEvent.emit(ShowError(errorMessage)) }
                }
            )
        }
    }

    private fun loadInteriorsData() {
        viewModelScope.launch {
            try {
                println("DEBUG_INTERIORS: Calling styles...")
                val styles = interiorsRepository.getStyles()
                println("DEBUG_INTERIORS: Styles done = ${styles.styles.size}")

                println("DEBUG_INTERIORS: Calling types...")
                val types = interiorsRepository.getTypes()
                println("DEBUG_INTERIORS: Types done = ${types.types.size}")

                val colorsResponse = interiorsRepository.getColors()
                println("DEBUG_INTERIORS: ColorsPalate done = ${colorsResponse.colors.size}")

                _state.update {
                    it.copy(
                        availableInteriorStyles = styles.styles,
                        availableStylesString = styles.styles.map { style -> style.name },
                        availableRoomTypes = types.types.map { type -> type.name },
                        availableInteriorTypes = types.types,
                        availableColors = colorsResponse.colors.map { colorApi ->
                            ColorPalette(
                                id = colorApi.id,
                                name = colorApi.name,
                                colors = colorApi.colors.map { hex -> parseHexToColor(hex) }
                            )
                        },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                println("DEBUG_INTERIORS: ERROR -> ${e.message}")
                println("DEBUG_INTERIORS: ERROR CAUSE -> ${e.cause}")
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    fun parseHexToColor(hex: String): Color {
        val cleanHex = hex.removePrefix("#")
        return when (cleanHex.length) {
            6 -> Color(0xFF000000 or cleanHex.toLong(16)) // RGB
            8 -> Color(cleanHex.toLong(16)) // ARGB
            else -> Color.Gray // Fallback
        }
    }

    private fun buildPromptFromState(state: RoomUiState): String {
        val roomType = state.selectedRoomType?.ifBlank { "living room" } ?: "living room"
        val style = state.selectedStyleName?.ifBlank { "modern" } ?: "modern"

        // 1. Pehle selected palette dhoondein
        val selectedPalette = state.availableColors.firstOrNull { it.id == state.selectedPaletteId }

        println("🎨 PALETTE: selectedPaletteId = ${state.selectedPaletteId}")
        println("🎨 PALETTE: selectedPalette = $selectedPalette")
        println("🎨 PALETTE: availableColors count = ${state.availableColors.size}")
        // 2. Colors ko transform karein (Color object -> "FFFFFF")
        val cleanHexColors = selectedPalette?.colors?.map { colorValue ->
            when (colorValue) {
                is Color -> colorValue.toRawHex() // Agar Compose Color hai
                is String -> cleanColorString(colorValue) // Agar String hai
                else -> "FFFFFF"
            }
        } ?: listOf("neutral tones")

        val colorPaletteString = cleanHexColors.joinToString(", ")

        return """
Ultra-photorealistic architectural interior photography of a $style $roomType. This is a REDESIGN of an existing room — the camera captures the same space, same walls, same ceiling height, same floor, same window positions, same door positions. Nothing structural has moved or changed. Only surfaces, furniture, and decor have been replaced.

ARCHITECTURAL PRESERVATION — ABSOLUTE
Every wall, ceiling, floor, window, and door remains IDENTICAL to the input image. No new openings, no removed elements, no structural changes whatsoever. Room proportions, depth, and spatial layout are pixel-accurate to the original. The camera angle and perspective must match the original room photograph exactly. Ceiling height, room width, and depth are unchanged.

COLOR PALETTE — HIGHEST PRIORITY — ABSOLUTE ENFORCEMENT
Allowed colors only $colorPaletteString. This is the MOST IMPORTANT rule — color palette overrides ALL other requirements including furniture, realism, and completeness. ALL surfaces including walls, floor, and ceiling trim must use $colorPaletteString tones only. ALL furniture including upholstery, frames, and legs must be within $colorPaletteString — if furniture cannot be found in palette colors, leave that space empty rather than break the palette. ALL decor including cushions, rugs, curtains, and artwork must reflect $colorPaletteString. Tonal variations are allowed — use lighter and darker shades of $colorPaletteString for depth. Any color outside $colorPaletteString family is strictly forbidden, including accidental greys, reflected hues from lighting, and neutral drift. If palette includes black, use true deep black only such as matte black, jet black, or near-black charcoal — the scene must appear dark and rich, NOT grey, NOT desaturated, NOT washed out. If palette is a single color, build depth through tints and shades of that one color only. NO EXCEPTIONS — color compliance is non-negotiable.

FURNITURE GUIDELINES — SECONDARY TO COLOR PALETTE
Furniture should furnish the room appropriately, but ONLY if pieces exist within $colorPaletteString. Color compliance overrides furniture completeness. Recommended furniture by room type:

If $roomType is Living Room: a full sofa or sectional, one or two armchairs, a coffee table, a side table, a TV console or media unit, and a floor lamp — ALL in $colorPaletteString only.

If $roomType is Bedroom: a full bed with headboard, two nightstands, a dresser or wardrobe, a bench or ottoman at foot of bed, and bedside lamps — ALL in $colorPaletteString only.

If $roomType is Dining Room: a dining table sized for the room, a full set of dining chairs, a sideboard or buffet table, and a pendant light above the table — ALL in $colorPaletteString only.

If $roomType is Kitchen: counter stools or breakfast bar seating, open shelving styled with dishes and plants, and a kitchen island if space allows — ALL in $colorPaletteString only.

If $roomType is Home Office: a large desk, an ergonomic chair, a bookshelf or shelving unit, a desk lamp, and storage units — ALL in $colorPaletteString only.

If $roomType is Bathroom: a styled vanity with mirror, towel rack with folded towels, a small plant, and bath accessories on counter — ALL in $colorPaletteString only.

Include only furniture that exists in palette colors. An incomplete but color-accurate room is BETTER than a fully furnished room with wrong colors. Wall decor and accessories are secondary and must only fill remaining visual space after furniture is placed.

FURNITURE — REAL INTERIOR DESIGN STANDARDS
Furniture must look like it belongs in a real, lived-in $style $roomType and MUST use $colorPaletteString exclusively. Scale is proportional to the room with no oversized or undersized pieces. Every piece sits firmly on the floor with zero floating objects. Legs of chairs, tables, and sofas must cast proper shadows and contact the floor. Arrangement follows real interior design logic: sofa faces focal point such as fireplace, TV, or window; coffee table is centered and reachable from seating; bed is centered on main wall with equal nightstands; dining chairs are evenly spaced and tucked into table. Materials must be premium and physically accurate — velvet shows micro-texture and sheen variation, wood shows grain and natural variation, marble shows veining and reflective depth, metal shows brushed or polished surface quality, glass shows transparency and edge refraction. All materials MUST stay within $colorPaletteString. No clutter, no random objects, no decorative excess.

WALL DECOR — REQUIRED AFTER FURNITURE IS COMPLETE
Walls must never be bare or empty. Once furniture is placed, every visible wall surface must include at least one or two tasteful, style-appropriate decorative elements chosen from the following based on $style: large framed artwork or canvas paintings, curated gallery walls with 3 to 5 frames in matching or complementary sizes, architectural wall panels or textured feature walls, floating shelves with books, small sculptures, and plants, decorative mirrors with style-appropriate frames, wall sconces or mounted lighting fixtures, subtle wallpaper or textured wall finish, or woven wall hangings and tapestries. All wall decor must strictly use $colorPaletteString tones. Frames, artwork backgrounds, shelf contents, and mirror frames must all harmonize with the palette. Wall decor must be proportional to wall size — not too small and not overwhelming.

ROOM ACCESSORIES — LIVED-IN BUT CURATED
The room must feel complete, warm, and lived-in with carefully selected accessories that add personality without creating clutter. Include a small selection of the following based on $style and $roomType: a ceramic or sculptural vase with dried or fresh flowers on a side table or console, 2 to 4 decorative throw pillows on sofa or bed in coordinating $colorPaletteString tones, a neatly folded throw blanket draped over sofa or armchair, a styled coffee table or nightstand with 2 to 3 objects such as a tray, candle, small book stack, or decorative object, a potted indoor plant or small tree in a style-appropriate planter, a decorative rug that grounds the seating or sleeping area and stays within $colorPaletteString, table lamp or floor lamp that adds warm layered lighting, and small framed photo or art piece on a shelf or side table. All accessories must strictly conform to $colorPaletteString. No excess, no randomness — every object must feel intentionally placed by a professional interior stylist.

LIGHTING — CINEMATIC AND NATURAL
Soft, diffused natural light enters ONLY through existing windows in the same positions as the input image. Light creates gentle gradients, soft shadows, and warm depth. No harsh highlights, no overexposed surfaces, no blown-out areas. Lighting enhances $colorPaletteString and must NOT shift, wash out, or contaminate the palette. Global illumination allows ambient bounce light to fill shadows naturally. Table lamps and floor lamps emit soft warm glows that layer with natural light to create depth and intimacy. No artificial studio lighting unless $style specifically requires it.

PHOTOGRAPHY QUALITY
Resolution is 8K ultra-sharp. Lens is 24-35mm wide angle with slight perspective correction. Depth of field keeps foreground slightly soft and mid-room in sharp focus. No lens distortion, no vignette, no post-processing artifacts. Composition follows rule of thirds with a clear focal point and balanced negative space. Style reference is Architectural Digest, Elle Decor, and Dezeen editorial photography.

PRIORITY ORDER (STRICT):
1. Color palette enforcement (HIGHEST PRIORITY — OVERRIDES EVERYTHING)
2. Architectural preservation
3. Furniture placement (only with palette colors)
4. Styling and realism

Color palette MUST override furniture requirements, realism, and material accuracy. If conflict occurs, color wins every time.

ABSOLUTE FORBIDDEN LIST
No structural changes to walls, ceiling, floor, windows, or doors. No bare or completely empty walls. No floating furniture or objects not touching surfaces. NO COLORS OUTSIDE $colorPaletteString — every single pixel must fall within the palette. Any deviation is considered incorrect. No furniture in colors outside the specified palette, even if it means fewer pieces or empty spaces. No "close enough" color matching — palette colors ONLY. No text, watermarks, logos, or typography anywhere. No cartoon, illustration, CGI-render, or painterly look. No duplicate objects or copy-paste repetition. No misaligned or incorrectly scaled furniture. No excessive clutter or more than 6 to 8 accessories total in the scene. No grey substitutes for black palette. No lighting-induced color contamination. No randomly placed objects that serve no design purpose. No wall decor added before furniture is complete.""".trimIndent()
    }

    // Helper to clean existing strings
    private fun cleanColorString(rawColor: String): String {
        return if (rawColor.contains("Color")) {
            // Agar galti se "Color(1.0...)" string ban chuka hai, toh usse handle karein
            "FFFFFF"
        } else {
            rawColor.replace("#", "").trim()
        }
    }

    // Compose Color to "FFFFFF"
    fun Color.toRawHex(): String {
        val r = (this.red * 255).toInt().coerceIn(0, 255)
        val g = (this.green * 255).toInt().coerceIn(0, 255)
        val b = (this.blue * 255).toInt().coerceIn(0, 255)

        // Har component ko 2-digit hex string mein badlein aur join karein
        return listOf(r, g, b).joinToString("") {
            it.toString(16).padStart(2, '0').uppercase()
        }
    }

    private fun initBilling() {
        billingHelper = com.webscare.interiorismai.billing.BillingHelper(
            onProductsLoaded = { products ->
                _state.update { it.copy(billingProducts = products) }
            },
            onPurchaseComplete = { productId, credits ->
                val email = authViewModel.state.value.email ?: ""
                if (email.isNotBlank()) {
                    viewModelScope.launch {
                        val result = addCreditsUseCase(email, credits)
                        result.onSuccess { response ->
                            _state.update {
                                it.copy(
                                    isPurchasing = false,
                                    purchaseSuccess = "Credits added: ${response.purchasedCredits}"
                                )
                            }
                            authViewModel.onAuthEvent(RegisterEvent.FetchUserDetails)
                        }.onFailure { error ->
                            _state.update {
                                it.copy(
                                    isPurchasing = false,
                                    purchaseError = error.message ?: "Transaction failed"
                                )
                            }
                        }
                    }
                }
            },
            onPurchaseCancelled = {  // ✅ add karo
                _state.update { it.copy(isPurchasing = false) }
            }
        )
        billingHelper?.startConnection()
    }

    fun onSubscriptionEvent(event: RoomEvent) {
        println("🟡 onSubscriptionEvent called: $event")
        when (event) {
            is RoomEvent.OnPurchasePlan -> {
                println("🔴 STEP 1: OnPurchasePlan hit")
                println("🔴 STEP 2: email = '${authViewModel.state.value.email}'")
                println("🔴 STEP 3: navigateToLogin = ${_state.value.navigateToLogin}")
                println("🔴 STEP 4: isPurchasing = ${_state.value.isPurchasing}")
                println("🔴 STEP 5: billingHelper = $billingHelper")
                println("🔴 STEP 6: billingProducts = ${_state.value.billingProducts.size}")



                // ✅ EMAIL ki jagah LOGIN flag check karo
                val email = authViewModel.state.value.email

                if (email.isNullOrBlank()) {
                    println("🔴 STEP 7: EMAIL BLANK - going to login")

                    _state.update { it.copy(navigateToLogin = true) }
                    return
                }
                println("🔴 STEP 7: EMAIL OK - launching purchase")


                _state.update { it.copy(isPurchasing = true, purchaseError = null) }
                billingHelper?.launchPurchase(event.productId)
                println("🔴 STEP 8: launchPurchase called with ${event.productId}")

            }
            RoomEvent.ClearPurchaseState -> {
                _state.update { it.copy(purchaseSuccess = null, purchaseError = null) }
            }
            else -> onRoomEvent(event)
        }
    }

    override fun onCleared() {
        super.onCleared()
        billingHelper?.disconnect()
    }    // RoomsViewModel.kt
    fun deleteRecentImage(id: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            println("DEBUG_VM: Attempting to delete image with ID: $id")
            try {
                recentGeneratedRepository.deleteGeneratedById(id)
                println("🗑️ DELETE: ID $id removed from DB")
                _uiEvent.emit(CommonUiEvent.ShowError("Deleted successfully"))
                onDeleted() // Ye callback UI ko band karne ke liye hai
            } catch (e: Exception) {
                _uiEvent.emit(CommonUiEvent.ShowError("Delete failed"))
            }
        }
    }
    @OptIn(ExperimentalTime::class)
    fun redoGeneration(
        entity: RecentGeneratedEntity,
        indexToReplace: Int,
        onResult: () -> Unit
    ) {
        viewModelScope.launch {
            val latestEntity = _state.value.generatedImagesEntity
                .firstOrNull { it.bundleId == entity.bundleId }
                ?: entity
            // 1. Original image bytes lo
            val imageBytes = latestEntity.originalImagePath?.let {
                try { readLocalFile(it) } catch (e: Exception) { null }
            }

            if (imageBytes == null || imageBytes.isEmpty()) {
                _uiEvent.emit(ShowError("Original image not found"))
                return@launch
            }

            // 2. Task ID banao — FIFO queue mein add karo
            val taskId = generateTaskId()
            _taskQueue.update { it + taskId }
            _tasksStatus.update { it + (taskId to GenerationStatus.RUNNING) }
            _tasksProgress.update { it + (taskId to 0f) }

            // 3. Result screen pe jao
            onResult()

            // 4. Generation
            generationJobs[taskId] = viewModelScope.launch {
                try {
                    val email = authViewModel.state.value.email ?: ""
                    val deviceId = getDeviceId()
                    val creditResult = if (email.isBlank()) {
                        spendCreditsUseCaseGuest(deviceId, 1)
                    } else {
                        spendCreditsUseCase(email, deviceId, 1)
                    }

                    if (creditResult.isFailure) {
                        _taskQueue.update { it - taskId }
                        _tasksStatus.update { it - taskId }
                        _tasksProgress.update { it - taskId }
                        _uiEvent.emit(ShowError("Not enough credits"))
                        return@launch
                    }
                    authViewModel.fetchUserDetails()


                    // ✅ Entity ka prompt use karo — current state ka nahi
                    val base64Image = "data:image/jpeg;base64,${imageBytes.toBase64()}"
                    val prompt = latestEntity.prompt ?: buildPromptFromState(_state.value)
                    val request = GenerateRoomRequest(initImage = base64Image, prompt = prompt)

                    val result = generateRoomUseCase(request)

                    if (result is ResultState.Success && result.data.fetchUrl != null) {
                        val eta = result.data.eta ?: 30

                        // Progress timer
                        launch {
                            for (seconds in 1..200) {
                                kotlinx.coroutines.delay(1000L)
                                val progress = (seconds.toFloat() / eta.toFloat()).coerceAtMost(0.99f)
                                _tasksProgress.update { it + (taskId to progress) }
                                val status = _tasksStatus.value[taskId]
                                if (status == GenerationStatus.SUCCESS || !_tasksStatus.value.containsKey(taskId)) break
                            }
                        }

                        var retries = 0
                        while (retries < 30) {
                            val fetchResult = fetchGeneratedRoomUseCase(result.data.fetchUrl!!)
                            if (fetchResult is ResultState.Success) {
                                val data = fetchResult.data
                                if (!data.isProcessing && data.availableImages.isNotEmpty()) {
                                    val newUrl = data.availableImages.first()
                                    val newPath = downloadAndCacheImage(newUrl, "redo_${taskId}_$indexToReplace.jpg")
                                    val currentEntity = _state.value.generatedImagesEntity
                                        .firstOrNull { it.bundleId == entity.bundleId }
                                        ?: entity

                                    // ✅ Sirf target index replace karo
                                    val    updatedUrls = currentEntity.imageUrls.toMutableList().apply {
                                        if (size > indexToReplace) set(indexToReplace, newUrl)
                                    }
                                    val updatedPaths = currentEntity.localPaths.toMutableList().apply {
                                        if (size > indexToReplace) set(indexToReplace, newPath ?: "")
                                    }

                                    val updatedBundle = currentEntity.copy(
                                        imageUrls = updatedUrls,
                                        localPaths = updatedPaths
                                    )

                                    // DB update
                                    recentGeneratedRepository.saveGenerated(updatedBundle)

                                    // State update
                                    _state.update { s ->
                                        s.copy(
                                            generatedImagesEntity = s.generatedImagesEntity.map { e ->
                                                if (e.bundleId == entity.bundleId) updatedBundle else e
                                            }
                                        )
                                    }

                                    // SUCCESS
                                    _tasksProgress.update { it + (taskId to 1f) }
                                    _tasksStatus.update { it + (taskId to GenerationStatus.SUCCESS) }

                                    delay(5000L)
                                    _taskQueue.update { it - taskId }
                                    _tasksStatus.update { it - taskId }
                                    _tasksProgress.update { it - taskId }
                                    generationJobs.remove(taskId)
                                    break
                                }
                            }
                            retries++
                            kotlinx.coroutines.delay(5000L)
                        }
                    }
                } catch (e: Exception) {
                    _taskQueue.update { it - taskId }
                    _tasksStatus.update { it - taskId }
                    _tasksProgress.update { it - taskId }
                    generationJobs.remove(taskId)
                }
            }
        }
    }
    fun startGlobalTimer() {
        timerJob?.cancel()
        _generationProgress.value = 0f
        val maxEtaFromApi = _state.value.imageEtaSeconds.maxOrNull() ?: 30
        timerJob = viewModelScope.launch {
            for (seconds in 1..200) {
                kotlinx.coroutines.delay(1000L)
                val currentProgress = seconds.toFloat() / maxEtaFromApi.toFloat()
                if (currentProgress >= 0.99f) {
                    _generationProgress.value = 0.99f
                } else {
                    _generationProgress.value = currentProgress
                }

                if (!_state.value.isFetchingImages) {
                    _generationProgress.value = 1f
                    break
                }
            }
        }
    }
    fun prepareForNewGeneration() {
        _state.update { it.copy(isFetchingImages = false) }
    }

    fun deleteImageFromBundle(entity: RecentGeneratedEntity, imageIndex: Int, onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                val updatedUrls = entity.imageUrls.toMutableList().apply { removeAt(imageIndex) }
                val updatedPaths = entity.localPaths.toMutableList().apply { removeAt(imageIndex) }

                val updatedBundle = entity.copy(
                    imageUrls = updatedUrls,
                    localPaths = updatedPaths
                )

                if (updatedUrls.isEmpty()) {
                    recentGeneratedRepository.deleteGeneratedById(entity.id)
                } else {
                    recentGeneratedRepository.saveGenerated(updatedBundle)
                }
                _state.update { s ->
                    s.copy(
                        generatedImagesEntity = s.generatedImagesEntity.map { e ->
                            if (e.bundleId == entity.bundleId) updatedBundle else e
                        }
                    )
                }
                onDeleted()
            } catch (e: Exception) {
                _uiEvent.emit(CommonUiEvent.ShowError("Delete failed"))
            }
        }
    }

}
