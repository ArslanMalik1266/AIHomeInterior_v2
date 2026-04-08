package com.webscare.interiorismai.ui.Account

    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.pager.HorizontalPager
    import androidx.compose.foundation.pager.rememberPagerState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.DisposableEffect
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.alpha
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.geometry.Offset
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.ColorFilter
    import androidx.compose.ui.graphics.graphicsLayer
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.text.SpanStyle
    import androidx.compose.ui.text.buildAnnotatedString
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.text.withStyle
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.compose.ui.util.lerp
    import homeinterior.composeapp.generated.resources.Res
    import homeinterior.composeapp.generated.resources.close
    import homeinterior.composeapp.generated.resources.subscardback
    import homeinterior.composeapp.generated.resources.subscriptionbackgroun
    import org.jetbrains.compose.resources.painterResource
    import com.webscare.interiorismai.ui.CreateAndExplore.RoomEvent
    import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
    import com.webscare.interiorismai.ui.UiUtils.ProgressIndicator
    import com.webscare.interiorismai.utils.SetStatusBarIcons
    import com.webscare.interiorismai.utils.toggleStatusBarIcons
    import homeinterior.composeapp.generated.resources.ic_subscribed_bg
    import kotlin.math.absoluteValue

    data class SubscriptionPlan(
        val name: String,
        val price: String,
        val credits: String,
        val features: List<String>,
        val cardColor: Color
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SubscriptionScreen(onBackClick: () -> Unit,
                           roomsViewModel: RoomsViewModel,
                           onLoginClick: () -> Unit = {},
                           onAddPhotoClick: () -> Unit = {}
    ) {

        SetStatusBarIcons(isLight = true)

        DisposableEffect(Unit) {
            onDispose {
                toggleStatusBarIcons(isLight = false)
            }
        }

        val state by roomsViewModel.state.collectAsState()


        val subscriptionPlans = listOf(
            SubscriptionPlan(
                name = "Basic",
                price = state.billingProducts.find { it.productId == "credits_basic" }?.price ?: "",
                credits = "200 credits",
                features = listOf(
                    "Full crack generation with advanced AI enhancement.",
                    "Professional made for expert-level design precision.",
                    "Watermark remover- clean visuals with no marks.",
                    "Image upscaling enhanced clarity with AI precision"
                ),
                cardColor = Color(0xFFE0E0E0)
            ),
            SubscriptionPlan(
                name = "Standard",
                price = state.billingProducts.find { it.productId == "credits_standard" }?.price ?: "",
                credits = "500 credits",
                features = listOf(
                    "Unlimited parallel generation with advanced AI.",
                    "Professional mode for expert-level design precision.",
                    "Watermark remover- clean visuals with no marks.",
                    "Image upscaling enhanced clarity with AI precision"
                ),
                cardColor = Color(0xFFD4D4D4)
            ),
            SubscriptionPlan(
                name = "Pro",
                price = state.billingProducts.find { it.productId == "credits_pro" }?.price ?: "",
                credits = "900 credits",
                features = listOf(
                    "Priority access to all new AI features and tools.",
                    "Custom model generation for tailored results.",
                    "Unlimited high-speed parallel image generation.",
                    "Advanced upscaling and watermark-free visuals."
                ),
                cardColor = Color(0xFFC8C8C8)
            )
        )

        val pagerState = rememberPagerState( initialPage = 1,pageCount = { subscriptionPlans.size })
        val pagerOffset = pagerState.currentPageOffsetFraction
        var showSuccessSheet by remember { mutableStateOf(false) }


        LaunchedEffect(state.purchaseSuccess) {
            state.purchaseSuccess?.let {
                roomsViewModel.onSubscriptionEvent(RoomEvent.ClearPurchaseState)
                showSuccessSheet = true
            }
        }
        LaunchedEffect(state.navigateToLogin) {
            if (state.navigateToLogin) {
                roomsViewModel.onSubscriptionEvent(RoomEvent.ClearNavigateToLogin)

                onLoginClick()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)

        ) {
            Image(
                painter = painterResource(Res.drawable.subscriptionbackgroun),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x00000000),
                                Color(0xFF141414)
                            ),
                            startY = 0f,
                            endY = 1000f
                        )
                    )
            )

            Box(modifier = Modifier.align(Alignment.TopEnd).padding(end = 10.dp).windowInsetsPadding(WindowInsets.statusBars)) {
                Box(
                    modifier = Modifier.size(30.dp)

                        .background(Color.White.copy(alpha = 0.3f), CircleShape).clip(CircleShape)
                        .clickable {
                            onBackClick()
                        }, contentAlignment = Alignment.Center

                ) {
                    Image(
                        painter = painterResource(Res.drawable.close),
                        colorFilter = ColorFilter.tint(color = Color.White),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        ) {
                            append("Unlock\n")
                        }

                        withStyle(
                            style = SpanStyle(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9ECE49)
                            )
                        ) {
                            append("Pro-level\n")
                        }

                        withStyle(
                            style = SpanStyle(
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        ) {
                            append("benefits.")
                        }
                    },
                    textAlign = TextAlign.Center,
                    lineHeight = 43.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 35.dp),
                    pageSpacing = (-10).dp
                ) { page ->
                    SubscriptionCard(
                        plan = subscriptionPlans[page],
                        modifier = Modifier.fillMaxWidth().graphicsLayer {
                            val pageOffset =
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                            // Scale effect
                            scaleX = lerp(0.85f, 1f, 1f - pageOffset.absoluteValue.coerceIn(0f, 1f))
                            scaleY = lerp(0.85f, 1f, 1f - pageOffset.absoluteValue.coerceIn(0f, 1f))

                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                ProgressIndicator(
                    currentStep = pagerState.currentPage,
                    selectedColor = Color.White.copy(alpha = 0.66f),
                    totalSteps = subscriptionPlans.size,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        println("🔴 BUY NOW CLICKED!")
                        val productId = when (pagerState.currentPage) {
                            0 -> "credits_basic"
                            1 -> "credits_standard"
                            2 -> "credits_pro"
                            else -> ""
                        }
                        println("🔴 productId=$productId")
                        roomsViewModel.onSubscriptionEvent(RoomEvent.OnPurchasePlan(productId))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(51.dp).padding(horizontal = 24.dp),
                    enabled = !state.isPurchasing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFAADA56),
                                        Color(0xFF799F35),
                                    ),
                                    start = Offset(0f, 0f),        // Top
                                    end = Offset(0f, Float.POSITIVE_INFINITY)  // Bottom
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Buy Now",
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "By subscribing, you agree to our Terms of Service and Privacy Policy.Enjoy full access to all Pro features!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFC1C1C1),
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        if (showSuccessSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showSuccessSheet = false
                    onBackClick()
                },
                containerColor = Color.Transparent,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = null,
                contentWindowInsets = { WindowInsets(0) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.White)


                )
                {
                    // BACKGROUND IMAGE - poore box ke peeche
                    Image(
                        painter = painterResource(Res.drawable.ic_subscribed_bg),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize().alpha(0.3f),
                        contentScale = ContentScale.FillBounds
                    )

                    // CONTENT - image ke upar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 24.dp)
                            .padding(top = 32.dp, bottom = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Credits Added Successfully!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your credits are ready. Start transforming your space with AI-generated designs.",
                            fontSize = 12.sp,
                            color = Color(0xFF9B9B9B),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                showSuccessSheet = false
                                onAddPhotoClick()
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFFAADA56), Color(0xFF799F35))
                                        ),
                                        RoundedCornerShape(50)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Start Generating",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }    }

    @Composable
    fun SubscriptionCard(
        plan: SubscriptionPlan,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier.fillMaxHeight(0.5f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painterResource(Res.drawable.subscardback),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plan.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF696969),
                        modifier = Modifier.padding(start = 18.dp)
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFEE76),
                                        Color(0xFFEFD836)
                                    )
                                ),
                                shape = RoundedCornerShape(bottomStart = 20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = plan.credits,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF585858)
                            )
//                            Text(
//                                text = " /month",
//                                fontSize = 9.sp,
//                                fontWeight = FontWeight.Bold,
//                                color = Color(0xFF585858)
//                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = plan.price,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA3B18A),
                            lineHeight = 22.sp
                        )
//                        Text(
//                            text = "/month",
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Normal,
//                            color = Color(0xFFA3B18A),
//                            modifier = Modifier.padding(bottom = 4.dp)
//                        )
                    }

                    Spacer(modifier = Modifier.height(17.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFCFCFCF))
                    )

                    Spacer(modifier = Modifier.height(17.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        plan.features.forEach { feature ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFFA3B18A),
                                                    Color(0xFF799F35)
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = feature,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF838383),
                                    lineHeight = 15.sp,
                                    modifier = Modifier.weight(1f),
                                    letterSpacing = 0.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
