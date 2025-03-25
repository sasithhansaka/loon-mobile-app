package com.cns.loon.ui.signUpScreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.cns.loon.ui.loginScreen.LoginScreen
import com.cns.loon.ui.theme.Aeonik
import com.cns.loon.ui.theme.DarkGreenColor
import com.cns.loon.ui.theme.GothamBlack
import com.cns.loon.ui.theme.GreenColor
import com.cns.loon.ui.theme.LightGrayGreenColor

class UserSignUpScreen : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            var isLoading by remember { mutableStateOf(false) }

            UserSignUpUI(
                isLoading = isLoading,
                onSignUp = { firstName, lastName, city, district, email, password, onError ->
                    isLoading = true
                    signUpUser(
                        firstName, lastName, city, district, email, password,
                        onSignUpSuccess = {
                            isLoading = false
                            startActivity(Intent(this, LoginScreen::class.java))
                            finish()
                        },
                        onError = { error ->
                            isLoading = false
                            onError(error)
                        }
                    )
                },
                onBackClick = {
                    finish()
                }
            )
        }
    }

    private fun signUpUser(
        firstName: String,
        lastName: String,
        city: String,
        district: String,
        email: String,
        password: String,
        onSignUpSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = task.result?.user
                    val uid = user?.uid

                    val userDetails = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "city" to city,
                        "district" to district,
                        "email" to email
                    )

                    uid?.let {
                        // Store user data in Firestore
                        firestore.collection("users").document(it)
                            .set(userDetails)
                            .addOnSuccessListener {
                                auth.signOut()
                                Toast.makeText(baseContext, "Sign-up successful. Please log in.", Toast.LENGTH_SHORT).show()
                                onSignUpSuccess()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error saving user details", e)
                                onError("Error saving user details: ${e.message}")
                            }
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    onError("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    companion object {
        private const val TAG = "UserSignUpScreen"
    }
}

@Composable
fun UserSignUpUI(
    isLoading: Boolean,
    onSignUp: (
        firstName: String,
        lastName: String,
        city: String,
        district: String,
        email: String,
        password: String,
        onError: (String) -> Unit
    ) -> Unit,
    onBackClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_3),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )
        
        if (isLoading) {
            CircularProgressIndicator(color = GreenColor)
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                // Back button at the top left
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = onBackClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = DarkGreenColor
                        ),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "← Back",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = Aeonik,
                            ),
                            color = DarkGreenColor
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.logo_text_2),
                    contentDescription = "Loon Logo",
                    modifier = Modifier
                        .height(100.dp)
                        .width(200.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.FillWidth
                )
                
                Text(
                    text = "Create User Account",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontFamily = Aeonik,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkGreenColor
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = LightGrayGreenColor,
                            focusedLabelColor = DarkGreenColor
                        )
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = LightGrayGreenColor,
                            focusedLabelColor = DarkGreenColor
                        )
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = LightGrayGreenColor,
                            focusedLabelColor = DarkGreenColor
                        )
                    )
                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("District") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = LightGrayGreenColor,
                            focusedLabelColor = DarkGreenColor
                        )
                    )
                }
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    shape = RoundedCornerShape(16.dp),
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
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(17.dp),
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = LightGrayGreenColor,
                        focusedLabelColor = DarkGreenColor
                    )
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                Button(
                    onClick = {
                        if (firstName.isEmpty() || lastName.isEmpty() || city.isEmpty() || district.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                            errorMessage = "Please fill out all fields."
                        } else if (password != confirmPassword) {
                            errorMessage = "Passwords do not match."
                        } else {
                            errorMessage = null
                            onSignUp(
                                firstName,
                                lastName,
                                city,
                                district,
                                email,
                                password
                            ) { errorMessage = it }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenColor
                    )
                ) {
                    Text(
                        text = "SIGN UP",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = GothamBlack,
                            fontWeight = FontWeight.Black,
                        ),
                        color = DarkGreenColor
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 412,
    heightDp = 846,
    device = "spec:width=720dp,height=1480dp,dpi=280"
)
@Composable
fun UserSignUpScreenPreview() {
    UserSignUpUI(
        isLoading = false,
        onSignUp = { _, _, _, _, _, _, _ -> },
        onBackClick = { }
    )
}