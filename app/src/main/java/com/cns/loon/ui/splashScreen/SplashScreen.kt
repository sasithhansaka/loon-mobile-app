package com.cns.loon.ui.splashScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cns.loon.R
import com.cns.loon.ui.loginScreen.LoginScreen
import kotlinx.coroutines.delay
import com.google.firebase.FirebaseApp

@SuppressLint("CustomSplashScreen")
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        setContent {
            SplashScreenUI {
                startActivity(Intent(this, LoginScreen::class.java))
                finish()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSplashScreenUI() {
    SplashScreenUI(onTimeout = {})
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SplashScreenUI(onTimeout: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    // Start the animation after the composition is created
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        onTimeout()
    }

    // Define the animation
    val yOffset: Dp by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else (-300).dp, // Start above screen (-300.dp) and settle at center (0.dp)
        animationSpec = tween(
            durationMillis = 1000, // Animation duration for the initial drop
            easing = LinearOutSlowInEasing // Smooth drop easing
        ), label = ""
    )

    val bounceAnimation: Float by animateFloatAsState(
        targetValue = if (startAnimation) 1.06f else 1f, // Slight scaling for subtle bounce
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_bg_removed),
            contentDescription = "Salon Logo",
            modifier = Modifier
                .size((200 * bounceAnimation).dp) // Apply subtle bouncing scale
                .offset(y = yOffset) // Animate the vertical offset
        )
    }
}