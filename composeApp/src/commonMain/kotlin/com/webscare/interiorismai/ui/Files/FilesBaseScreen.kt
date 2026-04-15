package com.webscare.interiorismai.ui.Files


import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.navigation.Routes
import com.webscare.interiorismai.ui.CreateAndExplore.RoomEvent
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.utils.addPressEffect
import com.webscare.interiorismai.utils.getImageModel
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(
    viewModel: RoomsViewModel = koinViewModel(),
    navController: androidx.navigation.NavController,
    onImageClick: (Long) -> Unit,
    onShowResults: (bundleId: String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val tasksProgress by viewModel.tasksProgress.collectAsState()
    val tabs = listOf("Recent", "Drafts")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var selectedTabIndex = remember {
        derivedStateOf {
            pagerState.currentPage
        }
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp)
                    .statusBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                Text(
                    text = "Files",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )
                Text(
                    text = "Your saved creations.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex.value,
                    containerColor = Color.White,
                    contentColor = Color(0xFF2C2C2C),
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex.value])
                                .height(2.dp)
                                .background(
                                    color = Color(0xFF99AD76),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    },
                    divider = {
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = Color(0xFFE6E4E4)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex.value == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },

                            text = {
                                Text(
                                    text = title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedTabIndex.value == index) Color(0xFF2C2C2C) else Color(
                                        0xFF959595
                                    )
                                )
                            }
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {

                0 -> {
                    // 1. Database se images collect karein
                    val dbImages by viewModel.dbGeneratedImages.collectAsState()

                    // 2. RecentContent ko pass karein
                    RecentContent(
                        state = state,
                        generatedBundles = dbImages,
                        isFetching = state.isFetchingImages,
                        tasksProgress = tasksProgress,
                        onBundleClick = { selectedBundle ->
                            viewModel.onRoomEvent(RoomEvent.ShowSelectedBundle(listOf(selectedBundle)))
                            selectedBundle.bundleId?.let { onShowResults(it) }
                        },
                        onDeleteBundles = { selectedIds ->
                            // This is where you call the ViewModel to delete from RoomDB
                            selectedIds.forEach { id ->
                                viewModel.deleteBundleById(id)
                            }
                        }
                    )
                }


                1 -> DraftsContent(
                    viewModel = viewModel,
                    onImageClick = { clickedDraft ->
                        println("DEBUG_DRAFT_CLICK: navigating to = ${Routes.AddScreen}")
                        println("DEBUG_DRAFT_CLICK: currentPage = ${clickedDraft.currentPage}")
                        println("DEBUG_DRAFT_CLICK: roomType = ${clickedDraft.roomType}")
                        viewModel.selectDraftImage(clickedDraft)
                        navController.navigate(Routes.AddScreen)
                    }
                )
            }

        }
    }


}

@Composable
fun calculateProgress(isFetching: Boolean, generatedCount: Int): Float {
    if (!isFetching) return 0f

    // Timer state jo har second update hogi
    val elapsed = remember(isFetching) { mutableIntStateOf(0) }
    val maxSeconds = 90 // ResultScreen wala max time

    LaunchedEffect(isFetching) {
        if (isFetching) {
            elapsed.intValue = 0
            while (elapsed.intValue < maxSeconds) {
                kotlinx.coroutines.delay(1000L)
                elapsed.intValue++
            }
        }
    }

    // Percentage calculation (0.0 to 0.99)
    val rawProgress = elapsed.intValue.toFloat() / maxSeconds.toFloat()
    return rawProgress.coerceIn(0f, 0.99f)
}






