package com.webscare.interiorismai.ui.CreateAndExplore.Explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.webscare.interiorismai.domain.model.InteriorStyle
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.keyboard_arrow_down_24px
import homeinterior.composeapp.generated.resources.keyboard_arrow_up_24px
import org.jetbrains.compose.resources.painterResource
import com.webscare.interiorismai.ui.CreateAndExplore.FilterSection
import com.webscare.interiorismai.ui.CreateAndExplore.FilterState
import com.webscare.interiorismai.ui.Generate.UiScreens.ColorPalette

@Composable
fun FilterBottomSheetContent(
    filterState: FilterState,
    filterCount: Int,
    expandedRoomType: Boolean,
    expandedStyle: Boolean,
    expandedColor: Boolean,
    expandedFormat: Boolean,
    expandedPrice: Boolean,
    availableRoomTypes: List<String>,
    availableStyles: List<InteriorStyle>,
    availableColors: List<ColorPalette>,
    onFilterStateChange: (FilterState) -> Unit,
    onToggleSection: (FilterSection) -> Unit,
    expandedSection: FilterSection?,
    onApplyFilters: () -> Unit,
    onCancel: () -> Unit,
    onClearAll: () -> Unit
) {
    var activeSection by remember { mutableStateOf<FilterSection?>(null) }
    val primaryGreen = Color(0xFFA3B18A)
    val darkText = Color(0xFF2C2C2C)
    val mediumText = Color(0xFF323232)
    val lightText = Color(0xFF4D4D4D)
    val borderGray = Color(0xFF7D7A7A)
    val dividerGray = Color(0xFFBBBBBB)
    val cancelTextColor = Color(0xFF8C8989)
    val cancelBorderColor = Color(0xFFE1DDDD)
    val whiteText = Color(0xFFFEF7F7)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()

            .background(Color.White)
            .padding(horizontal = 21.dp)
            .navigationBarsPadding()
    )
    {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Filters",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkText
                    )
                    Text(
                        text = " ($filterCount)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryGreen
                    )
                }
                Text(
                    text = "Clear all",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = primaryGreen,
                    modifier = Modifier.clickable { onClearAll() }
                )
            }


            ExpandableFilterSection(
                title = "Type of Room",
                expanded = expandedSection == FilterSection.ROOM_TYPE,

                onExpandChange = { onToggleSection(FilterSection.ROOM_TYPE) },
                dividerColor = dividerGray,
                titleColor = mediumText
            ) {
                RoomTypeOptions(
                    selectedOptions = filterState.selectedRoomTypes,
                    availableOptions = availableRoomTypes,
                    onOptionsSelected = { onFilterStateChange(filterState.copy(selectedRoomTypes = it)) },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }



            ExpandableFilterSection(
                title = "Style",
                expanded = expandedSection == FilterSection.STYLE,
                onExpandChange = { onToggleSection(FilterSection.STYLE) },
                dividerColor = dividerGray,
                titleColor = mediumText
            ) {
                StyleOptions(
                    selectedOptions = filterState.selectedStyles,
                    availableStyles = availableStyles,
                    onOptionsSelected = { onFilterStateChange(filterState.copy(selectedStyles = it)) },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }



            ExpandableFilterSection(
                title = "Color",
                expanded = expandedSection == FilterSection.COLOR,
                onExpandChange = { onToggleSection(FilterSection.COLOR) },
                dividerColor = dividerGray,
                titleColor = mediumText
            ) {
                ColorOptions(
                    selectedPaletteIds = filterState.selectedColors, // Changed parameter name
                    availablePalettes = availableColors, // Pass dynamic data
                    onPalettesSelected = { onFilterStateChange(filterState.copy(selectedColors = it)) },
                    primaryGreen = primaryGreen
                )
            }


//        item {
//            ExpandableFilterSection(
//                title = "By Format",
//                expanded = activeSection == FilterSection.COLOR,
//                onExpandChange = { onToggleSection(FilterSection.FORMAT) },
//                dividerColor = dividerGray,
//                titleColor = mediumText
//            ) {
//                FormatOptions(
//                    selectedOptions = filterState.selectedFormats,
//                    onOptionsSelected = { onFilterStateChange(filterState.copy(selectedFormats = it)) },
//                    primaryGreen = primaryGreen,
//                    borderGray = borderGray,
//                    lightText = lightText
//                )
//            }
//        }

//        item {
//            ExpandableFilterSection(
//                title = "Price",
//                expanded = expandedPrice,
//                onExpandChange = { onToggleSection(FilterSection.PRICE) },
//                dividerColor = dividerGray,
//                titleColor = mediumText
//            ) {
//                PriceOptions(
//                    selectedOptions = filterState.selectedPrices,
//                    onOptionsSelected = { onFilterStateChange(filterState.copy(selectedPrices = it)) },
//                    primaryGreen = primaryGreen,
//                    borderGray = borderGray,
//                    lightText = lightText
//                )
//            }
//        }


            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onApplyFilters,
                    modifier = Modifier.padding(end = 10.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                ) {
                    Text(
                        text = "Apply Filters",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = whiteText,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.padding(vertical = 3.dp, horizontal = 4.dp)
                    )
                }

                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, cancelBorderColor),
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cancelTextColor,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.padding(vertical = 3.dp, horizontal = 4.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

    }
}

