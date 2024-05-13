package com.codescape.safeargs.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.codescape.safeargs.R
import kotlinx.serialization.Serializable

/*
* Images:
* Staircase Spiral Architecture by stokpic: https://pixabay.com/photos/staircase-spiral-architecture-600468/
* Architecture Corridor Modern by PIRO4D: https://pixabay.com/photos/architecture-corridor-modern-3357028/
* */

data class Photo(
    val photoRes: Int,
    val photoName: String,
    val photoDesc: String
)

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data class Detail(val photoRes: Int, val photoName: String, val photoDesc: String) : Screen()
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = Screen.Home,
            contentAlignment = Alignment.Center,
            sizeTransform = {
                SizeTransform { initialSize, targetSize ->
                    keyframes {
                        durationMillis = 200
                    }
                }
            }
        ) {
            composable<Screen.Home> { backStackEntry ->
                HomeScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable,
                    onNavigateToProfile = { photo ->
                        navController.navigate(
                            Screen.Detail(
                                photoRes = photo.photoRes,
                                photoName = photo.photoName,
                                photoDesc = photo.photoDesc
                            )
                        )
                    }
                )
            }
            composable<Screen.Detail> { backStackEntry ->
                val photo = backStackEntry.toRoute<Screen.Detail>()
                ProfileScreen(
                    photoRes = photo.photoRes,
                    photoName = photo.photoName,
                    photoDesc = photo.photoDesc,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onNavigateToProfile: (Photo) -> Unit
) {
    val photos = remember {
        listOf(
            Photo(
                photoRes = R.drawable.staircase_600468,
                photoName = "Staircase",
                photoDesc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi ut rhoncus nisl, sit amet auctor erat. Nunc ultricies nisl id risus pharetra dignissim. Nullam facilisis nisi in est elementum scelerisque. Praesent sit amet metus quis dui laoreet convallis et a nibh. Pellentesque aliquet eros nisl, a ornare risus vehicula in. Nullam vitae ligula felis. Proin fringilla ipsum non neque pharetra, sit amet malesuada sem maximus. Donec sapien est, faucibus scelerisque efficitur eget, imperdiet convallis lectus. Quisque tellus lectus, semper at interdum sit amet, condimentum ut leo. Nulla dolor mi, vulputate in ultrices ut, cursus vitae lacus. Morbi sem enim, porta id maximus eu, varius ac."
            ),
            Photo(
                photoRes = R.drawable.architecture_3357028,
                photoName = "Architecture",
                photoDesc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi ut rhoncus nisl, sit amet auctor erat. Nunc ultricies nisl id risus pharetra dignissim. Nullam facilisis nisi in est elementum scelerisque. Praesent sit amet metus quis dui laoreet convallis et a nibh. Pellentesque aliquet eros nisl, a ornare risus vehicula in. Nullam vitae ligula felis. Proin fringilla ipsum non neque pharetra, sit amet malesuada sem maximus. Donec sapien est, faucibus scelerisque efficitur eget, imperdiet convallis lectus. Quisque tellus lectus, semper at interdum sit amet, condimentum ut leo. Nulla dolor mi, vulputate in ultrices ut, cursus vitae lacus. Morbi sem enim, porta id maximus eu, varius ac nibh."
            )
        )
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        contentPadding = PaddingValues(24.dp)
    ) {
        items(photos) { photo ->
            val measurer = rememberTextMeasurer()
            val result = remember(photo) {
                measurer.measure(photo.photoName, style = TextStyle.Default.copy(fontSize = 16.sp))
            }
            val height = with(LocalDensity.current) { result.size.height.toDp() }
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onNavigateToProfile(photo) }
            ) {
                Image(
                    modifier = Modifier
                        .sharedTransition(
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope
                        ) { sharedTransitionScope, animatedContentScope ->
                            with(sharedTransitionScope) {
                                sharedElement(
                                    state = sharedTransitionScope.rememberSharedContentState(key = photo.photoRes),
                                    animatedVisibilityScope = animatedContentScope
                                )
                            }
                        }
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .fillMaxWidth(),
                    painter = painterResource(id = photo.photoRes),
                    contentDescription = "image",
                )
                Box(
                    modifier = Modifier
                        .sharedTransition(
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope
                        ) { sharedTransitionScope, animatedContentScope ->
                            with(sharedTransitionScope) {
                                sharedElement(
                                    state = sharedTransitionScope.rememberSharedContentState(key = photo.photoName),
                                    animatedVisibilityScope = animatedContentScope
                                )
                            }
                        }
                        .background(Color.White)
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(height)
                        .drawBehind {
                            drawText(result)
                        }
                )
                Text(
                    modifier = Modifier
                        .sharedTransition(
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope
                        ) { sharedTransitionScope, animatedContentScope ->
                            with(sharedTransitionScope) {
                                sharedElement(
                                    state = sharedTransitionScope.rememberSharedContentState(key = photo.photoDesc),
                                    animatedVisibilityScope = animatedContentScope
                                )
                            }
                        }
                        .height(0.dp),
                    text = photo.photoDesc,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProfileScreen(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    photoRes: Int,
    photoName: String,
    photoDesc: String
) {
    var focused by remember { mutableStateOf(false) }
    val scale = remember { Animatable(0f) }
    LaunchedEffect(focused) {
        if (focused) {
            scale.animateTo(1.5f, tween(150))
        } else {
            scale.animateTo(1f, tween(150))
        }
    }
    val measurer = rememberTextMeasurer()
    val result = remember(photoName) {
        measurer.measure(photoName, style = TextStyle.Default.copy(fontSize = 32.sp))
    }
    Box(contentAlignment = Alignment.Center) {
        Image(
            modifier = Modifier
                .sharedTransition(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                ) { sharedTransitionScope, animatedContentScope ->
                    with(sharedTransitionScope) {
                        sharedElement(
                            state = sharedTransitionScope.rememberSharedContentState(key = photoRes),
                            animatedVisibilityScope = animatedContentScope
                        )
                    }
                }
                .fillMaxHeight()
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .clip(RoundedCornerShape(topEnd = 125.dp)),
            painter = painterResource(id = photoRes),
            contentDescription = "image",
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .sharedTransition(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                ) { sharedTransitionScope, animatedContentScope ->
                    with(sharedTransitionScope) {
                        sharedElement(
                            state = sharedTransitionScope.rememberSharedContentState(key = photoName),
                            animatedVisibilityScope = animatedContentScope
                        )
                    }
                }
                .align(Alignment.TopStart)
                .padding(24.dp)
                .drawBehind {
                    drawText(result, Color.White)
                }

        )
        Text(
            modifier = Modifier
                .sharedTransition(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                ) { sharedTransitionScope, animatedContentScope ->
                    with(sharedTransitionScope) {
                        sharedElement(
                            state = sharedTransitionScope.rememberSharedContentState(key = photoDesc),
                            animatedVisibilityScope = animatedContentScope
                        )
                    }
                }
                .padding(24.dp)
                .align(Alignment.Center)
                .clickable(
                    interactionSource = null,
                    indication = null
                ) { focused = !focused },
            text = photoDesc,
            color = Color.White,
            fontSize = (16f * scale.value).sp
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
inline fun Modifier.sharedTransition(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    sharedTransition: Modifier.(SharedTransitionScope, AnimatedContentScope) -> Modifier,
): Modifier = if (sharedTransitionScope != null && animatedContentScope != null) {
    sharedTransition(sharedTransitionScope, animatedContentScope)
} else this