package com.cns.loon.ui.homeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.cns.loon.ui.theme.*
import kotlinx.coroutines.tasks.await
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import android.content.Intent
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextAlign
import com.cns.loon.ui.loginScreen.LoginScreen

data class ProfileBooking(
    val id: String = "",
    val bookingDate: String = "",
    val bookingDateRaw: String = "",
    val price: Double = 0.0,
    val serviceId: String = "",
    val serviceName: String = "",
    val status: String = "",
    val timeSlot: String = "",
    val userId: String = ""
)

data class ProfileSalonService(
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val profileImage: String = ""
)

@Composable
fun ProfileFragment() {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: ""

    var pendingBookings by remember { mutableStateOf<List<ProfileBooking>>(emptyList()) }
    var completedBookings by remember { mutableStateOf<List<ProfileBooking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val salonCache = remember { mutableStateMapOf<String, ProfileSalonService>() }

    // Fetch bookings from Firestore
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            try {
                val bookingsSnapshot = firestore.collection("bookings")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val allBookings = bookingsSnapshot.documents.mapNotNull { doc ->
                    doc.data?.let { data ->
                        ProfileBooking(
                            id = doc.id,
                            bookingDate = data["bookingDate"] as? String ?: "",
                            bookingDateRaw = data["bookingDateRaw"] as? String ?: "",
                            price = (data["price"] as? Number)?.toDouble() ?: 0.0,
                            serviceId = data["serviceId"] as? String ?: "",
                            serviceName = data["serviceName"] as? String ?: "",
                            status = data["status"] as? String ?: "",
                            timeSlot = data["timeSlot"] as? String ?: "",
                            userId = data["userId"] as? String ?: ""
                        )
                    }
                }

                pendingBookings = allBookings.filter { it.status == "pending" }
                completedBookings = allBookings.filter { it.status == "done" }

                // Fetch salon details for each booking
                allBookings.forEach { booking ->
                    coroutineScope.launch {
                        try {
                            val serviceDoc = firestore.collection("services")
                                .document(booking.serviceId)
                                .get()
                                .await()

                            serviceDoc.data?.let { data ->
                                val salon = ProfileSalonService(
                                    name = data["name"] as? String ?: "",
                                    category = data["category"] as? String ?: "",
                                    price = (data["price"] as? Number)?.toDouble() ?: 0.0,
                                    profileImage = data["profile_image"] as? String ?: ""
                                )
                                salonCache[booking.serviceId] = salon
                            }
                        } catch (_: Exception) {
                            // Handle error fetching salon details
                        }
                    }
                }
            } catch (_: Exception) {
                // Handle error fetching bookings
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val context = LocalContext.current
        if (isLoading) {
            CircularProgressIndicator(
                color = GreenColor,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Bookings",
                            style = TextStyle(
                                fontSize = 28.sp,
                                fontFamily = GothamBlack,
                                fontWeight = FontWeight.Black,
                            ),
                            color = DarkGreenColor
                        )

                        // Log out button
                        TextButton(
                            onClick = {
                                // Sign out the user
                                FirebaseAuth.getInstance().signOut()

                                // Navigate back to login activity
                                val intent = Intent(context, LoginScreen::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = DarkGreenColor
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = DarkGreenColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Logout",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = Aeonik,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (pendingBookings.isNotEmpty()) {
                    item {
                        Text(
                            text = "Pending Bookings",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = Aeonik,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = DarkGreenColor,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(pendingBookings) { booking ->
                        BookingCard(
                            booking = booking,
                            salon = salonCache[booking.serviceId],
                            onCancelBooking = { bookingId ->
                                // Update booking status to "canceled"
                                coroutineScope.launch {
                                    try {
                                        firestore.collection("bookings")
                                            .document(bookingId)
                                            .update("status", "canceled")
                                            .await()

                                        // Remove booking from pending list
                                        pendingBookings = pendingBookings.filter { it.id != bookingId }
                                    } catch (_: Exception) {
                                        // Handle error updating booking
                                    }
                                }
                            }
                        )
                    }
                }

                if (completedBookings.isNotEmpty()) {
                    item {
                        Text(
                            text = "Completed Bookings",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = Aeonik,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = DarkGreenColor,
                            modifier = Modifier.padding(vertical = 8.dp).padding(top = 24.dp)
                        )
                    }

                    items(completedBookings) { booking ->
                        BookingCard(
                            booking = booking,
                            salon = salonCache[booking.serviceId],
                            onCancelBooking = null // No cancel option for completed bookings
                        )
                    }
                }

                if (pendingBookings.isEmpty() && completedBookings.isEmpty()) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "No bookings",
                                tint = LightGrayGreenColor,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No bookings found",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontFamily = Aeonik,
                                    fontWeight = FontWeight.Normal,
                                ),
                                color = DarkGreenColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: ProfileBooking,
    salon: ProfileSalonService?,
    onCancelBooking: ((String) -> Unit)?
) {
    val isPending = booking.status == "pending"
    val context = LocalContext.current

    // Handle image conversion outside of the composable
    val imageBitmap = remember(salon?.profileImage) {
        if (salon?.profileImage?.isNotEmpty() == true) {
            try {
                val imageBytes = Base64.decode(salon.profileImage, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                if (bitmap != null) {
                    androidx.compose.ui.graphics.ImageBitmap.Companion.imageResource(context.resources, android.R.drawable.ic_menu_gallery)
                    bitmap.asImageBitmap()
                } else null
            } catch (_: Exception) {
                null
            }
        } else null
    }

    Card(
        shape = RoundedCornerShape(16.dp),
//        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Salon Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightGrayGreenColor.copy(alpha = 0.3f))
                    .border(1.dp, LightGrayGreenColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap != null) {
                    // If we have a valid bitmap, display it
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Salon Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback when no image available
                    Text(
                        text = booking.serviceName.firstOrNull()?.toString() ?: "S",
                        color = DarkGreenColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Rest of the code remains the same
            Spacer(modifier = Modifier.width(16.dp))

            // Booking Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = booking.serviceName,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = Aeonik,
                        fontWeight = FontWeight.Bold
                    ),
                    color = DarkGreenColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${booking.price}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = Aeonik,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GreenColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = booking.timeSlot,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Aeonik
                    ),
                    color = Color.DarkGray
                )

                Text(
                    text = booking.bookingDate,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Aeonik
                    ),
                    color = Color.DarkGray
                )
            }

            // Cancel Button (only for pending bookings)
            if (isPending && onCancelBooking != null) {
                Button(
                    onClick = { onCancelBooking(booking.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.8f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Aeonik,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}