@Composable
fun ExpandableFilterSection(
    title: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    dividerColor: Color,
    titleColor: Color,
    content: @Composable () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenHeight = windowInfo.containerSize.height  // in pixels
    val maxContentHeight = (screenHeight * 0.5f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .clickable { onExpandChange(!expanded) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
            )

            Image(
                painter = painterResource(if (expanded) Res.drawable.keyboard_arrow_up_24px else Res.drawable.keyboard_arrow_down_24px),
                contentDescription = null,
                modifier = Modifier
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = shrinkVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = with(LocalDensity.current) { maxContentHeight.toDp() })
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                content()
            }
        }
    }
}

@Composable
fun RadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    primaryGreen: Color,
    borderGray: Color,
    lightText: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .padding(start = 5.dp, top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .border(0.7.dp, borderGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(primaryGreen, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = lightText,
            )
        }
    }
}

@Composable
fun RoomTypeOptions(
    selectedOptions: Set<String>,
    availableOptions: List<String>, // Add this parameter
    onOptionsSelected: (Set<String>) -> Unit,
    primaryGreen: Color,
    borderGray: Color,
    lightText: Color
) {
    val allOptions = listOf("All") + availableOptions
    val optionsWithoutAll = allOptions.filter { it != "All" }


    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        maxItemsInEachRow = 2
    ) {
        allOptions.forEach { option ->
            Box(modifier = Modifier.weight(1f)) {
                RadioOption(
                    text = option,
                    selected = selectedOptions.contains(option),
                    onClick = {
                        val newSelection = when (option) {
                            "All" -> {
                                // Agar 'All' pehle se select hai, toh sab ko unselect (khali) kardo
                                // Agar select nahi hai, toh saari list ko select kardo
                                if (selectedOptions.contains("All")) emptySet() else allOptions.toSet()
                            }

                            else -> {
                                // Kisi specific option (e.g., Modern) par click kiya gaya
                                val currentWithoutAll = selectedOptions - "All"

                                val updated = if (currentWithoutAll.contains(option)) {
                                    currentWithoutAll - option // Pehle se tha toh remove kardo
                                } else {
                                    currentWithoutAll + option // Nahi tha toh add kardo
                                }

                                // Check karein: Kya saare individual options select ho chuke hain?
                                // Agar haan, toh 'All' ko bhi highlight kardo
                                if (updated.size == optionsWithoutAll.size) updated + "All" else updated
                            }
                        }
                        onOptionsSelected(newSelection)
                    },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }
        }
    }
}

@Composable
fun StyleOptions(
    selectedOptions: Set<String>,
    availableStyles: List<InteriorStyle>,
    onOptionsSelected: (Set<String>) -> Unit,
    primaryGreen: Color,
    borderGray: Color,
    lightText: Color
) {
    val dynamicStyleNames = availableStyles.map { it.name }
    val allOptions = listOf("All") + dynamicStyleNames
    val optionsWithoutAll = allOptions.filter { it != "All" }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        maxItemsInEachRow = 2
    ) {
        allOptions.forEach { option ->
            Box(modifier = Modifier.weight(1f)) {
                RadioOption(
                    text = option,
                    selected = selectedOptions.contains(option),
                    onClick = {
                        val newSelection = when (option) {
                            "All" -> {
                                // Agar 'All' pehle se select hai to emptySet() yani sab ko khatam kardo
                                // Agar select nahi hai to allOptions.toSet() yani sab ko select kardo
                                if (selectedOptions.contains("All")) emptySet() else allOptions.toSet()
                            }

                            else -> {
                                // Kisi specific item (e.g. "Living Room") par click hua
                                val currentWithoutAll = selectedOptions - "All"
                                val updated = if (currentWithoutAll.contains(option)) {
                                    currentWithoutAll - option // Pehle se tha to remove kardo
                                } else {
                                    currentWithoutAll + option // Nahi tha to add kardo
                                }

                                // Agar user ne saare items select kar liye hain, to 'All' ko bhi highlight kardo
                                if (updated.size == optionsWithoutAll.size) updated + "All" else updated
                            }
                        }
                        onOptionsSelected(newSelection)
                    },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }
        }
    }
}

