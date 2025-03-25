package com.cns.loon.ui.signUpScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class SignUpScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirect to the SignUpSelectionScreen
        setContent {
            val signUpSelectionScreen = SignUpSelectionScreen()
            signUpSelectionScreen.onCreate(savedInstanceState)
        }
    }
}