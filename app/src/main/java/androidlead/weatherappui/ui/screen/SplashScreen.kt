package androidlead.weatherappui.ui.screen

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidlead.weatherappui.R
import androidlead.weatherappui.ui.theme.ColorGradient1
import androidlead.weatherappui.ui.theme.ColorGradient2
import androidlead.weatherappui.ui.theme.ColorGradient3
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen() {
    val scale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = { OvershootInterpolator(2f).getInterpolation(it) })
            )
        }
        launch {
            delay(200)
            textAlpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 600))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(ColorGradient1, ColorGradient2, ColorGradient3)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Section: Image
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_sub_rain),
                    contentDescription = "Weather Icon",
                    modifier = Modifier
                        .size(280.dp)
                        .scale(scale.value)
                )
            }

            // Bottom Section: White Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(textAlpha.value),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "Weather Forecast",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color(0xFF333333), // Dark text on white
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Stay ahead of the weather in your city and plan your day with confidence.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Loader replaces Arrow Button
                    CustomFrostyLoader(modifier = Modifier.size(50.dp))

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Designed and developed by Muhammad Aliyan",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp)) 
                }
            }
        }
    }
}