@Composable
fun ColorOptions(
    selectedPaletteIds: Set<Int>, // Changed to palette IDs
    availablePalettes: List<ColorPalette>, // Add this parameter
    onPalettesSelected: (Set<Int>) -> Unit, // Changed to IDs
    primaryGreen: Color
) {


    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Do boxes ke beech ka gap
        verticalArrangement = Arrangement.spacedBy(10.dp),  // Rows ke beech ka gap
        maxItemsInEachRow = 2 // Ek row mein sirf 2 items ayenge
    ) {
        availablePalettes.forEach { palette ->
            val isSelected = selectedPaletteIds.contains(palette.id)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) primaryGreen else Color(0xFFE5E5E5),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val newSelection = if (isSelected) {
                            selectedPaletteIds - palette.id
                        } else {
                            selectedPaletteIds + palette.id
                        }
                        onPalettesSelected(newSelection)
                    }
                    .padding(8.dp)
            ) {
                Text(
                    text = palette.name ?: "Unnamed Palette",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4D4D4D),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp), // Circles ka size 30dp hai
                    contentAlignment = Alignment.CenterStart
                ) {
                    palette.colors.reversed().forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .offset(x = (index * 16).dp) // Jitna overlap chahiye x-offset kam rakhein
                                .size(24.dp)
                                .background(color, CircleShape)
                                .border(
                                    1.5.dp,
                                    Color.White,
                                    CircleShape
                                ) // Border overlapping ko clear dikhati hai
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FormatOptions(
    selectedOptions: Set<String>,
    onOptionsSelected: (Set<String>) -> Unit,
    primaryGreen: Color,
    borderGray: Color,
    lightText: Color
) {
    val allOptions = listOf("All", "JPEG", "PNG", "PDF")
    val optionsWithoutAll = allOptions.filter { it != "All" }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        maxItemsInEachRow = 2
    ) {
        allOptions.forEach { option ->
            Box(modifier = Modifier.weight(1f)) {
                RadioOption(
                    text = option,
                    selected = selectedOptions.contains(option),
                    onClick = {
                        val newSelection = when {
                            option == "All" -> {
                                if (selectedOptions.contains("All")) {
                                    val firstOptionAfterAll = allOptions.getOrNull(1)
                                    if (firstOptionAfterAll != null) {
                                        selectedOptions - "All" - firstOptionAfterAll
                                    } else {
                                        selectedOptions - "All"
                                    }
                                } else {
                                    allOptions.toSet()
                                }
                            }

                            selectedOptions.contains("All") && option != "All" -> {
                                (allOptions.toSet() - "All" - option).ifEmpty { emptySet() }
                            }

                            selectedOptions.contains(option) -> {
                                (selectedOptions - option).ifEmpty { emptySet() }
                            }

                            else -> {
                                val updated = selectedOptions + option
                                if (updated.size == optionsWithoutAll.size) updated + "All" else updated
                            }
                        }
                        onOptionsSelected(newSelection)
                    },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }
        }
    }
}

@Composable
fun PriceOptions(
    selectedOptions: Set<String>,
    onOptionsSelected: (Set<String>) -> Unit,
    primaryGreen: Color,
    borderGray: Color,
    lightText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 5.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f)) {
            RadioOption(
                text = "Free",
                selected = selectedOptions.contains("Free"),
                onClick = {
                    val newSelection = if (selectedOptions.contains("Free")) {
                        selectedOptions - "Free"
                    } else {
                        selectedOptions + "Free"
                    }
                    onOptionsSelected(newSelection)
                },
                primaryGreen = primaryGreen,
                borderGray = borderGray,
                lightText = lightText
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            RadioOption(
                text = "Premium",
                selected = selectedOptions.contains("Premium"),
                onClick = {
                    val newSelection = if (selectedOptions.contains("Premium")) {
                        selectedOptions - "Premium"
                    } else {
                        selectedOptions + "Premium"
                    }
                    onOptionsSelected(newSelection)
                },
                primaryGreen = primaryGreen,
                borderGray = borderGray,
                lightText = lightText
            )
        }
    }
}
