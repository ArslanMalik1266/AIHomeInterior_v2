package com.webscare.interiorismai.ui.Generate.UiScreens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.arrow_back_
import homeinterior.composeapp.generated.resources.close
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import com.webscare.interiorismai.ui.CreateAndExplore.RoomEvent
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.ui.Files.ProBadge
import com.webscare.interiorismai.ui.UiUtils.CommonAppButton

@Composable
fun BaseGenerateScreen(roomsViewModel: RoomsViewModel, endToNext: () -> Unit, onCloseClick: () -> Unit = {}) {
    val state by roomsViewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { state.pageCount })
    val scope = rememberCoroutineScope()
    var isClosing by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentPage) {
        if (!isClosing) {  // 👈 only animate if not closing
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White).statusBarsPadding(),
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().offset(y = (-30).dp), contentAlignment = Alignment.Center) {
                CommonAppButton(
                    title = "Next", modifier = Modifier
                        .fillMaxWidth(0.6f)
                ) {
                    scope.launch {
                        if (state.isEditMode) {
                            endToNext()
                        } else if (state.currentPage < state.pageCount - 1) {
                            roomsViewModel.onRoomEvent(RoomEvent.OnNextPage)
                        } else {
                            endToNext()
                        }
                    }
                }
            }
        }
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize().background(Color.White)
        ) {

            TopNavigationBar(
                gradientColors = listOf(
                    Color(0xFFC5EBB2),
                    Color(0xFFDFF2C2),
                    Color(0xFFC1DFB5),
                    Color(0xFFD2F7BD)
                ),
                currentPage = state.currentPage,
                onCloseClick = {
                    isClosing = true   // 👈 block pager animation first
                    onCloseClick()     // 👈 then close
                }
            ) {
                roomsViewModel.onRoomEvent(RoomEvent.OnPreviousPage)
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProgressIndicator(
                pageCount = state.pageCount,
                currentPage = pagerState.currentPage,
                targetPage = pagerState.targetPage,
                pageOffset = pagerState.currentPageOffsetFraction.coerceIn(0f, 1f)
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalPager(state = pagerState, userScrollEnabled = false) { page ->
                when (page) {
                    0 -> {
                        FirstPage(state.selectedImageBytes, state.selectedImage)
                    }

                    1 -> {
                        RoomTypeSelection(
                            roomTypes = state.availableRoomTypes,
                            selectedRoomType = state.selectedRoomType,
                            searchQuery = state.roomSearchQuery,
                            isSearchExpanded = state.isRoomSearchExpanded,
                            onRoomTypeSelected = { roomtype ->
                                roomsViewModel.onRoomEvent(RoomEvent.OnRoomTypeSelected(roomtype))
                            },
                            onSearchQueryChange = { query ->
                                roomsViewModel.onRoomEvent(RoomEvent.OnRoomSearchQueryChange(query))
                            },
                            onSearchExpandedChange = { updatedChange ->
                                roomsViewModel.onRoomEvent(RoomEvent.OnRoomSearchExpandedChange(updatedChange))
                            },
                        )
                    }

                    2 -> {
                        StyleSelectionScreen(
                            styles = state.availableStyles,
                            selectedStyleId = state.selectedStyleId,
                            selectedStyleName = state.selectedStyleName,
                            searchQuery = state.styleSearchQuery,
                            isSearchExpanded = state.isStyleSearchExpanded,
                            onStyleSelected = { id, name -> // 👈 Ab yahan do cheezein mil rahi hain
                                roomsViewModel.onRoomEvent(RoomEvent.OnStyleSelected(id, name)) // ✅ Dono pass kar dein
                            },
                            onSearchQueryChange = { query ->
                                roomsViewModel.onRoomEvent(RoomEvent.OnStyleSearchQueryChange(query))
                            },
                            onSearchExpandedChange = { expanded ->
                                roomsViewModel.onRoomEvent(RoomEvent.OnStyleSearchExpandedChange(expanded))
                            }
                        )
                    }

                    3 -> {
                        ColorPaletteSelectionScreen(
                            palettes = state.filterColors,
                            selectedPaletteId = state.selectedPaletteId,
                            selectedPalette = state.selectedPalette,
                            onPaletteSelected = { paletteId ->
                                roomsViewModel.onRoomEvent(
                                    RoomEvent.OnPaletteSelected(paletteId)
                                )
                                println("Selected Palette ID: $paletteId")
                            }
                        )
                    }

                }
            }

        }
    }
}


@Composable
private fun TopNavigationBar(
    gradientColors: List<Color>,
    currentPage: Int,
    onCloseClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (currentPage > 0) {

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    )
                    {
                        Image(
                            painter = painterResource(Res.drawable.arrow_back_),
                            colorFilter = ColorFilter.tint(color = Color(0xFFB2B0B0)),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(18.dp)
                        )
                    }

            } else {
//            ProBadge(gradientColors)
            }
        }
        Text(
            text = "Step ${currentPage + 1}/4",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9C9C9C),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(modifier = Modifier.clip(CircleShape).clickable {
                onCloseClick()
            }, contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(Res.drawable.close),
                    contentDescription = "back",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color(0xFFB2B0B0))
                )
            }
        }
    }
}

@Composable
private fun ProgressIndicator(pageCount: Int, currentPage: Int, targetPage: Int, pageOffset: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pageCount) { index ->
                val progress = when {
                    index < currentPage -> 1f
                    index == currentPage -> 1f - pageOffset
                    index == targetPage && pageOffset > 0 -> pageOffset
                    else -> 0f
                }

                val color by animateColorAsState(
                    targetValue = if (progress > 0) Color(0xFFA3B18A) else Color(0xFFE4E2E2),
                    animationSpec = tween(durationMillis = 300)
                )

                Box(
                    modifier = Modifier
                        .width(45.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                )
            }
        }
    }
}