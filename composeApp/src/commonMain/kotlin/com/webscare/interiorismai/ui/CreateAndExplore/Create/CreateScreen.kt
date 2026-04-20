package com.webscare.interiorismai.ui.CreateAndExplore.Create

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.webscare.interiorismai.ui.theme.green_btn
import com.webscare.interiorismai.ui.theme.white_color
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.add_2_24px
import homeinterior.composeapp.generated.resources.createpageimage
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.data.local.entities.RecentGeneratedEntity
import com.webscare.interiorismai.domain.model.RoomUi
import com.webscare.interiorismai.ui.CreateAndExplore.RoomEvent
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.ui.theme.black_color
import com.webscare.interiorismai.ui.theme.grey_color
import com.webscare.interiorismai.utils.RippleViewKmp
import com.webscare.interiorismai.utils.addPressEffect
import com.webscare.interiorismai.utils.getImageModel
import homeinterior.composeapp.generated.resources.ic_subscriptions
import homeinterior.composeapp.generated.resources.play_fair_italic
import homeinterior.composeapp.generated.resources.premiumicon
import org.jetbrains.compose.resources.Font

@Composable
fun CreateScreen(
    viewModel: RoomsViewModel = koinViewModel(),
    onPremiumClick: () -> Unit = {},
    onAddPhotoClick: () -> Unit = {},
    onRoomClick: (RoomUi) -> Unit = {},
    onShowResults: (bundleId: String) -> Unit,
    onSeeAllClick: () -> Unit,
    onNavigateToAboutToGenerate: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    val dbImages by viewModel.dbGeneratedImages.collectAsState()
    val isDbLoaded by viewModel.isDbLoaded.collectAsState()
    val scrollState = rememberScrollState()

    val isScrolled = scrollState.value > 0


    LaunchedEffect(dbImages) {
        println("🟣 UI_CREATE: dbImages count = ${dbImages.size}")
    }

    // ✅ Bundles of entities
    val generatedBundles = dbImages.take(10)
    LaunchedEffect(generatedBundles) {
        println("🟣 UI_CREATE: bundles count = ${generatedBundles.size}")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white_color)
            .windowInsetsPadding(WindowInsets.statusBars),
    )
    {
        Header(
            onClick = onPremiumClick
        )
        EmptyStateCard({ onAddPhotoClick() })

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                TrendingSection(
                    isLoading = state.isLoading,
                    rooms = state.trendingRooms,
                    onRoomClick = onRoomClick
                )
                RecentFilesSection(
                    generatedBundles = generatedBundles,
                    onBundleClick = { bundle ->
                        viewModel.onRoomEvent(RoomEvent.ShowSelectedBundle(listOf(bundle)))
                        bundle.bundleId?.let { onShowResults(it) }
                    },
                    onSeeAllClick = onSeeAllClick,
                    isDbLoaded = isDbLoaded
                )
            }

            if (scrollState.value > 0) {
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
        }
    }
}

@Composable
fun Header(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 20.dp, top = 14.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "Interiorism AI",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                lineHeight = 22.sp
            )
            Box(
                modifier = Modifier
                    .size(34.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.wrapContentSize(unbounded = true)) {
                RippleViewKmp(
                    modifier = Modifier.size(70.dp),
                    rippleColor = Color(0xFFD4F7BD).copy(alpha = 0.30f)
                ) }
                Box(
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                        .addPressEffect() { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_subscriptions),
                        contentDescription = "",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // ✅ Subtitle text
        Text(
            text = "Start your design journey.",  // Aapka subtitle text
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = grey_color,  // Light gray color
            lineHeight = 16.sp,
            modifier = Modifier.offset(y = (-8).dp)

        )
    }
}

