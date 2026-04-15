package com.webscare.interiorismai.ui.Files

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.roomplaceholder
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import com.webscare.interiorismai.data.local.entities.DraftEntity
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.ui.theme.green_btn
import com.webscare.interiorismai.utils.CommonBackHandler
import com.webscare.interiorismai.utils.addPressEffect
import com.webscare.interiorismai.utils.addPressEffectWithLongClick

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DraftsContent(
    viewModel: RoomsViewModel,
    onImageClick: (DraftEntity) -> Unit
) {

    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    val isScrolled by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0
        }
    }

    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<Long>() }
    val drafts by viewModel.draftImages.collectAsState()

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    fun exitSelectionMode() {
        isSelectionMode = false
        selectedIds.clear()
        scope.launch { sheetState.hide() }
    }
    CommonBackHandler(enabled = isSelectionMode) {
        exitSelectionMode()
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
                    text = "${selectedIds.size} selected",
                    fontSize = 15.sp,

                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFEDED))
                        .addPressEffect(
                            onClick = {
                                scope.launch {
                                    selectedIds.toList().forEach { id ->
                                        viewModel.deleteDraftById(id)
                                    }
                                    exitSelectionMode()
                                }
                            }
                        )
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
            Spacer(modifier = Modifier.height(8.dp))
        }
    )
    { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (drafts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No drafts yet", color = Color.Gray)
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
                    items(drafts.size) { index ->
                        val draft = drafts[index]
                        val isSelected = selectedIds.contains(draft.id)

                        val borderAlpha by animateFloatAsState(
                            targetValue = if (isSelected) 1f else 0f,
                            animationSpec = tween(150),
                            label = "border_alpha"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(145.dp)
                        ) {
                            // The image
                            coil3.compose.AsyncImage(
                                model = draft.userImageBytes,
                                contentDescription = "Draft $index",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .addPressEffectWithLongClick(
                                        onClick = {
                                            if (isSelectionMode) {
                                                if (selectedIds.contains(draft.id)) {
                                                    // If already selected, remove it (Deselect)
                                                    selectedIds.remove(draft.id)
                                                } else {
                                                    // If not selected, add it
                                                    selectedIds.add(draft.id)
                                                }

                                                // If the last item was deselected, exit selection mode automatically
                                                if (selectedIds.isEmpty()) {
                                                    exitSelectionMode()
                                                }
                                            } else {
                                                onImageClick(draft)
                                            }
                                        },
                                        onLongClick = {
                                            if (!isSelectionMode) {
                                                isSelectionMode = true
                                                scope.launch { sheetState.expand() }
                                            }
                                            if (!selectedIds.contains(draft.id)) {
                                                selectedIds.add(draft.id)
                                            }
                                        }
                                    ).clip(RoundedCornerShape(11.dp))
                                    .then(
                                        if (isSelected)
                                            Modifier.border(
                                                width = 2.dp,
                                                color = Color(0xFF4A90D9).copy(alpha = borderAlpha),
                                                shape = RoundedCornerShape(11.dp)
                                            )
                                        else Modifier
                                    ),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(Res.drawable.roomplaceholder)
                            )

                            // Dark overlay when selected
                            AnimatedVisibility(
                                visible = isSelected,
                                enter = fadeIn(tween(150)),
                                exit = fadeOut(tween(150)),
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(11.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(alpha = 0.35f))
                                )
                            }

                            // Tick circle — top left, visible when selection mode is ON
                            AnimatedVisibility(
                                visible = isSelectionMode,
                                enter = fadeIn(tween(150)),
                                exit = fadeOut(tween(150)),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(7.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) green_btn
                                            else Color.White.copy(alpha = 0.75f)
                                        )
                                        .then(
                                            if (!isSelected)
                                                Modifier.border(1.5.dp, Color.White, CircleShape)
                                            else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Top fade gradient when scrolled
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