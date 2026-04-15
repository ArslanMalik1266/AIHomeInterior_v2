package com.webscare.interiorismai.ui.Files

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.webscare.interiorismai.data.local.entities.RecentGeneratedEntity
import com.webscare.interiorismai.ui.CreateAndExplore.RoomUiState
import com.webscare.interiorismai.ui.theme.green_btn
import com.webscare.interiorismai.utils.addPressEffect
import com.webscare.interiorismai.utils.addPressEffectWithLongClick
import com.webscare.interiorismai.utils.getImageModel
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.emptyimage
import homeinterior.composeapp.generated.resources.play_fair_italic
import homeinterior.composeapp.generated.resources.roomplaceholder
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentContent(
    state: RoomUiState,
    generatedBundles: List<RecentGeneratedEntity>,
    isFetching: Boolean = false,
    tasksProgress: Map<String, Float> = emptyMap(),
    onBundleClick: (RecentGeneratedEntity) -> Unit,
    onDeleteBundles: (List<Long>) -> Unit // New parameter for deletion logic
) {
    val selectedIds = remember { mutableStateListOf<Long>() }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    // Automatically show/hide the bottom sheet based on selection
    LaunchedEffect(selectedIds.size) {
        if (selectedIds.isNotEmpty()) {
            scaffoldState.bottomSheetState.expand()
        } else {
            scaffoldState.bottomSheetState.hide()
        }
    }

    val gridState = rememberLazyGridState()
    val isScrolled by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        containerColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContainerColor = Color.White,
        sheetShadowElevation = 8.dp,
        sheetSwipeEnabled = false,
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 4.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFDDDDDD))
            )
        },
        sheetContent = {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${selectedIds.size} Selected",
                        color = Color(0xFF333333),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium

                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFEDED))
                            .addPressEffectWithLongClick(onClick = {
                                onDeleteBundles(selectedIds.toList())
                                selectedIds.clear()
                            })
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete selected",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Delete",
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (generatedBundles.isEmpty() && !isFetching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.emptyimage),
                        contentDescription = null,
                        modifier = Modifier.size(140.dp)
                    )
                }
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isFetching) {
                        items(state.activeTasksCount) { index ->
                            val taskList = tasksProgress.values.toList()
                            val currentProgress = taskList.getOrNull(index) ?: 0f
                            val animatedProgress by animateFloatAsState(
                                targetValue = currentProgress,
                                animationSpec = tween(900)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(145.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(Color(0xFFE8E8E8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        progress = { animatedProgress },
                                        color = Color(0xFF222222),
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Text(text = "${(animatedProgress * 100).toInt()}%")
                                }
                            }
                        }
                    }

                    items(generatedBundles) { bundle ->
                        val isSelected = selectedIds.contains(bundle.id)
                        BundleCard(
                            bundle = bundle,
                            isSelected = isSelected,
                            isInSelectionMode = selectedIds.isNotEmpty(),
                            onClick = {
                                if (selectedIds.isNotEmpty()) {
                                    if (selectedIds.contains(bundle.id)) {
                                        // If already selected, remove it (Deselect)
                                        selectedIds.remove(bundle.id)
                                    } else {
                                        // If not selected, add it
                                        selectedIds.add(bundle.id)
                                    }

                                } else {
                                    onBundleClick(bundle)
                                }
                            },
                            onLongClick = {
                                if (!isSelected) selectedIds.add(bundle.id)
                            }
                        )
                    }
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
        }
    }
}

@Composable
private fun BundleCard(
    bundle: RecentGeneratedEntity,
    isSelected: Boolean,
    isInSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val imageCount = bundle.localPaths.size
    val extraImages = imageCount - 1

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .addPressEffectWithLongClick(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .clip(RoundedCornerShape(11.dp))
            .then(
                if (isSelected) Modifier.border(2.dp, green_btn, RoundedCornerShape(11.dp))
                else Modifier
            )
    ) {
        if (imageCount > 0) {
            SingleImageCover(bundle.localPaths[0], bundle.imageUrls.getOrNull(0))
        } else {
            Image(
                painter = painterResource(Res.drawable.roomplaceholder),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Gradients and Labels
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.6f)
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 8.dp)
        ) {
            if (!bundle.roomType.isNullOrBlank()) {
                Text(
                    text = bundle.roomType.uppercase(),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    lineHeight = 1.sp,
                )
            }
            val myFont = FontFamily(Font(Res.font.play_fair_italic))
            if (!bundle.style.isNullOrBlank()) {
                Text(
                    text = bundle.style,
                    fontSize = 14.sp,
                    fontFamily = myFont,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 14.sp,
                )
            }
        }

        // Selection Indicator Icon
        AnimatedVisibility(
            visible = isInSelectionMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) green_btn else Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        if (extraImages > 0 && !isInSelectionMode) {
            Surface(
                color = green_btn,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "+$extraImages",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleImageCover(localPath: String?, url: String?) {
    AsyncImage(
        model = getImageModel(localPath) ?: url,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(Res.drawable.roomplaceholder),
        error = painterResource(Res.drawable.roomplaceholder)
    )
}