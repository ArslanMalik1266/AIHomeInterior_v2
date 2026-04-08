package com.webscare.interiorismai.ui.Generate.UiScreens


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.close
import homeinterior.composeapp.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import com.webscare.interiorismai.ui.theme.fieldBack


@Composable
fun RoomTypeSelection(
    roomTypes: List<String>,
    selectedRoomType: String?,
    searchQuery: String,
    isSearchExpanded: Boolean,
    onRoomTypeSelected: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchExpandedChange: (Boolean) -> Unit
) {
    LaunchedEffect(roomTypes, selectedRoomType) {
        if (selectedRoomType == null && roomTypes.isNotEmpty()) {
            onRoomTypeSelected(roomTypes.first())
        }
    }
    val listState = rememberLazyListState()

    val filteredRoomTypes = remember(roomTypes, searchQuery) {
        if (searchQuery.isBlank()) {
            roomTypes
        } else {
            roomTypes.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    val isLastItemVisible by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == roomTypes.size - 1
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize().background(Color.White)
    ) {
        HeaderWithSearch(
            title = "Type of Room",
            searchText = searchQuery,
            isSearchExpanded = isSearchExpanded,
            onSearchTextChange = onSearchQueryChange,
            onSearchExpandedChange = onSearchExpandedChange
        )


        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 90.dp)
            ) {
                items(filteredRoomTypes) { roomType ->
                    RoomTypeItem(
                        roomName = roomType,
                        isSelected = roomType == selectedRoomType,
                        onClick = { onRoomTypeSelected(roomType) }
                    )

                    if (roomType != filteredRoomTypes.last()) {
                        Divider(
                            color = Color(0xFFDBDBDB),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
            AnimatedContent(
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                targetState = !isLastItemVisible,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(311.dp)
                    .align(Alignment.BottomCenter),
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

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderWithSearch(
    title: String,
    searchText: String,
    isSearchExpanded: Boolean,
    onSearchTextChange: (String) -> Unit,
    onSearchExpandedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = !isSearchExpanded,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(end = 16.dp, top = 12.5.dp, bottom = 12.dp)
            )
        }

        Box(
            modifier = Modifier
                .then(
                    if (isSearchExpanded) Modifier.weight(1f)
                    else Modifier.size(32.dp)
                )
                .height(if (isSearchExpanded) 50.dp else 32.dp)
                .clip(if (isSearchExpanded) RoundedCornerShape(10.dp) else CircleShape)
                .background(if (isSearchExpanded) fieldBack else Color(0xFFF7F7F7))
                .clickable(
                    enabled = !isSearchExpanded,
                    onClick = { onSearchExpandedChange(true) }
                ),
            contentAlignment = if (isSearchExpanded) Alignment.CenterStart else Alignment.Center
        ) {
            if (isSearchExpanded) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { onSearchTextChange(it) },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.16.sp,
                        color = Color.Black
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE3E3E3),
                            shape = RoundedCornerShape(8.dp)
                        ).clip(RoundedCornerShape(8.dp))
                ) { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = searchText,
                        innerTextField = innerTextField,
                        placeholder = { Text("Search here...", fontSize = 12.sp, color = Color.Gray) },
                        trailingIcon = {
                            Image(
                                painter = painterResource(Res.drawable.close),
                                contentDescription = "Close",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        onSearchTextChange("")
                                        onSearchExpandedChange(false)
                                    },
                                colorFilter = ColorFilter.tint(color = Color(0xFFA0A0A0))
                            )
                        },
                        singleLine = true,
                        enabled = true,
                        interactionSource = remember { MutableInteractionSource() },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        visualTransformation = VisualTransformation.None
                    )
                }
            } else {
                Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                    Image(
                        painter = painterResource(Res.drawable.search),
                        contentDescription = "Search",
                        colorFilter = ColorFilter.tint(color = Color(0xFFA0A0A0)),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

    }
}

@Composable
private fun RoomTypeItem(
    roomName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = roomName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF414040)
            )

            if (isSelected) {
                Box(
                    modifier = Modifier.size(21.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(21.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F0F0))
                    )

                    Box(
                        modifier = Modifier
                            .size(13.53.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFA3B18A))
                    )
                }
            }
        }
    }
}