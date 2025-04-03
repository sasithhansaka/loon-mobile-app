package com.cns.loon.ui.salonDashboardScreen

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cns.loon.R
import com.cns.loon.ui.loginScreen.LoginScreen
import com.cns.loon.ui.theme.Aeonik
import com.cns.loon.ui.theme.DarkGreenColor
import com.cns.loon.ui.theme.GothamBlack
import com.cns.loon.ui.theme.GreenColor
import com.cns.loon.ui.theme.LightGrayGreenColor
import com.cns.loon.ui.theme.LightGreenHighOpacity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class SalonDashboardProfileScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SalonProfileScreen()
        }
    }
}

@Composable
fun SalonProfileScreen() {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val salonID = auth.currentUser?.uid ?: "unknown"
    val coroutineScope = rememberCoroutineScope()

    var category by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var profileImageBase64 by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isUpdated by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            try {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                val bitmap = ImageDecoder.decodeBitmap(source)

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val byteArray = outputStream.toByteArray()
                profileImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
                isUpdated = true
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to process image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Convert base64 string to bitmap
    val profileBitmap = remember(profileImageBase64) {
        if (profileImageBase64.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(profileImageBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    // Fetch Data from Firestore
    LaunchedEffect(Unit) {
        try {
            val document = firestore.collection("services").document(salonID).get().await()
            if (document.exists()) {
                val data = document.data ?: emptyMap()
                category = data["category"] as? String ?: "N/A"
                name = data["name"] as? String ?: ""
                price = (data["price"] as? Number)?.toString() ?: ""
                email = data["email"] as? String ?: ""
                profileImageBase64 = data["profile_image"] as? String ?: ""
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    // Delete Account Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your salon account? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                // Delete from Firestore
                                firestore.collection("services").document(salonID).delete().await()

                                // Delete user account from Firebase Auth
                                auth.currentUser?.delete()?.await()

                                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_LONG).show()

                                // Navigate to login
                                val intent = Intent(context, LoginScreen::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to delete account: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                            showDeleteConfirmDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = GreenColor
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_text_2),
                    contentDescription = "Loon Logo",
                    modifier = Modifier
                        .height(60.dp)
                        .width(120.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.FillWidth
                )

                // Title
                Text(
                    text = "Salon Profile",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = GothamBlack,
                        fontWeight = FontWeight.Black,
                    ),
                    color = DarkGreenColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Manage your salon details",
                    fontSize = 16.sp,
                    fontFamily = Aeonik,
                    color = GreenColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(LightGrayGreenColor.copy(alpha = 0.3f))
                        .border(2.dp, GreenColor, CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileBitmap != null) {
                        Image(
                            bitmap = profileBitmap,
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (name.isNotEmpty()) {
                        Text(
                            text = name.first().toString(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreenColor
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
                    text = "Tap to change profile image",
                    fontSize = 14.sp,
                    fontFamily = Aeonik,
                    color = DarkGreenColor,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                // Salon Details Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Category (Non-Editable)
                        Column {
                            Text(
                                text = "SERVICE CATEGORY",
                                fontSize = 12.sp,
                                fontFamily = Aeonik,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = LightGrayGreenColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 16.sp,
                                    fontFamily = Aeonik,
                                    fontWeight = FontWeight.Medium,
                                    color = DarkGreenColor,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        // Editable Fields
                        EditableField(
                            label = "SALON NAME",
                            value = name,
                            onValueChange = {
                                name = it
                                isUpdated = true
                            }
                        )

                        EditableField(
                            label = "SERVICE PRICE ($)",
                            value = price,
                            onValueChange = {
                                price = it
                                isUpdated = true
                            }
                        )

                        // Category (Non-Editable)
                        Column {
                            Text(
                                text = "EMAIL ADDRESS",
                                fontSize = 12.sp,
                                fontFamily = Aeonik,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = LightGrayGreenColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = email,
                                    fontSize = 16.sp,
                                    fontFamily = Aeonik,
                                    fontWeight = FontWeight.Medium,
                                    color = DarkGreenColor,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // "Update" button when fields are changed
                if (isUpdated) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    // Validate price is numeric
                                    val priceValue = price.toDoubleOrNull()
                                    if (priceValue == null) {
                                        Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }

                                    // Update Firestore document
                                    val updates = mutableMapOf<String, Any>(
                                        "name" to name,
                                        "price" to priceValue,
                                        "email" to email
                                    )

                                    // Add profile image if updated
                                    if (profileImageBase64.isNotEmpty()) {
                                        updates["profile_image"] = profileImageBase64
                                    }

                                    // Update Firestore
                                    firestore.collection("services").document(salonID).update(updates).await()

                                    // Update Authentication Email if changed
                                    val currentUser = auth.currentUser
                                    if (email != currentUser?.email) {
                                        currentUser?.updateEmail(email)?.await()
                                    }

                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                    isUpdated = false
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightGreenHighOpacity
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "UPDATE PROFILE",
                            fontSize = 16.sp,
                            fontFamily = GothamBlack,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Delete Account Button
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color.Red)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Account",
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "DELETE ACCOUNT",
                        fontSize = 16.sp,
                        fontFamily = GothamBlack,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember(value) { mutableStateOf(value) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = Aeonik,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (isEditing) {
            OutlinedTextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                    onValueChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = GreenColor,
                    unfocusedBorderColor = LightGrayGreenColor
                ),
                trailingIcon = {
                    IconButton(onClick = { isEditing = false }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Done Editing",
                            tint = GreenColor
                        )
                    }
                }
            )
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isEditing = true },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = textValue,
                        fontSize = 16.sp,
                        fontFamily = Aeonik,
                        color = Color.Black
                    )

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = GreenColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Helper function to convert Bitmap to Base64 string
fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

@Preview(
    showBackground = true,
    widthDp = 412,
    heightDp = 846,
    device = "spec:width=720dp,height=1480dp,dpi=280"
)
@Composable
fun SalonProfileScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Salon Profile",
            style = TextStyle(
                fontSize = 28.sp,
                fontFamily = GothamBlack,
                fontWeight = FontWeight.Black,
            ),
            color = DarkGreenColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Manage your salon details",
            fontSize = 16.sp,
            fontFamily = Aeonik,
            color = GreenColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Image
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(LightGrayGreenColor.copy(alpha = 0.3f))
                .border(2.dp, GreenColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "E",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreenColor
            )
        }

        Text(
            text = "Tap to change profile image",
            fontSize = 14.sp,
            fontFamily = Aeonik,
            color = DarkGreenColor,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        // Salon Details Card
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category
                Column {
                    Text(
                        text = "SERVICE CATEGORY",
                        fontSize = 12.sp,
                        fontFamily = Aeonik,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = LightGrayGreenColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Hair Cutting",
                            fontSize = 16.sp,
                            fontFamily = Aeonik,
                            fontWeight = FontWeight.Medium,
                            color = DarkGreenColor,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Editable Fields (Preview)
                EditableField(
                    label = "SALON NAME",
                    value = "Elegant Salon",
                    onValueChange = { }
                )

                EditableField(
                    label = "SERVICE PRICE ($)",
                    value = "45.99",
                    onValueChange = { }
                )

                EditableField(
                    label = "EMAIL ADDRESS",
                    value = "elegant.salon@example.com",
                    onValueChange = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Update button
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "UPDATE PROFILE",
                fontSize = 16.sp,
                fontFamily = GothamBlack,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delete Account Button
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Red
            ),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color.Red)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Account",
                tint = Color.Red
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "DELETE ACCOUNT",
                fontSize = 16.sp,
                fontFamily = GothamBlack,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        }
    }
}