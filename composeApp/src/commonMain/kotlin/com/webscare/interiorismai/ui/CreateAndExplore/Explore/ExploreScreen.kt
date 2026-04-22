package com.webscare.interiorismai.ui.CreateAndExplore.Explore

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.filter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.DrawableResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.RoomUi
import com.webscare.interiorismai.ui.CreateAndExplore.Create.shimmerLoading
import com.webscare.interiorismai.ui.CreateAndExplore.RoomEvent
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.ui.theme.fieldBack
import com.webscare.interiorismai.utils.addPressEffect
import homeinterior.composeapp.generated.resources.ic_filter
import homeinterior.composeapp.generated.resources.ic_premium_icon
import homeinterior.composeapp.generated.resources.ic_search
import homeinterior.composeapp.generated.resources.play_fair_italic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: RoomsViewModel = koinViewModel(), onRoomClick: (RoomUi) -> Unit = {}) {
    val scope = rememberCoroutineScope()
    var showFilterSheet by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()
    val listState = rememberLazyStaggeredGridState()
    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 }
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sticky Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 20.dp, top = 14.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {

                    Text(
                        text = "Explore",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C),
                        lineHeight = 22.sp
                    )

                    Text(
                        text = "Find your aesthetic.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp)
                ) {
                    BasicTextField(
                        value = state.searchQuery,
                        onValueChange = {
                            viewModel.onRoomEvent(RoomEvent.OnSearchQueryChange(it))
                        },
                        textStyle = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.16.sp,
                            color = Color.Black
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .height(50.dp)
                            .weight(1f)
                            .background(fieldBack, RoundedCornerShape(50.dp))
                    ) { innerTextField ->
                        TextFieldDefaults.DecorationBox(
                            value = state.searchQuery,
                            innerTextField = innerTextField,
                            placeholder = {
                                Text(
                                    text = "Try 'Modern Kitchen' or 'Boho'...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Gray,
                                    letterSpacing = 0.16.sp
                                )
                            },
                            leadingIcon = {  // ✅ Left side icon ke liye
                                Icon(
                                    painter = painterResource(Res.drawable.ic_search), // Aapka icon
                                    contentDescription = "Search",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)  // Icon ka size
                                )
                            },
                            singleLine = true,
                            enabled = true,
                            interactionSource = remember { MutableInteractionSource() },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            visualTransformation = VisualTransformation.None
                        )
                    }

                    Box(modifier = Modifier.height(50.dp).aspectRatio(1f) .addPressEffect {
                        viewModel.onRoomEvent(RoomEvent.OnFilterClick)
                        showFilterSheet = true
                    }) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(fieldBack, CircleShape)
                                .clip(CircleShape)
                               ,
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_filter),
                                contentDescription = null
                            )
                        }

                        if (state.filterCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .background(Color(0xFFA3B18A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.padding(4.dp)
                                        .background(Color.Transparent)

                                ) {
                                    Text(
                                        text = state.filterCount.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White,
                                        lineHeight = 10.sp,
                                    )

                                }

                            }
                        }
                    }
                }
            }
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    scope.launch {
                        isRefreshing = true
                        viewModel.onRoomEvent(RoomEvent.OnShuffleRooms)
                        delay(600)
                        isRefreshing = false
                    }
                },
                state = pullToRefreshState,
                indicator = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        PullToRefreshDefaults.Indicator(
                            state = pullToRefreshState,
                            isRefreshing = isRefreshing,
                            color = Color(0xFF99AD76),
                            containerColor = Color.White
                        )
                    }
                },
                modifier = Modifier.fillMaxSize().background(Color(0xFFFFFFFF))
            ) { Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = state.getRoomsResponse to state.filteredRooms.isEmpty(),
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                )
                { (loading, isEmpty) ->
                    when {
                        loading is ResultState.Loading -> ExploreGridShimmer(gridState = listState)
                        isEmpty -> EmptyRoomsMessage()
                        else -> ExploreGrid(
                            rooms = state.filteredRooms,
                            onRoomClick = onRoomClick,
                            gridState = listState,
                        )
                    }
                }
                if (isScrolled) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color.White.copy(alpha = 0.8f),
                                        Color.White.copy(alpha = 0.5f),
                                        Color.White.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            } }

        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.onRoomEvent(RoomEvent.OnDismissFilterSheet)
                    showFilterSheet = false
                },
                sheetState = sheetState,
                containerColor = Color.Transparent,
                dragHandle = null,
                modifier = Modifier.statusBarsPadding(),
                contentWindowInsets = { WindowInsets(0) }
            )
            {
                FilterBottomSheetContent(

                    filterState = state.tempFilterState,
                    filterCount = state.tempFilterCount,
                    expandedRoomType = state.expandedRoomType,
                    expandedStyle = state.expandedStyle,
                    expandedColor = state.expandedColor,
                    expandedFormat = state.expandedFormat,
                    expandedPrice = state.expandedPrice,
                    expandedSection = state.expandedSection,
                    onFilterStateChange = {
                        viewModel.onRoomEvent(RoomEvent.OnTempFilterChange(it))
                    },
                    onToggleSection = { section ->
                        viewModel.onRoomEvent(RoomEvent.OnToggleFilterSection(section))
                    },
                    onApplyFilters = {
                        viewModel.onRoomEvent(RoomEvent.OnApplyFilters)

                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showFilterSheet = false
                            }
                        }
                    },
                    onCancel = {
                        viewModel.onRoomEvent(RoomEvent.OnDismissFilterSheet)
                        showFilterSheet = false
                    },
                    onClearAll = {
                        scope.launch {
                            sheetState.hide()        // 👈 first hide the sheet fully
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                viewModel.onRoomEvent(RoomEvent.OnClearFilters)  // 👈 THEN clear/collapse
                                showFilterSheet = false
                            }
                        }
                    },
                    availableRoomTypes = state.availableRoomTypes,
                    availableStyles = state.availableStyles,
                    availableColors = state.filterColors
                )
            }
        }
    }
}

