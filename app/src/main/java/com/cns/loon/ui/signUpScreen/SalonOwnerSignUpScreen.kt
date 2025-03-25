package com.cns.loon.ui.signUpScreen

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import java.io.ByteArrayOutputStream


class SalonOwnerSignUpScreen : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            var isLoading by remember { mutableStateOf(false) }

            SalonOwnerSignUpUI(
                isLoading = isLoading,
                onSignUp = { name, category, price, profileImageBase64, email, password, onError ->
                    isLoading = true
                    signUpSalonOwner(
                        name, category, price, profileImageBase64, email, password,
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

    private fun signUpSalonOwner(
        name: String,
        category: String,
        price: Double,
        profileImageBase64: String?,
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

                    val salonDetails = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "category" to category,
                        "price" to price,
                        "profile_image" to (profileImageBase64 ?: "")
                    )

                    uid?.let {
                        // Store salon owner data in Firestore
                        firestore.collection("services").document(it)
                            .set(salonDetails)
                            .addOnSuccessListener {
                                auth.signOut()
                                Toast.makeText(baseContext, "Sign-up successful. Please log in.", Toast.LENGTH_SHORT).show()
                                onSignUpSuccess()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error saving salon details", e)
                                onError("Error saving salon details: ${e.message}")
                            }
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    onError("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    companion object {
        private const val TAG = "SalonOwnerSignUpScreen"
    }
}

@Composable
fun SalonOwnerSignUpUI(
    isLoading: Boolean,
    onSignUp: (
        name: String,
        category: String,
        price: Double,
        profileImageBase64: String?,
        email: String,
        password: String,
        onError: (String) -> Unit
    ) -> Unit,
    onBackClick: () -> Unit
) {
    var salonName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageBase64 by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            val bitmap = ImageDecoder.decodeBitmap(source)

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            profileImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    // Main container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Background image at the bottom
        Image(
            painter = painterResource(id = R.drawable.background_2),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )

        if (isLoading) {
            CircularProgressIndicator(color = GreenColor)
        } else {
            // Scrollable content container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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

                // Main scrollable content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logo_text_2),
                        contentDescription = "Loon Logo",
                        modifier = Modifier
                            .height(80.dp)
                            .width(160.dp)
                            .padding(vertical = 8.dp),
                        contentScale = ContentScale.FillWidth
                    )

                    // Title
                    Text(
                        text = "Create Salon Account",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = Aeonik,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkGreenColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Profile Image Selector
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(alpha = 0.3f))
                                .border(2.dp, GreenColor, CircleShape)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                val source = ImageDecoder.createSource(
                                    context.contentResolver,
                                    selectedImageUri!!
                                )
                                val bitmap = ImageDecoder.decodeBitmap(source)

                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Add Photo",
                                    tint = DarkGreenColor,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Text(
                            text = "Upload Salon Profile Image",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = Aeonik
                            ),
                            color = DarkGreenColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Form Fields
                    OutlinedTextField(
                        value = salonName,
                        onValueChange = { salonName = it },
                        label = { Text("Salon Name") },
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
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Service Category") },
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
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Service Price") },
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

                    // Error message
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sign Up Button
                    Button(
                        onClick = {
                            if (salonName.isEmpty() || category.isEmpty() || priceText.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                                errorMessage = "Please fill out all fields."
                            } else if (password != confirmPassword) {
                                errorMessage = "Passwords do not match."
                            } else {
                                val price = try {
                                    priceText.toDouble()
                                } catch (_: NumberFormatException) {
                                    errorMessage = "Please enter a valid price."
                                    return@Button
                                }
                                errorMessage = null
                                onSignUp(
                                    salonName,
                                    category,
                                    price,
                                    profileImageBase64,
                                    email,
                                    password
                                ) { errorMessage = it }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenColor
                        )
                    ) {
                        Text(
                            text = "SIGN UP",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = GothamBlack,
                                fontWeight = FontWeight.Black,
                            ),
                            color = DarkGreenColor
                        )
                    }

                    // Bottom spacing to ensure everything is visible when scrolling
                    Spacer(modifier = Modifier.height(24.dp))
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
fun SalonOwnerSignUpScreenPreview() {
    SalonOwnerSignUpUI(
        isLoading = false,
        onSignUp = { _, _, _, _, _, _, _ -> },
        onBackClick = { }
    )
}