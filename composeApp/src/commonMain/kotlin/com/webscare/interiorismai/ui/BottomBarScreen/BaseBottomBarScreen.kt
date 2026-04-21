package com.webscare.interiorismai.ui.BottomBarScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import homeinterior.composeapp.generated.resources.*
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.navigation.Routes
import com.webscare.interiorismai.ui.Account.AccountScreen
import com.webscare.interiorismai.ui.Account.ProfileScreen
import com.webscare.interiorismai.ui.Account.SubscriptionScreen
import com.webscare.interiorismai.ui.CreateAndExplore.Create.CreateScreen
import com.webscare.interiorismai.ui.CreateAndExplore.Explore.ExploreScreen
import com.webscare.interiorismai.ui.CreateAndExplore.RoomEvent
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.ui.Files.CreateEditScreen
import com.webscare.interiorismai.ui.Files.FilesScreen
import com.webscare.interiorismai.utils.uriToByteArray
import com.webscare.interiorismai.ui.Generate.UiScreens.AboutToGenerateScreen
import com.webscare.interiorismai.ui.Generate.UiScreens.BaseGenerateScreen
import com.webscare.interiorismai.ui.Generate.UiScreens.ResultScreen
import com.webscare.interiorismai.ui.UiUtils.*
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import com.webscare.interiorismai.ui.theme.bottomBarBack
import com.webscare.interiorismai.ui.theme.selectedNavItem
import com.webscare.interiorismai.ui.theme.unselectedNavItem
import com.webscare.interiorismai.utils.CommonBackHandler
import com.webscare.interiorismai.utils.GenerationStatus
import com.webscare.interiorismai.utils.getPlatformContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBottomBarScreen(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel
) {


    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val roomViewModel: RoomsViewModel = koinViewModel()
    var showGallery by remember { mutableStateOf(false) }
    var isFromExplore by remember { mutableStateOf(false) }


    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded, // Shuru mein sirf peek dikhayega
            skipHiddenState = false // Aap isy AnimatedVisibility ki tarah hide bhi kar sakte hain
        )
    )
    val tasksStatus by roomViewModel.tasksStatus.collectAsState()
    val tasksProgress by roomViewModel.tasksProgress.collectAsState()
    val roomState by roomViewModel.state.collectAsState()
    val activeTaskIds = remember(tasksProgress) { tasksProgress.keys.toList() }

    val taskQueue by roomViewModel.taskQueue.collectAsState()
    val currentTaskId = remember(tasksStatus) { tasksStatus.keys.firstOrNull() ?: "" }
    val currentStatus = tasksStatus[currentTaskId] ?: GenerationStatus.IDLE
    var showTapToView by remember { mutableStateOf<String?>(null) }
    val displayProgress =
        if (currentStatus == GenerationStatus.SUCCESS) 1f else (tasksProgress[currentTaskId] ?: 0f)
    var lastTaskId by remember { mutableStateOf<String?>(null) }
    val activeCount = tasksStatus.count { it.value == GenerationStatus.RUNNING }
    var isExpanded by remember { mutableStateOf(false) }


