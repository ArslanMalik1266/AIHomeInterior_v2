package com.webscare.interiorismai.ui.CreateAndExplore

import kotlinx.serialization.Serializable
import com.webscare.interiorismai.data.local.entities.RecentGeneratedEntity
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.GenerateRoomResponse
import com.webscare.interiorismai.domain.model.InteriorStyle
import com.webscare.interiorismai.domain.model.InteriorType
import com.webscare.interiorismai.domain.model.RoomUi
import com.webscare.interiorismai.domain.model.Rooms
import com.webscare.interiorismai.ui.Generate.UiScreens.ColorPalette
import com.webscare.interiorismai.utils.GenerationStatus

data class RoomUiState(
    val navigateToLogin: Boolean = false,
    val imageEtaSeconds: List<Int> = emptyList(),
    val getRoomsResponse: ResultState<Rooms> = ResultState.Stable,
    val trendingRooms: List<RoomUi> = emptyList(),
    val allRooms: List<RoomUi> = emptyList(),
    val filteredRooms: List<RoomUi> = emptyList(),
    val decodedImageBytes: List<ByteArray> = emptyList(),
    val searchQuery: String = "",
    val isEditMode: Boolean = false,
    val isResultSheetExpanded: Boolean = false,
    val selectedTaskId: String? = null,
    val expandedSection: FilterSection? = null,



    val filterState: FilterState = FilterState(),
    val filterCount: Int = 0,
    val errorMessage: String? = null,
    val tempFilterCount: Int = 0,
    val isLoading: Boolean = true,
    val currentTaskId: String? = null, // ✅ Har generation ki unique ID
    val activeTasksCount: Int = 0,
    val showFilterSheet: Boolean = false,
    val tempFilterState: FilterState = FilterState(),
    val expandedRoomType: Boolean = false,
    val expandedStyle: Boolean = false,
    val expandedColor: Boolean = false,
    val expandedFormat: Boolean = false,
    val expandedPrice: Boolean = false,
    val availableRoomTypes: List<String> = emptyList(),
    val availableStyles: List<InteriorStyle> = emptyList(),
    val availableTypes: List<InteriorType> = emptyList(),
    val availableStylesString: List<String> = emptyList(),
    val availableColors: List<ColorPalette> = emptyList(),
    val filterColors: List<ColorPalette> = emptyList(),
    val recentGeneratedImages: List<List<String>> = emptyList(),
    val selectedImage: String? = null,
    val currentPage: Int = 0,
    val pageCount: Int = 4,
    val selectedRoomType: String? = availableRoomTypes.firstOrNull(),
    val roomSearchQuery: String = "",
    val isRoomSearchExpanded: Boolean = false,
    val selectedStyleName: String? = availableStyles.firstOrNull()?.name,

    val styleSearchQuery: String = "",
    val isStyleSearchExpanded: Boolean = false,
    val selectedPaletteId: Int? = availableColors.firstOrNull()?.id,
    val isGenerating: Boolean = false,
    val generationStatus: GenerationStatus = GenerationStatus.IDLE,

    val availableInteriorStyles: List<com.webscare.interiorismai.domain.model.InteriorStyle> = emptyList(),
    val availableInteriorTypes: List<InteriorType> = emptyList(),
    val selectedStyleId: Int? = availableInteriorStyles.firstOrNull()?.id,


    val generatedImages: List<String> = emptyList(),
    val jobId: String? = null,
    val generatedRoom: GenerateRoomResponse? = null,
    val selectedImageBytes: ByteArray? = null,
    val selectedFileName: String? = null,
    val generatedCount: Int = 3,
    val generatedImagesEntity: List<RecentGeneratedEntity> = emptyList(),
    val isFetchingImages: Boolean = false,
    val etaSeconds: Int = 0,

    val isPurchasing: Boolean = false,
    val purchaseSuccess: String? = null,
    val purchaseError: String? = null,
    val billingProducts: List<com.webscare.interiorismai.billing.PurchaseProduct> = emptyList(),


    )

data class FilterState(
    val selectedRoomTypes: Set<String> = emptySet(),
    val selectedStyles: Set<String> = emptySet(),
    val selectedColors: Set<Int> = emptySet(),
    val selectedFormats: Set<String> = emptySet(),
    val selectedPrices: Set<String> = emptySet()
)
enum class FilterSection {
    ROOM_TYPE, STYLE, COLOR, FORMAT, PRICE
}

data class RoomDraft(
    val imageBytes: ByteArray,
    val roomType: String? = null,
    val styleName: String? = null,
    val paletteId: Int? = null,
    val currentPage: Int = 0
)
@Serializable
data class User(
    val id: Int,
    val fullname: String,
    val email: String,
    val image: String? = null
)
