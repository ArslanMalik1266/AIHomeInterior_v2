package com.webscare.interiorismai.ui.Generate.UiScreens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.webscare.interiorismai.domain.model.InteriorStyle
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.close
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.jetbrains.compose.resources.painterResource



@Composable
fun StyleSelectionScreen(
    styles: List<com.webscare.interiorismai.domain.model.InteriorStyle>,
    selectedStyleName: String?,
    searchQuery: String,
    isSearchExpanded: Boolean,
    selectedStyleId: Int?,
    onStyleSelected: (Int, String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchExpandedChange: (Boolean) -> Unit
) {
    LaunchedEffect(styles, selectedStyleName) {
        if (selectedStyleName == null && styles.isNotEmpty()) {
            val firstStyle = styles.first()
            onStyleSelected(firstStyle.id, firstStyle.name)
        }
    }

        val listState = rememberLazyGridState()
    val isLastItemVisible by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == styles.size - 1
        }
    }

    val filteredStyles = remember(styles, searchQuery) {
        if (searchQuery.isBlank()) {
            styles
        } else {
            styles.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            HeaderWithSearch(
                title = "Style",
                searchText = searchQuery,
                isSearchExpanded = isSearchExpanded,
                onSearchTextChange = onSearchQueryChange,
                onSearchExpandedChange = onSearchExpandedChange
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (styles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Styles Found", color = Color.Gray)
                }
            }
            else {
            LazyVerticalGrid(
                state = listState,
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredStyles) { style ->
                    StyleCard(
                        style = style,
                        isSelected = style.id == selectedStyleId,
                        onClick = {
                            println("DEBUG_UI: Clicking Card. Style Name: ${style.name}, Style ID: ${style.id}")
                            onStyleSelected(style.id, style.name) },
                    )
                }
            }
        }
        }
        AnimatedVisibility(
            visible = !isLastItemVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x00FFFFFF),
                                Color(0xF5FFFFFF)
                            ),
                            startY = 0f,
                            endY = 900f
                        )
                    )
            )
        }
    }
}


@Composable
private fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF8F8F8).copy(alpha = 0.72f))
            .border(
                width = 0.5.dp,
                color = Color(0xFFE3E3E3),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (searchText.isEmpty()) {
                Text(
                    text = "Search here...",
                    fontSize = 14.sp,
                    color = Color(0xFFAFABAB),
                    fontWeight = FontWeight.Normal
                )
            } else {
                Text(
                    text = searchText,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            }

            Image(
                painter = painterResource(Res.drawable.close),
                contentDescription = "Clear",
                colorFilter = ColorFilter.tint(color = Color(0xFFB2B0B0)),
                modifier = Modifier
                    .size(11.dp)
                    .clickable { onClearSearch() }
            )
        }
    }
}

@Composable
private fun StyleCard(
    style: InteriorStyle,
    isSelected: Boolean,
    onClick: (InteriorStyle) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(11.dp))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color(0xFFCBE0A7) else Color.Transparent,
                shape = RoundedCornerShape(11.dp)
            )
            .clickable {
                onClick(style)
            }    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(style.image)
                .crossfade(true)
                .build(),
            placeholder = painterResource(Res.drawable.roomplaceholder),
            error = painterResource(Res.drawable.roomplaceholder),
            contentDescription = style.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.7f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.8f)
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = style.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
