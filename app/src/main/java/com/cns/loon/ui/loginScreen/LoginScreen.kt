package com.cns.loon.ui.loginScreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.cns.loon.R
import com.cns.loon.ui.homeScreen.HomeScreen
import com.cns.loon.ui.salonDashboardScreen.SalonDashboardScreen
import com.cns.loon.ui.signUpScreen.SignUpSelectionScreen
import com.cns.loon.ui.theme.Aeonik
import com.cns.loon.ui.theme.DarkGreenColor
import com.cns.loon.ui.theme.GothamBlack
import com.cns.loon.ui.theme.GreenColor
import com.cns.loon.ui.theme.LightGrayGreenColor

class LoginScreen : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            LoginUI(
                onLogin = { email, password -> signIn(email, password) },
                onSignUp = {
                    startActivity(Intent(this, SignUpSelectionScreen::class.java))
                }
            )
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkUserTypeAndRedirect(currentUser.uid)
        }
    }

    private fun checkUserTypeAndRedirect(uid: String) {
        // First, check if the user is in the "users" collection
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { userDocument ->
                if (userDocument.exists()) {
                    // User is a regular user, redirect to HomeScreen
                    startActivity(Intent(this, HomeScreen::class.java))
                    finish()
                } else {
                    // If not found in users, check in services collection
                    firestore.collection("services").document(uid).get()
                        .addOnSuccessListener { salonDocument ->
                            if (salonDocument.exists()) {
                                // User is a salon owner, redirect to SalonDashboardScreen
                                startActivity(Intent(this, SalonDashboardScreen::class.java))
                                finish()
                            } else {
                                // User not found in either collection, sign out
                                auth.signOut()
                                Toast.makeText(
                                    baseContext,
                                    "Account not found. Please sign up.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error checking salon account", e)
                            auth.signOut()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error checking user account", e)
                auth.signOut()
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(
                        baseContext,
                        "Authentication successful.",
                        Toast.LENGTH_SHORT,
                    ).show()

                    // Check user type and redirect accordingly
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        checkUserTypeAndRedirect(userId)
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}

@Preview(
    showBackground = true,
    widthDp = 412,
    heightDp = 846,
    device = "spec:width=720dp,height=1480dp,dpi=280"
)
@Composable
fun PreviewLoginUI() {
    LoginUI(onLogin = { _, _ -> }, onSignUp = {})
}

@Composable
fun LoginUI(
    onLogin: (String, String) -> Unit,
    onSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_bg_removed),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 25.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = LightGrayGreenColor,
                    focusedLabelColor = DarkGreenColor
                )

            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = LightGrayGreenColor,
                    focusedLabelColor = DarkGreenColor
                )
            )

            Button(
                onClick = {
                    isLoading = true
                    onLogin(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 8.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenColor
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 16.dp
                ),
                shape = RoundedCornerShape(16.dp)

            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        text ="LOGIN",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = GothamBlack,
                            fontWeight = FontWeight.Black,
                        ),
                        color = DarkGreenColor
                    )
                }
            }

            TextButton(
                onClick = onSignUp,
                enabled = !isLoading,
            ) {
                Text(
                    text = "Don't have an account? Sign up",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = Aeonik,
                        fontWeight = FontWeight.Normal,
                    ),
                    color = DarkGreenColor
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.background_4),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}