@Composable
private fun EmptyRoomsMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No rooms found",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}


@Composable
private fun ExploreGrid(
    rooms: List<RoomUi>,
    onRoomClick: (RoomUi) -> Unit,
    gridState: LazyStaggeredGridState
) {
    LazyVerticalStaggeredGrid(
        state = gridState,
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(rooms) { room ->
            RoomImageCard(
                room = room,
                onClick = { onRoomClick(room) }
            )
        }
    }
}

@Composable
private fun RoomImageCard(
    room: RoomUi,
    onClick: () -> Unit,

) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(room.cardHeight.dp)
            .addPressEffect() { onClick() }
            .clip(RoundedCornerShape(12.dp))

    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(room.compressedImageUrl)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCacheKey(room.imageUrl.hashCode().toString())
                .memoryCacheKey(room.imageUrl.hashCode().toString())
                .build(),
            contentDescription = room.roomType,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE8E8E8))
                        .shimmerLoading()
                )
            },
            error = {
                Image(
                    painter = painterResource(Res.drawable.roomplaceholder),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
                .background(bottomGradient)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = room.roomType.uppercase(),
                fontSize = 8.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                lineHeight = 1.sp,
            )
            val myFont = playFairFont

            Text(
                text = room.roomStyle,
                fontSize = 16.sp,
                fontFamily = myFont,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 14.sp,
            )
        }

        if (room.colors.isNotEmpty()) {
            OverlappingColorRow(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp),
                colors = room.colors
            )
        }
        if (room.isTrending == 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(36.dp)
                    .background(
                        color = Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_premium_icon),
                    contentDescription = "Trending",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun ImageCard(imageRes: DrawableResource, height: Dp, colors: List<Color>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        OverlappingColorRow(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp, start = 8.dp),
            colors = colors
        )
    }
}

@Composable
private fun ExploreGridShimmer(gridState: LazyStaggeredGridState) {
    val dummyItems = List(12) { it }

    LazyVerticalStaggeredGrid(
        state = gridState,
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(dummyItems) { index ->
            val height = remember(index) {
                listOf(150, 180, 210, 240, 280).random().dp
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8E8E8))
                    .shimmerLoading()
            )
        }
    }
}

@Composable
fun OverlappingColorRow(modifier: Modifier = Modifier, colors: List<Color>) {
    Box(
        modifier = modifier
            .size(
                width = (20.dp * colors.size) - (7.5.dp * (colors.size - 1)),
                height = 20.dp
            )
    ) {
        colors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .offset(x = (index * 12.5).dp)
                    .size(20.dp)
                    .background(color, shape = CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
        }
    }
}

private val playFairFont @Composable get() = FontFamily(Font(Res.font.play_fair_italic))

private val bottomGradient = Brush.verticalGradient(
    0.0f to Color.Transparent,
    1.0f to Color.Black.copy(alpha = 0.6f)
)