@Composable
private fun EmptyStateCard(onClick: () -> Unit) {
    Box(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp)) {
        Image(painter = painterResource(Res.drawable.createpageimage), contentDescription = null)
        Surface(
            color = green_btn,
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .height(28.dp)
                .addPressEffect(onClick = onClick),
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.add_2_24px),
                    contentDescription = "Add photo",
                    colorFilter = ColorFilter.tint(color = white_color),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add photo",
                    color = white_color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun TrendingSection(
    isLoading: Boolean, // ✅ Naya parameter
    rooms: List<RoomUi>,
    onRoomClick: (RoomUi) -> Unit
) {
    Column {
        Text(
            text = "Trending",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = black_color,
            modifier = Modifier.padding(start = 24.dp, top = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Teen states handle karein
        when {
            isLoading -> {
                // Jab API call chal rahi ho
                TrendingGridShimmer()
            }

            rooms.isEmpty() -> {
                // Jab API call khatam ho jaye lekin koi trending room na mile
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trending images available", // Aapka manga hua text
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                // Jab data mojood ho
                TrendingGrid(rooms = rooms, onRoomClick = onRoomClick)
            }
        }
    }
}

@Composable
private fun TrendingGrid(rooms: List<RoomUi>, onRoomClick: (RoomUi) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
        modifier = Modifier.then(
            if (rooms.size > 1) Modifier.height(300.dp)
            else Modifier.wrapContentHeight()
        )
    ) {
        items(rooms.chunked(2)) { columnItems ->
            val columnIndex = rooms.chunked(2).indexOf(columnItems)
            val isAlternate = columnIndex % 2 == 1
            Column(
                modifier = Modifier
                    .fillMaxHeight() // 👈 Ye zaroori hai taake Column poori 260.dp height le
                    .width(126.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                columnItems.forEachIndexed { index, room ->
                    // Height fix karne ki bajaye Weight use karein
                    val weight = when {
                        isAlternate && index == 1 -> 0.3f // Chota box (40% space)
                        isAlternate && index == 0 -> 0.7f // Bara box (60% space)
                        else -> 0.5f                      // Barabar (50% space)
                    }

                    Box(modifier = Modifier.weight(weight)) {
                        // height parameter ko Modifier.fillMaxSize() se replace kar dein
                        RoomCategoryCard(
                            room = room,
                            modifier = Modifier.fillMaxSize(), // 👈 Ab ye weight wali height lega
                            onClick = { onRoomClick(room) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomCategoryCard(room: RoomUi, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier // External modifier use karein (jo weight handle kar raha hai)
            .width(126.dp)
            .addPressEffect { onClick() }
            .clip(RoundedCornerShape(8.782.dp))

    ) {
        // ✅ AsyncImage ki jagah SubcomposeAsyncImage use karein
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
                // Error ke waqt placeholder
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
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.3f to Color.Black.copy(alpha = 0.1f),
                        0.6f to Color.Black.copy(alpha = 0.4f),
                        1.0f to Color.Black.copy(alpha = 0.6f)
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = room.roomType.uppercase(),
                fontSize = 8.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                lineHeight = 1.sp,
            )
            val myFont = FontFamily(Font(Res.font.play_fair_italic))

            Text(
                text = room.roomStyle,
                fontSize = 14.sp,
                fontFamily = myFont,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 14.sp,
            )
        }
    }
}

@Composable
private fun RecentFilesSection(
    generatedBundles: List<RecentGeneratedEntity>,  // ✅ Change type here
    onBundleClick: (RecentGeneratedEntity) -> Unit,  // ✅ Correct type
    onSeeAllClick: () -> Unit,
    isDbLoaded: Boolean
) {

    Column(modifier = Modifier.padding(bottom = 30.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Files",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = black_color
            )
            if (generatedBundles.isNotEmpty()) {
                Text(
                    text = "See all",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable { onSeeAllClick() }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        RecentFilesRow(
            generatedBundles = generatedBundles,  // ✅ Matches parameter name
            onBundleClick = onBundleClick,
            isLoading = !isDbLoaded
        )
    }
}

@Composable
private fun RecentFilesRow(
    generatedBundles: List<RecentGeneratedEntity>,  // ✅ Change type here
    onBundleClick: (RecentGeneratedEntity) -> Unit, // ✅ Callback parameter
    isLoading: Boolean = true
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        if (generatedBundles.isNotEmpty()) {
            items(generatedBundles) { bundle ->
                Box(
                    modifier = Modifier
                        .size(114.dp)
                        .addPressEffect { onBundleClick(bundle) }
                        .clip(RoundedCornerShape(12.dp))

                ) {
                    val coverImage =
                        bundle.localPaths.firstOrNull() ?: bundle.imageUrls.firstOrNull()
                    if (coverImage != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(getImageModel(coverImage) ?: coverImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(Res.drawable.roomplaceholder),
                            error = painterResource(Res.drawable.roomplaceholder)
                        )
                    }
                }
            }
        } else if (isLoading) {
            items(3) {
                Box(
                    modifier = Modifier
                        .size(114.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                        .shimmerLoading()
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(114.dp)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent files yet",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendingGridShimmer() {
    val dummyItems = List(12) { it }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
        modifier = Modifier.height(300.dp)
    ) {
        items(dummyItems.chunked(2)) { columnItems ->
            val columnIndex = dummyItems.chunked(2).indexOf(columnItems)
            val isAlternate = columnIndex % 2 == 1
            Column(
                modifier = Modifier.fillMaxHeight().width(126.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                columnItems.forEachIndexed { index, _ ->
                    val weight = when {
                        isAlternate && index == 1 -> 0.3f
                        isAlternate && index == 0 -> 0.7f
                        else -> 0.5f
                    }
                    Box(
                        modifier = Modifier
                            .width(126.dp)
                            .weight(weight)
                            .clip(RoundedCornerShape(8.782.dp))
                            .background(Color(0xFFE8E8E8))
                            .shimmerLoading()
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.shimmerLoading(durationMillis: Int = 1000): Modifier {
    val transition = rememberInfiniteTransition(label = "")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )
    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.LightGray.copy(alpha = 0.2f),
                    Color.LightGray.copy(alpha = 1.0f),
                    Color.LightGray.copy(alpha = 0.2f),
                ),
                start = Offset(x = translateAnimation, y = translateAnimation),
                end = Offset(x = translateAnimation + 100f, y = translateAnimation + 100f),
            )
        )
    }
}