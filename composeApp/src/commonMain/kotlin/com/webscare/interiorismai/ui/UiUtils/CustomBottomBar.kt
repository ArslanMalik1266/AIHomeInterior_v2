package com.webscare.interiorismai.ui.UiUtils


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data Classes
data class SlippyBadgeStyle(
    val backgroundColor: Color,
    val contentColor: Color,
)

data class SlippyBar(
    val barStyle: SlippyBarStyle? = null,
    val textStyle: SlippyTextStyle? = null,
    val iconStyle: SlippyIconStyle? = null,
    val badgeStyle: SlippyBadgeStyle? = null,
    val startIndex: Int = 0,
    val animationMillis: Int = 250
) {
    init {
        if (SlippyOptions.currentPage.value == null) {
            SlippyOptions.currentPage.value = startIndex
        }
    }
}

data class SlippyBarStyle(
    val backgroundColor: Color
)

data class SlippyIconStyle(
    val disabledIconColor: Color,
    val enabledIconColor: Color
)

data class SlippyTextStyle(
    val enabledTextColor: Color,
    val disabledTextColor: Color,
    val textSize: TextUnit = 12.sp
)

data class SlippyTab(
    val name: String,
    val icon: Painter,
    val enableBadge: Boolean = false,
    val selectedIcon: Painter? = null,
    val badgeCount: Int? = null,
    val action: (() -> Unit)? = null
)

// Options
object SlippyOptions {
    var currentPage = mutableStateOf<Int?>(null)
}

// Exceptions
internal enum class ExceptionMessage(val message: String) {
    TABS_EMPTY_MESSAGE("To use Slippy-Bottom-Bar, the slippy-tab type list you provide as a parameter must not be empty."),
    START_INDEX_GREATER_MESSAGE("StartIndex variable cannot be greater than the size of the slippy tabs list.")
}

internal data class SlippyTabsException(
    override val message: String?,
    override val cause: Throwable? = null
) : Exception()

// Main Composable
@Throws(SlippyTabsException::class)
@Composable
fun SlippyBottomBar(
    bar: SlippyBar,
    tabs: List<SlippyTab>,
    iconSize: Dp = 24.dp,
    badgeTextSize: TextUnit = 10.sp,
    selectedIndex: Int? = null,
    modifier: Modifier = Modifier,
    fabIndex: Int = -1
) {
    if (tabs.isEmpty()) {
        throw SlippyTabsException(message = ExceptionMessage.TABS_EMPTY_MESSAGE.message)
    }

    val currentIndex = selectedIndex ?: SlippyOptions.currentPage.value ?: bar.startIndex

    if (currentIndex > tabs.lastIndex) {
        throw SlippyTabsException(message = ExceptionMessage.START_INDEX_GREATER_MESSAGE.message)
    }

    val barPadding = remember {
        PaddingValues(top = 12.dp, bottom = 6.dp)
    }

    val animateIconColor: @Composable (Int) -> State<Color> = { index ->
        animateColorAsState(
            animationSpec = tween(
                durationMillis = bar.animationMillis,
                easing = FastOutLinearInEasing
            ),
            targetValue = if (currentIndex == index) {
                bar.iconStyle?.enabledIconColor ?: Color(0xFF6200EE)
            } else {
                bar.iconStyle?.disabledIconColor ?: Color(0xFF757575)
            },
            label = "iconColor"
        )
    }

    Row(
        modifier = modifier
            .background(
                color = bar.barStyle?.backgroundColor ?: Color.White,
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Max)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index: Int, page: SlippyTab ->
            if (index == fabIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(weight = 1.0F, fill = true)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(weight = 1.0F, fill = true)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (currentIndex != index) {
                                if (selectedIndex == null) {
                                    SlippyOptions.currentPage.value = index
                                }
                                page.action?.invoke()
                            }
                        }
                        .padding(paddingValues = barPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    if (page.enableBadge) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = bar.badgeStyle?.backgroundColor
                                        ?: Color(0xFFFF0000),
                                    contentColor = bar.badgeStyle?.contentColor
                                        ?: Color.White,
                                    content = {
                                        Text(
                                            text = page.badgeCount.toString(),
                                            fontSize = badgeTextSize
                                        )
                                    }
                                )
                            }
                        ) {
                            GetTabIcon(
                                animateIconColor = animateIconColor.invoke(index).value,
                                page = page,
                                iconSize = iconSize,
                                isSelected = currentIndex == index
                            )
                        }
                    } else {
                        GetTabIcon(
                            animateIconColor = animateIconColor.invoke(index).value,
                            page = page,
                            iconSize = iconSize,
                            isSelected = currentIndex == index
                        )
                    }

                    val animateTextColor: Color by animateColorAsState(
                        animationSpec = tween(
                            durationMillis = bar.animationMillis,
                            easing = FastOutLinearInEasing
                        ),
                        targetValue = if (currentIndex == index) {
                            bar.textStyle?.enabledTextColor ?: Color(0xFF6200EE)
                        } else {
                            bar.textStyle?.disabledTextColor ?: Color(0xFF757575)
                        },
                        label = "textColor"
                    )

                    Text(
                        text = page.name,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = animateTextColor,
                        fontSize = bar.textStyle?.textSize ?: 12.sp
                    )
                }
            }
        }
    }
}


@Composable
internal fun GetTabIcon(
    animateIconColor: Color,
    page: SlippyTab,
    iconSize: Dp,
    isSelected: Boolean
) {
    Image(
        modifier = Modifier.size(size = iconSize),
        painter = if (isSelected && page.selectedIcon != null) page.selectedIcon else page.icon,
        contentDescription = page.name
    )
}