// YEH LINE ADD KAR EIN:
    val isCurrentBundleFetching = currentTaskId.isNotEmpty() &&
            tasksProgress.containsKey(currentTaskId) &&
            tasksStatus[currentTaskId] == GenerationStatus.RUNNING

    val shouldShowBottomBar = currentDestination?.route?.let { route ->
        route.contains("Create") ||
                route.contains("Files") ||
                route.contains("Explore") ||
                route.contains("Account")
    } ?: false
    LaunchedEffect(currentDestination?.route) {
        val index = getSelectedTabIndex(currentDestination?.route)
        SlippyOptions.currentPage.value = index
    }
    val isRunning =
        shouldShowBottomBar && currentTaskId.isNotEmpty() && currentStatus != GenerationStatus.IDLE
    val waitingMessages = listOf(
        "Almost there..",
        "Finalizing details..",
        "Adding finishing touches..",
        "Just a moment more..",
        "Polishing your design..",
        "Worth the wait.."
    )
    var waitingMessageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(displayProgress >= 0.99f) {
        if (displayProgress >= 0.99f) {
            while (true) {
                delay(5000L)
                waitingMessageIndex = (waitingMessageIndex + 1) % waitingMessages.size
            }
        }
    }
    val loadingText = when (currentStatus) {
        GenerationStatus.SUCCESS -> "Completed."
        GenerationStatus.RUNNING -> {
            when {
                displayProgress < 0.4f -> "Getting things ready.."
                displayProgress < 0.9f -> "Creating your image.."
                displayProgress >= 0.99f -> waitingMessages[waitingMessageIndex]
                else -> "Almost there.."
            }
        }

        else -> ""
    }
    val platformContext = getPlatformContext()


    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                val tabs = listOf(
                    SlippyTab(
                        name = stringResource(Res.string.tabOneCreate),
                        icon = painterResource(Res.drawable.createiconfinal),
                        selectedIcon = painterResource(Res.drawable.selectedcreate),
                        action = {
                            navController.navigate(Routes.Create) {
                                popUpTo(Routes.Create) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    ),
                    SlippyTab(
                        name = stringResource(Res.string.tabTwoExplore),
                        icon = painterResource(Res.drawable.newexploreicon),
                        selectedIcon = painterResource(Res.drawable.selectedexploreicon),
                        action = {
                            navController.navigate(Routes.Explore) {
                                popUpTo(Routes.Create) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ),
                    SlippyTab(
                        name = "",
                        icon = painterResource(Res.drawable.createiconfinal),
                        action = {
                            showGallery = true
                        } // Placeholder for FAB
                    ),
                    SlippyTab(
                        name = stringResource(Res.string.tabThreeFiles),
                        icon = painterResource(Res.drawable.files),
                        selectedIcon = painterResource(Res.drawable.selectedfile),
                        enableBadge = activeCount > 0,
                        badgeCount = activeCount,
                        action = {
                            navController.navigate(Routes.Files) {
                                popUpTo(Routes.Create) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },

                        ),
                    SlippyTab(
                        name = stringResource(Res.string.tabFourProfile),
                        icon = painterResource(Res.drawable.profileiconnew),
                        selectedIcon = painterResource(Res.drawable.selectedprofile),
                        action = {
                            navController.navigate(Routes.Account) {
                                popUpTo(Routes.Create) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bottomBarBack)
                ) {
                    SlippyBottomBar(
                        bar = SlippyBar(
                            barStyle = SlippyBarStyle(backgroundColor = bottomBarBack),
                            textStyle = SlippyTextStyle(
                                enabledTextColor = selectedNavItem,
                                disabledTextColor = unselectedNavItem,
                                textSize = 12.sp
                            ),
                            iconStyle = SlippyIconStyle(
                                enabledIconColor = selectedNavItem,
                                disabledIconColor = unselectedNavItem
                            ),
                            startIndex = getSelectedTabIndex(currentDestination.route),
                            badgeStyle = SlippyBadgeStyle(
                                backgroundColor = Color(0xFFCCE9A2),
                                contentColor = Color.White
                            )
                        ),
                        tabs = tabs,
                        iconSize = 24.dp,
                        fabIndex = 2,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            if (shouldShowBottomBar) {
                Box(
                    modifier = Modifier
                        .offset(y = 50.dp)
                        .background(bottomBarBack, CircleShape)
                        .size(65.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            showGallery = true
                        },
                        containerColor = Color(0xFFD4F7BD),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        ),
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.Center),
                        shape = CircleShape
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .innerShadow(
                                    shape = CircleShape,
                                    shadow = Shadow(
                                        radius = 7.dp,
                                        color = Color.Black.copy(alpha = 0.2f),
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painterResource(Res.drawable.add),
                                contentDescription = "Add",
                                colorFilter = ColorFilter.tint(color = Color.Black),
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Routes.Create,
                modifier = Modifier
                    .background(Color.White)
                    .padding(bottom = padding.calculateBottomPadding() + if (isRunning) 60.dp else 0.dp)
            )
            {
                // Bottom bar destinations
                composable<Routes.Create> {
                    CreateScreen(
                        viewModel = roomViewModel,
                        onPremiumClick = {
                            navController.navigate(Routes.Subscription)
                        },
                        onAddPhotoClick = {
                            roomViewModel.prepareForNewGeneration()
                            showGallery = true
                        },
                        onNavigateToAboutToGenerate = {
                            navController.navigate(Routes.AbtToGenerate)
                        },
                        onRoomClick = { room ->
                            roomViewModel.clearBundleSelection()
                            val hexColors = room.colors.map { color ->
                                roomViewModel.run { color.toRawHex() }
                            }
                            navController.navigate(
                                Routes.AbtToGenerateWithData(
                                    imageUrl = room.compressedImageUrl,
                                    style = room.roomStyle,
                                    type = room.roomType,
                                    colors = hexColors,
                                    fullImageUrl = room.imageUrl,
                                )
                            )
                        },
                        onShowResults = { bundleId ->
                            roomViewModel.selectBundle(bundleId)
                            navController.navigate(Routes.Result)
                        },
                        onSeeAllClick = {
                            navController.navigate(Routes.Files) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }

                    )
                }

                composable<Routes.Explore> {
                    ExploreScreen(
                        viewModel = roomViewModel,
                        onRoomClick = { room ->
                            println("colorsars = ${room}")

                            roomViewModel.clearBundleSelection()
                            roomViewModel.resetStateForNewGeneration(isExplore = true)

                            val hexColors = room.colors.map { color ->
                                roomViewModel.run { color.toRawHex() }
                            }
                            navController.navigate(
                                Routes.AbtToGenerateWithData(
                                    imageUrl = room.compressedImageUrl,
                                    fullImageUrl = room.imageUrl,
                                    style = room.roomStyle,
                                    type = room.roomType,
                                    colors = hexColors
                                )
                            )
                        }
                    )
                }
                composable<Routes.AbtToGenerateWithData> { backStackEntry ->
                    val args = backStackEntry.toRoute<Routes.AbtToGenerateWithData>()
                    var showGallery by remember { mutableStateOf(false) }
                    var localImageUrl by remember { mutableStateOf(args.fullImageUrl) }
                    val state by roomViewModel.state.collectAsState()
                    println("DEBUG_NAVIGATION_CHECK: imageUrl (Compressed) = ${args.imageUrl}")
                    println("DEBUG_NAVIGATION_CHECK: fullImageUrl = ${args.fullImageUrl}")

                    LaunchedEffect(Unit) {
                        println("DEBUG_DEBUG: Sending to ViewModel -> ${args.imageUrl}")
                        println("DEBUG_CLASS_PATH: Composable class path = ${RoomEvent.SetTemplateDetails::class.qualifiedName}")
                        roomViewModel.onRoomEvent(
                            RoomEvent.SetTemplateDetails(
                                style = args.style,
                                type = args.type,
                                colors = args.colors,
                                compressedImageUrl = args.imageUrl, // Yahan data gaya
                                fullImageUrl = args.fullImageUrl
                            )
                        )
                    }


                    AboutToGenerateScreen(
                        roomsViewModel = roomViewModel,
                        authViewModel = authViewModel,
                        isEditable = false,
                        compressedImageUrl = state.compressedImageUrl,
                        fullImageUrl = state.fullImageUrl,
                        imageUrl = state.fullImageUrl ?: localImageUrl,
                        isFromExplore = state.isFromExplore,
                        onCloseClick = { navController.popBackStack() },
                        onResult = { shouldOpenGallery ->
                            if (shouldOpenGallery) {
                                showGallery = true
                            } else {
                                navController.navigate(Routes.Result) {
                                    popUpTo(Routes.AbtToGenerate) { inclusive = true }
                                }
                            }
                                   },
                        onSubscriptionClick = { navController.navigate(Routes.Subscription) },
                        onEditType = {

                        },
                        onEditStyle = {

                        },
                        onEditPalette = {

                        }
                    )
                    if (showGallery) {
                        GalleryPickerLauncher(
                            onPhotosSelected = { photos ->
                                val photo = photos.first()
                                val bytes = uriToByteArray(platformContext, photo.uri.toString())
                                val fileName = "room_upload.jpg"

                                roomViewModel.onRoomEvent(RoomEvent.SetImageBytes(bytes, fileName))
                                roomViewModel.onRoomEvent(
                                    RoomEvent.SetTemplateDetails(
                                        style = args.style,
                                        type = args.type,
                                        colors = args.colors,
                                        compressedImageUrl = state.compressedImageUrl ?: "",
                                        fullImageUrl = state.fullImageUrl ?: ""
                                    )
                                )

                                showGallery = false
                            },
                            onError = { showGallery = false },
                            onDismiss = { showGallery = false },
                            allowMultiple = false,
                            selectionLimit = 1
                        )
                    }
                }

                composable<Routes.Files> {
                    FilesScreen(
                        viewModel = roomViewModel,
                        navController = navController,
                        onImageClick = { entityId ->
                            navController.navigate(
                                Routes.FileEdit(entityId = entityId)
                            )
                        },
                        onShowResults = { bundleId ->
                            roomViewModel.selectBundle(bundleId)
                            navController.navigate(Routes.Result)
                        }
                    )
                }

                composable<Routes.Account> {
                    AccountScreen(
                        onSubscriptionClick = {
                            navController.navigate(Routes.Subscription)
                        },
                        onProfileClick = {
                            navController.navigate(Routes.Profile)
                        },
                        viewModel = authViewModel
                    )
                }

                composable<Routes.AddScreen> {
                    BaseGenerateScreen(
                        roomViewModel,
                        endToNext = {
                            val isEdit = roomViewModel.state.value.isEditMode
                            if (isEdit) {
                                roomViewModel.onRoomEvent(RoomEvent.SetEditMode(false))
                                navController.navigate(Routes.AbtToGenerate) {
                                    popUpTo(Routes.AddScreen) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Routes.AbtToGenerate)
                            }
                        },
                        onCloseClick = {
                            val previousRoute =
                                navController.previousBackStackEntry?.destination?.route
                            println("DEBUG_ADDSCREEN_CLOSE: previousRoute = $previousRoute")
                            roomViewModel.onRoomEvent(RoomEvent.SetEditMode(false))
                            navController.popBackStack()
                            kotlinx.coroutines.GlobalScope.launch {
                                kotlinx.coroutines.delay(800L)
                                roomViewModel.saveOrUpdateDraft()
                            }

                        }
                    )
                }

                composable<Routes.FileEdit> { backStackEntry ->

                    val args = backStackEntry.toRoute<Routes.FileEdit>()

                    val dbImages by roomViewModel.dbGeneratedImages.collectAsState()
                    val state by roomViewModel.state.collectAsState()
                    val selectedBundleId by roomViewModel.selectedBundleId.collectAsState()

                    // 🔥 FIX: stable entity resolution (NO heavy recomposition)
                    val entity = remember(args.entityId, selectedBundleId) {
                        when {
                            args.imageIndex >= 0 && state.generatedImagesEntity.isNotEmpty() -> {
                                state.generatedImagesEntity.firstOrNull { it.bundleId == selectedBundleId }
                                    ?: state.generatedImagesEntity.firstOrNull()
                            }

                            args.entityId != -1L -> dbImages.find { it.id == args.entityId }

                            else -> null
                        }
                    }

                    val selectedIndex = if (args.imageIndex >= 0) args.imageIndex else 0

                    when {

                        // 🔹 Trending / direct URL flow
                        args.imageUrl.isNotEmpty() -> {
                            CreateEditScreen(
                                entity = null,
                                viewModel = null,
                                imageUrlString = args.imageUrl,
                                isTrending = true,
                                onClick = { navController.popBackStack() }
                            )
                        }

                        // 🔹 Entity-based flow
                        entity != null -> {

                            val imagePath =
                                entity.localPaths.getOrNull(selectedIndex)

                            CreateEditScreen(
                                entity = entity,
                                viewModel = roomViewModel,
                                selectedIndex = selectedIndex,
                                imageUrlString = imagePath ?: "",

                                onClick = {
                                    navController.popBackStack()
                                },

                                onRedo = {
                                    navController.navigate(Routes.Result) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        else -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF99AD76))
                            }
                        }
                    }
                }
                composable<Routes.AbtToGenerate> {

                    AboutToGenerateScreen(
                        imageUrl = null,
                        isFromExplore = false,
                        roomsViewModel = roomViewModel,
                        authViewModel = authViewModel,
                        onCloseClick = {
                            roomViewModel.onRoomEvent(RoomEvent.OnResetState)
                            navController.popBackStack()
                        },
                        onResult = {
                            roomViewModel.selectBundle(null)
                            navController.navigate(Routes.Result) {
                                popUpTo(Routes.AbtToGenerate) { inclusive = true }
                            }
                        },
                        onSubscriptionClick = {
                            navController.navigate(Routes.Subscription)
                        },
                        onEditType = {
                            roomViewModel.onRoomEvent(RoomEvent.SetEditMode(true))
                            roomViewModel.onRoomEvent(RoomEvent.GoToPage(1))
                            navController.navigate(Routes.AddScreen)
                        },

                        onEditStyle = {
                            roomViewModel.onRoomEvent(RoomEvent.SetEditMode(true))
                            roomViewModel.onRoomEvent(RoomEvent.GoToPage(2))
                            navController.navigate(Routes.AddScreen)
                        },
                        onEditPalette = {
                            roomViewModel.onRoomEvent(RoomEvent.SetEditMode(true))
                            roomViewModel.onRoomEvent(RoomEvent.GoToPage(3))
                            navController.navigate(Routes.AddScreen)
                        }
                    )
                }

                composable<Routes.Subscription> {
                    SubscriptionScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        roomsViewModel = roomViewModel,
                        onLoginClick = {
                            rootNavController.navigate(Routes.Login) {
                                launchSingleTop = true
                            }
                        },
                        onAddPhotoClick = {
                            navController.popBackStack()
                            showGallery = true
                        }
                    )
                }

                composable<Routes.Result> {
                    val state by roomViewModel.state.collectAsState()
                    val selectedImage by roomViewModel.selectedGeneratedImage.collectAsState()

                    selectedImage?.let {
                        navController.navigate(Routes.FileEdit(imageUrl = it))
                        roomViewModel.resetSelectedGeneratedImage()
                    }
                    ResultScreen(
                        viewModel = roomViewModel,
                        onCloseClick = {
                            val previousRoute =
                                navController.previousBackStackEntry?.destination?.route
                            roomViewModel.onRoomEvent(RoomEvent.OnGenerationComplete)
                            if (previousRoute?.contains("Files") == true) {
                                navController.popBackStack()
                            } else {
                                navController.navigate(Routes.Create) {
                                    popUpTo(Routes.Create) { inclusive = true }
                                }
                            }
                        },
                        imageEtaSeconds = state.imageEtaSeconds,
                        generatedImages = state.generatedImagesEntity,
                        isFetchingImages = state.isFetchingImages,
                        generatedCount = 3,
                        onImageClick = { index ->
                            val currentBundle =
                                roomViewModel.state.value.generatedImagesEntity.firstOrNull()
                            if (currentBundle != null) {
                                navController.navigate(
                                    Routes.FileEdit(
                                        imageIndex = index,
                                        entityId = currentBundle.id
                                    )
                                )
                            }
                        },
                        onBackClick = {
                            navController.navigate(Routes.Create) {
                                popUpTo(Routes.Create) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Routes.Profile> {
                    ProfileScreen(
                        authViewModel = authViewModel,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onLogoutSuccess = {
                            rootNavController.navigate(Routes.Login) {
                                popUpTo(Routes.BaseAppScreen) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        onLoginClick = {
                            rootNavController.navigate(Routes.Login) {
                                launchSingleTop = true

                            }
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = isRunning,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 24.dp, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color.White, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .animateContentSize(animationSpec = tween(durationMillis = 600))
                        .then(if (isExpanded) Modifier.fillMaxHeight(0.9f) else Modifier.wrapContentHeight())
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ){}
                )
                {

                    if (isExpanded) {
                        CommonBackHandler(enabled = isExpanded) {
                            isExpanded = false
                        }
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            // Ye raha aapka reusable result screen
                            ResultScreen(
                                viewModel = roomViewModel,
                                generatedImages = roomViewModel.state.collectAsState().value.generatedImagesEntity,
                                isFetchingImages = isCurrentBundleFetching,
                                generatedCount = 3,
                                onCloseClick = {
                                    // Bas expand band kar dein
                                    isExpanded = false
                                },
                                onBackClick = {
                                    // Bas expand band kar dein
                                    isExpanded = false
                                },
                                onImageClick = { index ->
                                    // Yahan wahi logic jo aapne NavHost wale ResultScreen mein likhi thi
                                    val currentBundle = roomViewModel.state.value.generatedImagesEntity.firstOrNull()
                                    if (currentBundle != null) {
                                        navController.navigate(Routes.FileEdit(imageIndex = index, entityId = currentBundle.id))
                                    }
                                }
                            )
                        }

                    } else
                    {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = true }
                                .padding(bottom = padding.calculateBottomPadding())
                                .padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    end = 16.dp,
                                    bottom = 24.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            BadgedBox(
                                badge = {
                                    val activeCount =
                                        tasksStatus.count { it.value == GenerationStatus.RUNNING }
                                    if (activeCount > 0) {
                                        Badge(
                                            containerColor = Color(0xFFCCE9A2),
                                            modifier = Modifier.size(14.dp)
                                        ) {
                                            Text(
                                                text = activeCount.toString(),
                                                fontSize = 8.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                },

                                ) {
                                Box(
                                    modifier = Modifier.size(36.dp).clip(CircleShape)
                                        .background(Color(0xFFF2F2F1)), contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.bottom_box_image),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    CircularProgressIndicator(
                                        progress = { displayProgress },
                                        modifier = Modifier.size(36.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFFCCE9A2),
                                        trackColor = Color(0xFFF2F2F1)
                                    )
                                    val activeCount =
                                        tasksStatus.count { it.value == GenerationStatus.RUNNING }
                                    if (activeCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .align(Alignment.TopEnd)
                                                .offset(x = 4.dp, y = (-4).dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFCCE9A2)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = activeCount.toString(),
                                                fontSize = 8.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))
                            val infiniteTransition = rememberInfiniteTransition()
                            val blinkAlpha by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 0.2f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 800),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )

                            val textAlpha =
                                if (currentStatus == GenerationStatus.RUNNING) blinkAlpha else 1f

                            Text(
                                text = loadingText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2C2C2C),
                                modifier = Modifier.graphicsLayer { alpha = textAlpha }
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            if (currentStatus == GenerationStatus.SUCCESS) {
                                Text(
                                    text = "Tap to view",
                                    fontSize = 12.sp,
                                    color = Color(0xFFD4F7BD),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable {
                                            roomViewModel.selectBundle(currentTaskId)
                                            navController.navigate(Routes.Result)
                                            showTapToView = null
                                            roomViewModel.onRoomEvent(RoomEvent.OnGenerationComplete)
                                        }
                                        .padding(8.dp)
                                )
                            } else {
                                IconButton(
                                    onClick = {
                                        roomViewModel.onRoomEvent(RoomEvent.OnCancelGeneration(taskId = currentTaskId))
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cancel",
                                        tint = Color(0xFF8E8E8E),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }


            } }
        }


        if (showGallery) {
            GalleryPickerLauncher(
                onPhotosSelected = { photos ->
                    val photo = photos.first()
                    val bytes = uriToByteArray(platformContext, photo.uri.toString())
                    println("DEBUG_PICKER: Raw bytes from picker = ${bytes.size}")
                    val fileName = "room_upload.jpg"
                    roomViewModel.resetGenerationState()
                    roomViewModel.onRoomEvent(
                        RoomEvent.SetImageBytes(bytes, fileName)
                    )

                    showGallery = false
                    navController.navigate(Routes.AddScreen)

                },
                onError = { showGallery = false },
                onDismiss = { showGallery = false },
                allowMultiple = false,
                selectionLimit = 1
            )
        }
    }
}

@Composable
fun ScrimOverlay(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
        )
    }
}

private fun getSelectedTabIndex(currentRoute: String?): Int {
    return when {
        currentRoute?.contains("Create") == true -> 0
        currentRoute?.contains("Explore") == true -> 1
        currentRoute?.contains("Files") == true -> 3
        currentRoute?.contains("Account") == true -> 4
        else -> 0
    }
}