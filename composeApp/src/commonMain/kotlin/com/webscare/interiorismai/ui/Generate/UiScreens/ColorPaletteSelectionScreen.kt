package com.webscare.interiorismai.ui.Generate.UiScreens


    import androidx.compose.animation.AnimatedContent
    import androidx.compose.animation.fadeIn
    import androidx.compose.animation.fadeOut
    import androidx.compose.animation.togetherWith
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.rememberLazyListState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.draw.shadow
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.unit.dp


    data class ColorPalette(
        val colors: List<Color>,
        val name: String,
        val id: Int
    )

    @Composable
    fun ColorPaletteSelectionScreen(
        palettes: List<ColorPalette>,
        selectedPaletteId: Int?,
        onPaletteSelected: (Int) -> Unit,
    ) {
        LaunchedEffect(palettes, selectedPaletteId) {
            if (selectedPaletteId == null && palettes.isNotEmpty()) {
                onPaletteSelected(palettes.first().id)
            }
        }

        val listState = rememberLazyListState()

        val isLastItemVisible by remember {
            derivedStateOf {
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index == palettes.size - 1
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
                    title = "Color Palette",
                    searchText = "",
                    isSearchExpanded = false,
                    onSearchTextChange = {},
                    onSearchExpandedChange = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(palettes.size) { index ->
                            ColorPaletteRow(

                                palette = palettes[index],
                                isSelected = palettes[index].id == selectedPaletteId,
                                onClick = {
                                    println("DEBUG_PALETTE: Clicked palette id=${palettes[index].id}, name=${palettes[index].name}")
                                    println("DEBUG_PALETTE: Current selectedPaletteId=$selectedPaletteId")
                                    onPaletteSelected(palettes[index].id) }
                            )
                        }

                    }

                    AnimatedContent(
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        targetState = !isLastItemVisible,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .align(Alignment.BottomCenter)
                    ){
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

        }
    }


    @Composable
    private fun ColorPaletteRow(
        palette: ColorPalette,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(9.dp))
                .background(Color.White)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFFCBE0A7) else Color(0xFFE7E7E7),
                    shape = RoundedCornerShape(9.dp)
                )
                .clickable(onClick = onClick)
                .padding(vertical = 15.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly, // Added Alignment.CenterHorizontally
                verticalAlignment = Alignment.CenterVertically
            ) {
                palette.colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(
                                elevation = 14.dp,
                                shape = CircleShape,
                                spotColor = Color.Black.copy(alpha = 0.13f)
                            )
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }