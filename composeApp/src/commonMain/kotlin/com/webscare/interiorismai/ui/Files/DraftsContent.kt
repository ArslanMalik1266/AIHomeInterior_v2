package com.webscare.interiorismai.ui.Files

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.jetbrains.compose.resources.painterResource
import com.webscare.interiorismai.data.local.entities.DraftEntity
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.utils.addPressEffect

@Composable
fun DraftsContent(
    viewModel: RoomsViewModel,
    onImageClick: (DraftEntity) -> Unit
) {

    val gridState = rememberLazyGridState()
    val isScrolled by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0
        }
    }
    val drafts by viewModel.draftImages.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        if (drafts.isEmpty()) {
            // Empty state dikha sakte hain
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

                    coil3.compose.AsyncImage(
                        model = draft.userImageBytes,
                        contentDescription = "Draft $index",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                            .addPressEffect { onImageClick(draft) }
                            .clip(RoundedCornerShape(11.dp))
                        ,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(Res.drawable.roomplaceholder)
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