package com.webscare.interiorismai.ui.UiUtils


import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.alert
import homeinterior.composeapp.generated.resources.checked
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

sealed class SnackbarType {
    object Success : SnackbarType()
    object Error : SnackbarType()
}

@Composable
fun rememberCustomSnackbarState(): CustomSnackbarState {
    return remember { CustomSnackbarState() }
}

class CustomSnackbarState {
    private val _message = mutableStateOf<String?>(null)
    val message: State<String?> = _message

    private val _type = mutableStateOf<SnackbarType?>(null)
    val type: State<SnackbarType?> = _type

    var updateState by mutableStateOf(false)
        private set

    fun showSuccess(message: String) {
        _message.value = message
        _type.value = SnackbarType.Success
        updateState = !updateState
    }

    fun showError(message: String) {
        _message.value = message
        _type.value = SnackbarType.Error
        updateState = !updateState
    }

    fun isNotEmpty(): Boolean {
        return _message.value != null
    }

    fun clear() {
        _message.value = null
        _type.value = null
    }
}

@Composable
fun CustomSnackbar(
    modifier: Modifier = Modifier,
    state: CustomSnackbarState,
    duration: Long = 3000L,
) {
    var showSnackbar by remember { mutableStateOf(false) }
    val message by rememberUpdatedState(newValue = state.message.value)
    val type by rememberUpdatedState(newValue = state.type.value)

    LaunchedEffect(state.updateState) {
        if (state.isNotEmpty()) {
            showSnackbar = true
            delay(duration)
            showSnackbar = false
            state.clear()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = state.isNotEmpty() && showSnackbar && type != null,
            enter = slideInVertically(
                animationSpec = tween(300),
                initialOffsetY = { -it }
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                animationSpec = tween(300),
                targetOffsetY = { -it }
            ) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .statusBarsPadding()
        ) {
            if (type != null && message != null) {
                SnackbarContent(
                    message = message!!,
                    type = type!!
                )
            }
        }
    }
}

@Composable
private fun SnackbarContent(
    message: String,
    type: SnackbarType,
) {
    val backgroundColor = when (type) {
        SnackbarType.Success -> Color(0xFFF1F8F6)
        SnackbarType.Error -> Color(0xFFFEF2F1)
    }

    val dividerColor = when (type) {
        SnackbarType.Success -> Color(0xFF19B661)
        SnackbarType.Error -> Color(0xFFE8503A)
    }

    val icon = when (type) {
        SnackbarType.Success -> Res.drawable.checked
        SnackbarType.Error -> Res.drawable.alert
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Divider
        Box(
            modifier = Modifier
                .width(7.dp)
                .fillMaxHeight()
                .background(
                    color = dividerColor,
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        bottomStart = 8.dp
                    )
                )
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Icon
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = dividerColor),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Message Text
        Text(
            text = message,
            color = Color(0xFF1A1A1A),
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.20.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp, horizontal = 8.dp)
        )
    }
}