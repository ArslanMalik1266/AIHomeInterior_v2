package com.webscare.interiorismai.ui.Generate.UiScreens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.jetbrains.compose.resources.painterResource
import com.webscare.interiorismai.data.local.entities.RecentGeneratedEntity
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.ui.UiUtils.CloseIconButton
import com.webscare.interiorismai.utils.GenerationStatus
import com.webscare.interiorismai.utils.addPressEffect
import com.webscare.interiorismai.utils.getImageModel

@Composable
fun ResultScreen(
    viewModel: RoomsViewModel,
    generatedImages: List<RecentGeneratedEntity>,
    onCloseClick: () -> Unit = {},
    onBackClick: () -> Unit,
    isFetchingImages: Boolean = false,
    imageEtaSeconds: List<Int> = emptyList(),
    generatedCount: Int = 3,
    onImageClick: (Int) -> Unit,
) {
    val tasksProgress by viewModel.tasksProgress.collectAsState()
    val state by viewModel.state.collectAsState()
    val selectedBundleId by viewModel.selectedBundleId.collectAsState()
    val currentBundle = state.generatedImagesEntity.firstOrNull { it.bundleId == selectedBundleId }
        ?: state.generatedImagesEntity.firstOrNull()
    val currentTaskId = currentBundle?.bundleId
    val currentProgress = tasksProgress[currentTaskId] ?: 0f
    val paths = currentBundle?.localPaths ?: emptyList()
    val urls = currentBundle?.imageUrls ?: emptyList()
    val tasksStatus by viewModel.tasksStatus.collectAsState()
    val isCurrentBundleFetching = currentTaskId != null &&
            tasksProgress.containsKey(currentTaskId) &&
            tasksStatus[currentTaskId] == GenerationStatus.RUNNING


    println("DEBUG_PATHS: paths = $paths")
    println("DEBUG_URLS: urls = $urls")
    println("DEBUG_BUNDLE: selectedBundleId = $selectedBundleId")
    println("DEBUG_BUNDLE: currentBundle bundleId = ${currentBundle?.bundleId}")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CloseIconButton(iconSize = 20.dp) {
                onCloseClick()
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Results",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C2C2C)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 2000.dp)
                .padding(horizontal = 24.dp)
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().heightIn(max = 2000.dp),
                contentPadding = PaddingValues(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                userScrollEnabled = false
            ) {
                // DOWNLOADED IMAGES
                itemsIndexed(
                    items = paths,
                    key = { index, path -> "image_${path}_$index" },
                    span = { index, _ ->
                        if ((index + 1) % 3 == 0) StaggeredGridItemSpan.FullLine
                        else StaggeredGridItemSpan.SingleLane
                    }
                ) { index, path ->
                    println("🔍 Image $index: path='$path', url='${urls.getOrNull(index)}'")
                    val imageModel = getImageModel(path) ?: urls.getOrNull(index) ?: ""
                    println("🔍 Final model: $imageModel")
                    ImageCard(
                        imageUrl = imageModel,
                        isLarge = (index + 1) % 3 == 0,
                        modifier = Modifier.addPressEffect { onImageClick(index) }
                    )
                }

                // LOADING BOXES
                    if (isFetchingImages) {
                        // ✅ NAYA — generatedCount 0 ho tab bhi 3 use karo
                        val effectiveCount = if (generatedCount <= 0) 3 else generatedCount
                        val remainingCount = (effectiveCount - paths.size).coerceAtLeast(0)
                        items(count = remainingCount , key = { it },   span = { index ->                                    // ✅ YEH ADD KARO
                            val globalIndex = paths.size + index
                            if ((globalIndex + 1) % 3 == 0) StaggeredGridItemSpan.FullLine
                            else StaggeredGridItemSpan.SingleLane
                        }) { index ->
                            val globalIndex = paths.size + index
                            val isLarge = (globalIndex + 1) % 3 == 0

                            EtaLoadingBox(
                                progressValue = currentProgress,
                                modifier = Modifier.fillMaxWidth().then(
                                    if (isLarge) Modifier.height(176.dp) else Modifier.aspectRatio(1f)
                                )
                            )
                        }
                    }
            }
        }


        if (isCurrentBundleFetching) {
            ExploreSection { onBackClick() }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun ImageCard(
    imageUrl: Any?,
    isLarge: Boolean,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Room design",
        modifier = modifier
            .then(
                if (isLarge) Modifier.fillMaxWidth().height(176.dp)
                else Modifier.aspectRatio(1f)
            )
            .clip(RoundedCornerShape(9.dp))
            .border(1.dp, Color(0xFFCFCFCF), RoundedCornerShape(9.dp)),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(Res.drawable.roomplaceholder),
        error = painterResource(Res.drawable.roomplaceholder)
    )
}

@Composable
private fun EtaLoadingBox(
    progressValue: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(targetValue = progressValue, animationSpec = tween(900))

    Box(
        modifier = modifier.clip(RoundedCornerShape(9.dp)).background(Color(0xFFE8E8E8)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 1f },
                    color = Color(0xFFD0D0D0),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.size(48.dp)
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    color = Color(0xFF222222),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("${(animatedProgress * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ExploreSection(onHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Explore while your images are generating",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onHome,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4F7BD)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(45.dp).padding(horizontal = 24.dp)
        ) {
            Text("Go to Home", color = Color.Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp), tint = Color.Black)
        }
    }
}