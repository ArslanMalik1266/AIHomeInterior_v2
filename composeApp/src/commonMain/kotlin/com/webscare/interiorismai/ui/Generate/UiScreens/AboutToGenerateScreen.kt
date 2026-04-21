package com.webscare.interiorismai.ui.Generate.UiScreens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.edit_icon
import homeinterior.composeapp.generated.resources.generate
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.ui.CreateAndExplore.RoomEvent
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.ui.UiUtils.CloseIconButton
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import homeinterior.composeapp.generated.resources.add_icon

@Composable
fun AboutToGenerateScreen(
    roomsViewModel: RoomsViewModel = koinViewModel(),
    authViewModel: AuthViewModel,
    onCloseClick: () -> Unit,
    imageUrl: String?,
    fullImageUrl: String? = null,
    compressedImageUrl: String? = null,
    onResult: (shouldOpenGallery: Boolean) -> Unit,
    isFromExplore: Boolean = false,
    isEditable: Boolean = true,

    onSubscriptionClick: () -> Unit,
    onEditType: () -> Unit = {},
    onEditStyle: () -> Unit = {},
    onEditPalette: () -> Unit = {}
) {
    val state by roomsViewModel.state.collectAsState()
    val authState by authViewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isClosing by remember { mutableStateOf(false) }
    val paletteToDisplay = if (state.isFromExplore) {
        state.availableColors.find { it.id == state.selectedPaletteId }
    } else {
        state.selectedPalette ?: state.filterColors.firstOrNull()
    }

    val buttonText = when {
        state.isFromExplore && state.selectedImageBytes == null -> "Upload my Room"
        state.isFromExplore && state.selectedImageBytes != null -> "Generate Design"
        else -> "Generate Design"
    }
    val selectedType = state.selectedRoomType ?: ""
    val selectedStyle = state.selectedStyleName ?: ""
    val selectedImage = state.selectedImage
    val isImageSelected = state.selectedImageBytes != null
    println("DEBUG_ABOUT: selectedPaletteId = ${state.selectedPaletteId}")
    println("DEBUG_ABOUT: availableColors ids = ${state.availableColors.map { it.id }}")

    val backgroundColor = Color(0xFFFFFFFF)
    val borderColor = Color(0xFFD7D6D6)
    val selectedBorderColor = Color(0xFFACBE8D)

    LaunchedEffect(Unit) {
        roomsViewModel.onRoomEvent(RoomEvent.OnResetLoading)
    }
    LaunchedEffect(state.isGenerating, state.isFetchingImages) {
        if (!isClosing && !state.isGenerating && state.isFetchingImages) {
            onResult(false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            TopBar(roomsViewModel = roomsViewModel,onCloseClick)

            Spacer(modifier = Modifier.height(10.dp))

            ImagePreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                imageBytes = state.selectedImageBytes,
                imageUrl = imageUrl,
                compressedImageUrl = compressedImageUrl
            )

            Spacer(modifier = Modifier.height(20.dp))

            SelectionCard(
                label = "Type",
                value = selectedType,
                borderColor = selectedBorderColor,
                onEditClick = onEditType,
                isEditable = isEditable,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            SelectionCard(
                label = "Style",
                value = selectedStyle,
                borderColor = borderColor,
                onEditClick = onEditStyle,
                isEditable = isEditable,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            paletteToDisplay?.let { palette ->
                println("pallatecolors = ${palette.id}")
                ColorPaletteCard(
                    borderColor = Color(0xFFCBE0A7),
                    paletteColors = palette.colors,
                    onEditClick = onEditPalette,
                    isEditable = isEditable,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            GenerateButton(
                modifier = Modifier.wrapContentSize().align(Alignment.CenterHorizontally),
                isFromExplore = isFromExplore,
                text = buttonText
            ) {
                println("DEBUG_GENERATE: Button Clicked! Current Text: $buttonText")

                // Senior Approach: Button ke text se decision lein
                if (buttonText == "Upload my Room") {
                    println("DEBUG_GENERATE: Triggering Gallery Picker...")
                    onResult(true) // Explore mode mein pehle gallery khulegi
                } else {
                    // Yahan Generation ka logic chalayein
                    val effectiveCredits = if (authState.email.isNullOrBlank()) {
                        authViewModel.guestSession.value?.totalCredits ?: 0
                    } else {
                        authState.totalCredits
                    }

                    if (effectiveCredits > 0) {
                        coroutineScope.launch {
                            val bytes = state.selectedImageBytes
                            val fileName = state.selectedFileName ?: "room_image.jpg"

                            if (bytes != null) {
                                roomsViewModel.onRoomEvent(
                                    RoomEvent.OnGenerateClick(
                                        imageBytes = bytes,
                                        fileName = fileName
                                    )
                                )
                            } else {
                                println("DEBUG_GENERATE: Error - No image bytes found for generation")
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "You've run out of credits to generate designs.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .padding(horizontal = 24.dp)
        ) { data ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C).copy(alpha = 0.98f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier.fillMaxWidth()
                    .border(0.5.dp, Color.White.copy(0.2f), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Icon Badge
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                            .background(Color(0xFFFF5252).copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "!",
                            color = Color(0xFFFF5252),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Insufficient Credits",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = data.visuals.message,
                            color = Color.White.copy(0.7f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.sp
                        )
                    }

                    // Action Label
                    Text(
                        text = "GET MORE",
                        color = Color(0xFFCEFFB3),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                onSubscriptionClick()
                                data.dismiss()
                            }
                            .padding(8.dp)
                    )
                }
            }
        }

        if (state.isGenerating) {
            LoadingScreen()
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (state.isGenerating) {
        LoadingScreen()
    }
}

@Composable
fun TopBar(roomsViewModel: RoomsViewModel,onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        CloseIconButton(tint = Color(0xFFB2B0B0)) {
            roomsViewModel.onRoomEvent(RoomEvent.OnResetState)
            onCloseClick()
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Create",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C2C2C)
        )
    }
}

@Composable
private fun ImagePreview(
    modifier: Modifier = Modifier,
    imageBytes: ByteArray?,
    imageUrl: String?,
    compressedImageUrl: String?
) {
    Box(
        modifier = modifier
            .fillMaxHeight(0.45f)
            .clip(RoundedCornerShape(9.dp))
            .background(Color(0xFFF5F5F5)) // Agar image load hone mein time le, to ye gray dikhega
    ) {
        if (imageBytes != null) {
            AsyncImage(model = imageBytes, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else if (!imageUrl.isNullOrEmpty()) {

            // 1. Compressed Image (Background) - Always Visible
            AsyncImage(
                model = compressedImageUrl ?: imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onSuccess = {
                    println("DEBUG_IMAGE: ✅ compressed Image : $compressedImageUrl")
                }
            )

            // 2. Full Image (Foreground) - Fade In
            // Hum yahan key(imageUrl) use kar rahe hain taake image change hone par reset ho jaye
            key(imageUrl) {
                var isFullLoaded by remember { mutableStateOf(false) }
                val alpha by animateFloatAsState(
                    targetValue = if (isFullLoaded) 1f else 0f,
                    animationSpec = tween(500)
                )

                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha),
                    contentScale = ContentScale.Crop,
                    onSuccess = {
                        isFullLoaded = true
                        println("DEBUG_IMAGE: ✅ Full Image Faded In: $imageUrl")
                    }
                )
            }
        } else {
            Text(text = "No Image Selected", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun SelectionCard(
    label: String,
    value: String,
    borderColor: Color,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    isEditable: Boolean = true

) {
    val backgroundColor = Color(0xFFFFFFFF).copy(alpha = 0.57f)
    val mediumText = Color(0xFF4D4D4D)
    val grayText = Color(0xFF91918F)
    val editIconColor = Color(0xFFB3B5B1)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(9.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = mediumText,
                lineHeight = 20.sp
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = grayText,
                lineHeight = 20.sp
            )
        }
        if (isEditable) {

            Icon(
                painter = painterResource(Res.drawable.edit_icon),
                contentDescription = "Edit",
                tint = editIconColor,
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.CenterEnd)
                    .clickable { onEditClick() }
            )
        }
    }
}

@Composable
private fun ColorPaletteCard(
    borderColor: Color,
    paletteColors: List<Color>,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {},
    isEditable: Boolean = true
) {
    val backgroundColor = Color(0xFFFFFFFF).copy(alpha = 0.57f)
    val lightGrayText = Color(0xFF90918F)
    val editIconColor = Color(0xFFB3B5B1)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(9.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalArrangement = Arrangement.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy((-9).dp)) {
                paletteColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .shadow(
                                5.dp,
                                CircleShape,
                                ambientColor = Color.Black.copy(0.2f),
                                spotColor = Color.Black.copy(0.2f)
                            )
                            .clip(CircleShape)
                            .background(color)
                            .border(0.2.dp, Color(0xFF898989), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Colour Pallete",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = lightGrayText
            )
        }
        if (isEditable) {
            Icon(
                painter = painterResource(Res.drawable.edit_icon),
                contentDescription = "Edit",
                tint = editIconColor,
                modifier = Modifier.size(22.dp).align(Alignment.CenterEnd)
                    .clickable { onEditClick() }
            )
        }
    }
}

@Composable
private fun GenerateButton(modifier: Modifier = Modifier,
                           isFromExplore: Boolean,
                           text: String, // Naya parameter add karein
                           onGenerateClick: () -> Unit) {
    val buttonGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0288f to Color(0xFF34D399).copy(alpha = 0.7f),
            0.5144f to Color(0xFF25C768).copy(alpha = 0.7f),
            1.0f    to Color(0xFF16A34A).copy(alpha = 0.9f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    val borderGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0773f to Color(0xFF34D399),
            0.9281f to Color(0xFF16A34A)
        ),
        start = Offset(Float.POSITIVE_INFINITY, 0f), // 267.55deg ≈ right→left
        end = Offset(0f, 0f)
    )

    Button(
        onClick = onGenerateClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        border = BorderStroke(1.5.dp, borderGradient)
    ) {
        Box(
            modifier = Modifier.wrapContentSize().background(buttonGradient, RoundedCornerShape(50)).padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 9.dp)
                        .size(40.2.dp)
                        .shadow(
                            4.dp,
                            CircleShape,
                            ambientColor = Color.White,
                            spotColor = Color.White
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFC5EBB2).copy(0.69f),
                                    Color(0xFFDFF2C2).copy(0.69f),
                                    Color(0xFFD2F7BD).copy(0.69f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            if (text == "Upload my Room") Res.drawable.add_icon else Res.drawable.generate
                        ),
                        contentDescription = "Generate Icon",

                        tint = if (text == "Upload my Room"){
                            Color(0xFFFFFFFF)

                        }else
                        {
                            Color(0xFFFFD13A)

                        },
                        modifier = Modifier.size(if (text == "Upload my Room") 16.dp else 24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = text,
                    fontSize = 19.67.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}