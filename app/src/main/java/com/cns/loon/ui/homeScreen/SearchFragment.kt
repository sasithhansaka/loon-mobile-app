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
import androidx.compose.ui.graphics.Brush
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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.CompositionLocalProvider
import com.cns.loon.ui.theme.*

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

/*@Composable
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
}*/

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        LightGreenLowOpacity.copy(alpha = 0.2f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isFromBottomNav) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(30.dp),
                            ambientColor = LightGreenColor.copy(alpha = 0.3f),
                            spotColor = LightGreenColor.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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
                            placeholder = { Text("Search for a service...", color = LightGrayGreenColor) },
                            textStyle = TextStyle(fontSize = 16.sp, color = DarkGreenColor),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = GreenColor
                            ),
                            singleLine = true
                        )
                        Button(
                            onClick = { searchQuery = searchQuery.trim().capitalize(Locale.current) },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenColor),
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .height(48.dp)
                        ) {
                            Text("Search", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = GreenColor
                        )
                    }
                    searchQuery.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                /*painter = painterResource(id = R.drawable.ic_search),*/ // Replace with your search icon
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = LightGrayGreenColor,
                                modifier = Modifier.size(50.dp)
                            )
                            Text(
                                text = "Please enter a search term or select a category",
                                modifier = Modifier.padding(top = 16.dp),
                                color = DarkGreenColor,
                                textAlign = TextAlign.Center,
                                style = TextStyle(fontSize = 16.sp)
                            )
                        }
                    }
                    services.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                /*painter = painterResource(id = R.drawable.ic_not_found), // Replace with your not found icon*/
                                imageVector = Icons.Filled.ErrorOutline,
                                contentDescription = "Not Found",
                                tint = LightGrayGreenColor,
                                modifier = Modifier.size(50.dp)
                            )
                            Text(
                                text = "No services found for '$searchQuery'",
                                modifier = Modifier.padding(top = 16.dp),
                                color = DarkGreenColor,
                                textAlign = TextAlign.Center,
                                style = TextStyle(fontSize = 16.sp)
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = if (isFromBottomNav) 8.dp else 0.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(services) { service ->
                                EnhancedServiceCard(service, onBookNow = {
                                    selectedService = service
                                    showBookingDialog = true
                                })
                            }
                        }
                    }
                }
            }
        }

        if (showBookingDialog && selectedService != null) {
            Dialog(onDismissRequest = { showBookingDialog = false }) {
                Card(
                    modifier = Modifier
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = DarkGreenColor.copy(alpha = 0.2f),
                            spotColor = DarkGreenColor.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Book ${selectedService!!.name}",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreenColor
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = LightGreenLowOpacity.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Selected Date:",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = DarkGreenColor.copy(alpha = 0.7f)
                                    )
                                )
                                Text(
                                    text = dateText,
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DarkGreenColor
                                    ),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

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
                            colors = ButtonDefaults.buttonColors(containerColor = GreenColor),
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .height(50.dp)
                                .shadow(4.dp, RoundedCornerShape(25.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Select Date",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Text(
                            "Available Time Slots",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreenColor
                            ),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        var selectedSlot by remember { mutableStateOf<String?>(null) }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            timeSlots.forEach { slot ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedSlot = slot },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedSlot == slot) LightGreenColor.copy(alpha = 0.3f) else Color.White
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (selectedSlot == slot) GreenColor else LightGrayGreenColor
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            slot,
                                            color = if (selectedSlot == slot) DarkGreenColor else DarkGreenColor.copy(alpha = 0.7f),
                                            fontWeight = if (selectedSlot == slot) FontWeight.Medium else FontWeight.Normal
                                        )
                                        if (selectedSlot == slot) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = GreenColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
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
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenColor,
                                disabledContainerColor = LightGrayGreenColor
                            ),
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                                .height(50.dp)
                                .shadow(4.dp, RoundedCornerShape(25.dp)),
                            enabled = selectedSlot != null && selectedDate != null
                        ) {
                            Text(
                                "Confirm Booking",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedServiceCard(service: SalonService, onBookNow: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = LightGrayGreenColor.copy(alpha = 0.3f),
                spotColor = LightGrayGreenColor.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                service.name,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreenColor
                )
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 1.dp,
                color = LightGreenLowOpacity
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Price",
                        tint = GreenColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "$${service.price}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkGreenColor
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Rating",
                        tint = GreenColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Rating",
                        tint = GreenColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Rating",
                        tint = GreenColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Rating",
                        tint = GreenColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Rating",
                        tint = LightGrayGreenColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Button(
                onClick = onBookNow,
                colors = ButtonDefaults.buttonColors(containerColor = GreenColor),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(45.dp)
                    .shadow(4.dp, RoundedCornerShape(25.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Book Now",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Book Now",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Preview for the SearchFragment - Fixed
@Preview(showBackground = true, name = "Search Fragment Preview")
@Composable
fun SearchFragmentPreview() {
    // Create a simple preview that shows sample service cards
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            LightGreenLowOpacity.copy(alpha = 0.2f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Sample service cards
                EnhancedServiceCard(
                    service = SalonService("1", "Haircut & Styling", 45.00),
                    onBookNow = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                EnhancedServiceCard(
                    service = SalonService("2", "Color Treatment", 75.00),
                    onBookNow = {}
                )
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

