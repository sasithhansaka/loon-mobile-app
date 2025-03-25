package com.cns.loon.ui.homeScreen

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale as JavaLocale

data class SalonService(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0
)

data class Booking(
    val serviceName: String = "",
    val userId: String = "",
    val serviceId: String = "",
    val date: Date = Date(),
    val timeSlot: String = "",
    val status: String = "pending",
    val price: Double = 0.0
) {
    // Convert the Booking object to a map for Firestore with custom field names
    fun toMap(): Map<String, Any> {
        val displayDateFormatter = SimpleDateFormat("dd MMMM yyyy", JavaLocale.getDefault())
        val rawDateFormatter = SimpleDateFormat("yyyy-MM-dd", JavaLocale.getDefault())
        return mapOf(
            "serviceName" to serviceName,
            "serviceId" to serviceId,
            "userId" to userId,
            "bookingDate" to displayDateFormatter.format(date), // For display (e.g., "28 March 2025")
            "bookingDateRaw" to rawDateFormatter.format(date), // For querying (e.g., "2025-03-28")
            "timeSlot" to timeSlot,
            "status" to status,
            "price" to price
        )
    }
}

@Composable
fun SearchFragment(keyword: String = "") {
    val db = Firebase.firestore
    var services by remember { mutableStateOf<List<SalonService>>(emptyList()) }
    var isLoading by remember { mutableStateOf(keyword.isNotEmpty()) }
    var searchQuery by remember { mutableStateOf(keyword) }
    val isFromBottomNav = keyword.isEmpty()
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<SalonService?>(null) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", JavaLocale.getDefault())
    var dateText by remember { mutableStateOf(dateFormatter.format(calendar.time)) }
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: "unknown"

    val timeSlots = listOf(
        "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM",
        "12:00 PM - 1:00 PM", "1:00 PM - 2:00 PM", "2:00 PM - 3:00 PM",
        "3:00 PM - 4:00 PM", "4:00 PM - 5:00 PM"
    )

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isLoading = true
            println("Fetching data for category: $searchQuery")
            db.collection("services")
                .whereEqualTo("category", searchQuery.trim().capitalize(Locale.current))
                .get()
                .addOnSuccessListener { result ->
                    services = result.map { doc ->
                        // Safely get the price with error handling
                        val price = try {
                            doc.getDouble("price") ?: 0.0
                        } catch (_: Exception) {
                            // Handle the case where price might be a different type
                            val priceValue = doc.get("price")
                            when (priceValue) {
                                is Long -> priceValue.toDouble()
                                is String -> priceValue.toDoubleOrNull() ?: 0.0
                                else -> 0.0
                            }
                        }

                        SalonService(
                            id = doc.id, // Store the document ID
                            name = doc.getString("name") ?: "",
                            price = price
                        )
                    }
                    isLoading = false
                    println("Fetched ${services.size} services")
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    println("Fetch failed: ${exception.message}")
                }
        } else {
            services = emptyList()
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isFromBottomNav) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent),
                    placeholder = { Text("Search for a service...", color = Color.Gray.copy(alpha = 0.7f)) },
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF006400)
                    ),
                    singleLine = true
                )
                Button(
                    onClick = { searchQuery = searchQuery.trim().capitalize(Locale.current) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(48.dp)
                ) {
                    Text("Search", color = Color.White, fontSize = 14.sp)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                searchQuery.isEmpty() -> {
                    Text(
                        text = "Please enter a search term or select a category",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                services.isEmpty() -> {
                    Text(
                        text = "No services found for '$searchQuery'",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = if (isFromBottomNav) 8.dp else 0.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(services) { service ->
                            ServiceCard(service, onBookNow = {
                                selectedService = service
                                showBookingDialog = true
                            })
                        }
                    }
                }
            }
        }

        if (showBookingDialog && selectedService != null) {
            Dialog(onDismissRequest = { showBookingDialog = false }) {
                Column(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("Book ${selectedService!!.name}", style = TextStyle(fontSize = 20.sp))

                    Text("Selected Date: $dateText")
                    Button(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    calendar.set(year, month, day)
                                    selectedDate = calendar.time
                                    dateText = dateFormatter.format(selectedDate ?: Date())
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(48.dp)
                    ) {
                        Text("Pick Date", color = Color.White)
                    }

                    var selectedSlot by remember { mutableStateOf<String?>(null) }
                    Column {
                        timeSlots.forEach { slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedSlot = slot }
                                    .background(if (selectedSlot == slot) Color(0xFFE0E0E0) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(slot, color = if (selectedSlot == slot) Color(0xFF006400) else Color.Black)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (selectedSlot != null && selectedDate != null) {
                                val booking = Booking(
                                    serviceName = selectedService!!.name,
                                    serviceId = selectedService!!.id,
                                    userId = currentUserId,
                                    date = selectedDate!!,
                                    timeSlot = selectedSlot!!,
                                    price = selectedService!!.price
                                )
                                saveBookingToFirestore(booking)
                                showBookingDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(48.dp),
                        enabled = selectedSlot != null && selectedDate != null
                    ) {
                        Text("Confirm Booking", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: SalonService, onBookNow: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(service.name, style = TextStyle(fontSize = 18.sp, color = Color(0xFF006400)))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Price: $${service.price}", style = TextStyle(fontSize = 16.sp))
            }
            Button(
                onClick = onBookNow,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(40.dp)
            ) {
                Text("Book Now", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

fun saveBookingToFirestore(booking: Booking) {
    val db = Firebase.firestore
    db.collection("bookings")
        .add(booking.toMap()) // Use the toMap() function to save the formatted data
        .addOnSuccessListener { documentReference ->
            println("Booking added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Error adding booking: $e")
        }
}

// Preview for the SearchFragment
@Preview(showBackground = true, name = "Search Fragment Preview")
@Composable
fun SearchFragmentPreview() {
    MaterialTheme {
        SearchFragment(keyword = "Hair Cutting")
    }
}