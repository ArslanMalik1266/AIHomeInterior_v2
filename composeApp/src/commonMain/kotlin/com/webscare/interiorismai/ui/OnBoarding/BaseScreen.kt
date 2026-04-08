package com.webscare.interiorismai.ui.OnBoarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.page_
import homeinterior.composeapp.generated.resources.page_2
import homeinterior.composeapp.generated.resources.page_3
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.webscare.interiorismai.domain.model.BoardingData
import com.webscare.interiorismai.ui.UiUtils.ProgressIndicator
import com.webscare.interiorismai.ui.UiUtils.TopUShape
import com.webscare.interiorismai.ui.theme.black_color
import com.webscare.interiorismai.ui.theme.green_btn
import com.webscare.interiorismai.ui.theme.grey_color
import com.webscare.interiorismai.ui.theme.light_green
import com.webscare.interiorismai.ui.theme.urbanistFontFamily

@Preview(showBackground = true)
@Composable
fun BaseScreen(onEndReached: () -> Unit) {
    val scope = rememberCoroutineScope()
    val myList = remember {
        listOf(
            BoardingData(
                imageUri = Res.drawable.page_,
                title = "Find Inspiration for Every Space",
                subTitle = "Get inspired by modern spaces, timeless aesthetics, and intelligent design trends crafted to elevate your living experience."
            ),
            BoardingData(
                imageUri = Res.drawable.page_2,
                title = "Design. Transform. Compare. Effortlessly.",
                subTitle = "Explore your space’s full potential with intelligent design previews that bring every detail to life."
            ),
            BoardingData(
                imageUri = Res.drawable.page_3,
                title = "Transform Your Space with AI",
                subTitle = "Explore sleek, minimalist interiors, sophisticated color palettes, and functional designs tailored to your unique style."
            )
        )
    }
    val state = rememberPagerState(pageCount = { myList.size })
    Box(
        Modifier.fillMaxSize().background(color = light_green)
    ) {
        HorizontalPager(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.TopCenter)
        ) { page ->

            BoardingPage(
                image = myList[page].imageUri,
            )
        }
        Box(
            modifier = Modifier.fillMaxHeight(0.5f).align(Alignment.BottomCenter)
                .clip(TopUShape())
                .background(Color.White),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(start = 30.dp, end = 30.dp, top = 30.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                AnimatedContent(
                    targetState = state.currentPage,
                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(
                                500,
                                easing = FastOutSlowInEasing
                            )
                        ).togetherWith(
                            fadeOut(animationSpec = tween(500, easing = FastOutSlowInEasing))
                        )
                    }
                ) { page ->
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = myList[page].title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 25.sp,
                            fontFamily = urbanistFontFamily(),
                            textAlign = TextAlign.Center,
                            color = black_color,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = myList[page].subTitle,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontFamily = urbanistFontFamily(),
                            fontWeight = FontWeight.Normal,
                            color = grey_color
                        )
                    }
                }

                ProgressIndicator(
                    currentStep = state.currentPage,
                    totalSteps = myList.size,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        onClick = {
                            onEndReached()
                        },
                        color = Color.Transparent,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, color = grey_color),
                    ) {
                        Text(
                            text = "Skip",
                            color = grey_color,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            if (state.currentPage + 1 < myList.size) {
                                scope.launch {
                                    state.animateScrollToPage(
                                        state.currentPage + 1,
                                        animationSpec = tween(
                                            durationMillis = 500,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                }
                            } else {
                                onEndReached()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = green_btn),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
