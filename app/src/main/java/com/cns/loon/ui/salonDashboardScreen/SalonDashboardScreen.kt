package com.cns.loon.ui.salonDashboardScreen

import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import java.util.*
import javax.mail.*
import javax.mail.internet.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.cns.loon.R
import com.cns.loon.ui.theme.LightGreenHighOpacity
import com.cns.loon.ui.theme.poppins
import com.cns.loon.ui.theme.Aeonik
import com.cns.loon.ui.theme.BlackColorLowOpacity
import com.cns.loon.ui.theme.LightGreenLowOpacity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp

class SalonDashboardScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.White)) {
                SalonDashboardContent()
            }
        }
    }
}


@Composable
fun SalonDashboardContent() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth
    val salonID = auth.currentUser?.uid ?: "unknown"

    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var totalBookings by remember { mutableIntStateOf(0) }
    var totalRevenue by remember { mutableDoubleStateOf(0.0) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val horizontalPadding = (screenWidth * 0.05f).coerceAtMost(16.dp) 

    LaunchedEffect(salonID) {
        getBookingsData(salonID) { fetchedBookings, totalBookingsCount, totalRevenueAmount ->
            bookings = fetchedBookings
            totalBookings = totalBookingsCount
            totalRevenue = totalRevenueAmount
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_bg_removed),
                        contentDescription = "Salon Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .padding(5.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "salonName",
                        modifier = Modifier.padding(2.dp),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = poppins,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }

                // Profile icon on the right
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "profile",
                    modifier = Modifier
                        .size(36.dp)
                        .padding(5.dp)
                        .clickable {
                            val intent = Intent(context, SalonDashboardProfileScreen::class.java)
                            context.startActivity(intent)
                        }
                )
            }

            // Dashboard Text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = 8.dp),
            ) {
                Text(
                    text = "Dashboard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Aeonik,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                Text(
                    text = "Manage salon operations including bookings, revenue, and services. Track performance and make informed decisions to enhance the business.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = poppins,
                    color = Color.Black
                )
            }

            // Stats Boxes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // First Box - Total Sales
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(LightGreenHighOpacity, shape = RoundedCornerShape(10.dp))
                        .padding(11.dp)
                        .height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.increase),
                            contentDescription = "increase",
                            modifier = Modifier.size(24.dp)
                        )
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "$totalBookings +",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "total sales",
                                fontSize = 11.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Second Box - Revenue
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(LightGreenHighOpacity, shape = RoundedCornerShape(10.dp))
                        .padding(11.dp)
                        .height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pouch),
                            contentDescription = "revenue",
                            modifier = Modifier.size(24.dp)
                        )
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "$${"%.2f".format(totalRevenue)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "total revenue",
                                fontSize = 11.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Appointments Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = 8.dp),
            ) {
                Text(
                    text = "Appointments",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Aeonik,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Pending",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = poppins,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        // Show Pending Appointments
        items(bookings.filter { it.status == "pending" }) { booking ->
            BookingItem(booking, horizontalPadding) { bookingId, newStatus ->
                updateBookingStatus(bookingId, newStatus) {
                    Toast.makeText(context, "Booking status updated", Toast.LENGTH_SHORT).show()
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = 8.dp),
            ) {
                Text(
                    text = "Approved",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Aeonik,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        items(bookings.filter { it.status == "done" }) { booking ->
            BookingItem(booking, horizontalPadding) { bookingId, newStatus ->
                updateBookingStatus(bookingId, newStatus) {
                    Toast.makeText(context, "Booking status updated", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Add bottom padding
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BookingItem(
    booking: Booking,
    horizontalPadding: Dp = 16.dp,
    onStatusUpdated: (String, String) -> Unit = { _, _ -> }
) {
    var status by remember { mutableStateOf(booking.status) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightGreenLowOpacity, shape = RoundedCornerShape(8.dp))
                .drawBehind {
                    drawLine(
                        color = BlackColorLowOpacity,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 3.dp.toPx()
                    )
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Username - take up available space but leave room for other elements
                    Text(
                        text = booking.userName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppins,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Date - make sure it doesn't get too wide
                    Text(
                        text = booking.bookingDate,
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .widthIn(max = 100.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Button - only show for pending items
                    when (status) {
                        "pending" -> Button(
                            onClick = {
                                status = "done"
                                onStatusUpdated(booking.bookingId, "done")
                            },
                            modifier = Modifier
                                .height(25.dp)
                                .width(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(15.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Approve",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Price
                Text(
                    text = "$${booking.price}",
                    fontWeight = FontWeight.Normal,
                    fontFamily = poppins,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

fun getBookingsData(
    salonName: String,
    onDataFetched: (List<Booking>, Int, Double) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val bookingsRef = db.collection("bookings")

    bookingsRef.whereEqualTo("serviceId", salonName).get().addOnSuccessListener { result ->
        val bookingsList = mutableListOf<Booking>()
        var processedCount = 0
        var totalRevenue = 0.0

        if (result.isEmpty) {
            onDataFetched(bookingsList, 0, totalRevenue)
            return@addOnSuccessListener
        }

        result.forEach { document ->
            val booking = document.toObject(Booking::class.java)
            booking.bookingId = document.id

            if (booking.status == "done") {
                totalRevenue += booking.price
            }

            getUserNameFromFirestoreDB(booking.userId) { username ->
                booking.userName = username
                bookingsList.add(booking)

                processedCount++
                if (processedCount == result.size()) {
                    onDataFetched(bookingsList, result.size(), totalRevenue)
                }
            }
        }
    }
}

fun getUserNameFromFirestoreDB(userId: String, onUsernameFetched: (String) -> Unit) {
    if (userId.isEmpty()) {
        Log.e("FirestoreError", "UserID is empty!")
        onUsernameFetched("Unknown User")
        return
    }

    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)

    userRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val firstName = document.getString("firstName")?.trim() ?: ""
                val lastName = document.getString("lastName")?.trim() ?: ""

                val fullName = when {
                    firstName.isNotEmpty() && lastName.isNotEmpty() -> "$firstName $lastName".uppercase()
                    firstName.isNotEmpty() -> firstName.uppercase()
                    lastName.isNotEmpty() -> lastName.uppercase()
                    else -> "UNKNOWN USER"
                }


                Log.d("FirestoreSuccess", "Fetched username: $fullName for userId: $userId")
                onUsernameFetched(fullName)
            } else {
                Log.e("FirestoreError", "No user found with ID: $userId")
                onUsernameFetched("Unknown User")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("FirestoreError", "Failed to fetch user: ${exception.message}")
            onUsernameFetched("Unknown User")
        }
}

fun getBookingDetailsAndSendEmail(bookingId: String) {
    val db = FirebaseFirestore.getInstance()
    val bookingRef = db.collection("bookings").document(bookingId)

    bookingRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val userId = document.getString("userId") ?: ""
            val bookingDate = document.getString("bookingDate") ?: "N/A"
            val timeSlot = document.getString("timeSlot") ?: "N/A"
            val price = document.getDouble("price") ?: 0.0
            val serviceName = document.getString("serviceName") ?: "Salon"

            if (userId.isNotEmpty()) {
                val userRef = db.collection("users").document(userId)
                userRef.get().addOnSuccessListener { userDocument ->
                    val userEmail = userDocument.getString("email") ?: ""
                    val userName = userDocument.getString("firstName") ?: "Valued Customer"

                    if (userEmail.isNotEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            sendApprovalEmail(userEmail, userName, bookingDate, timeSlot, price, serviceName)
                        }
                    } else {
                        Log.e("EmailError", "User email not found for booking ID: $bookingId")
                    }
                }
            }
        }
    }.addOnFailureListener {
        Log.e("FirestoreError", "Failed to fetch booking details")
    }
}

fun updateBookingStatus(bookingId: String, newStatus: String, onSuccess: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val bookingRef = db.collection("bookings").document(bookingId)

    bookingRef.update("status", newStatus).addOnSuccessListener {
        if (newStatus == "done") {
            getBookingDetailsAndSendEmail(bookingId)
        }
        onSuccess()
    }.addOnFailureListener {
        Log.e("FirestoreError", "Failed to update status")
    }
}

fun sendApprovalEmail(userEmail: String, userName: String, bookingDate: String, timeSlot: String, price: Double, serviceName: String) {
    val sendingEmail = "petcaresystem9@gmail.com"
    val sendingEmailPassword = "sclp zwpg kera pcut"

    val properties = Properties()
    properties["mail.smtp.host"] = "smtp.gmail.com"
    properties["mail.smtp.socketFactory.port"] = "465"
    properties["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
    properties["mail.smtp.auth"] = "true"
    properties["mail.smtp.port"] = "465"

    val session = Session.getDefaultInstance(properties, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(sendingEmail, sendingEmailPassword)
        }
    })

    try {
        val mimeMessage = MimeMessage(session)
        mimeMessage.setFrom(InternetAddress(sendingEmail))
        mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(userEmail))
        mimeMessage.subject = "Booking Confirmation - $serviceName"

        val emailBody = """
            Hello $userName, 

            BOOKING ALERT
            
            We are thrilled to inform you that your booking at $serviceName has been successfully approved! ✨

            🏷️ Booking Details 
            📅 Date: $bookingDate  
            ⏰ Time Slot: $timeSlot  
            💰 Total Price: $${"%.2f".format(price)}  

            We are thrilled to welcome you and ensure you have an exceptional experience.  
            If you need any modifications or have any inquiries, feel free to reach out to us anytime! 

            Thank you for choosing $serviceName! 
            We truly appreciate your trust and can’t wait to serve you!  

            See you soon!

            Best Regards,
            $serviceName Team  
            Contact: +94 744 078 67 
            Website: loonOnWeb.lk
        """.trimIndent()

        mimeMessage.setText(emailBody)

        Transport.send(mimeMessage)
        Log.d("Email", "Email sent successfully to $userEmail")
    } catch (e: MessagingException) {
        e.printStackTrace()
        Log.e("EmailError", "Failed to send email: ${e.message}")
    }
}

data class Booking(
    var bookingId: String = "",
    val serviceName: String = "",
    val price: Double = 0.0,
    val status: String = "",
    val timeSlot: String = "",
    val userId: String = "",
    val bookingDate: String = "",
    var userName: String = ""
)

@Preview(showBackground = true)
@Composable
fun PreviewSalonDashboardContent() {
    val mockBookings = listOf(
        Booking(
            bookingId = "booking1",
            serviceName = "Haircut & Style",
            price = 45.99,
            status = "pending",
            timeSlot = "10:00 AM - 11:00 AM",
            userId = "user1",
            bookingDate = "2023-05-15",
            userName = "JOHN SMITH"
        ),
        Booking(
            bookingId = "booking2",
            serviceName = "Hair Coloring",
            price = 85.50,
            status = "done",
            timeSlot = "1:00 PM - 2:30 PM",
            userId = "user2",
            bookingDate = "2023-05-16",
            userName = "EMMA JOHNSON"
        ),
        Booking(
            bookingId = "booking3",
            serviceName = "Hair Treatment",
            price = 65.75,
            status = "pending",
            timeSlot = "3:00 PM - 4:00 PM",
            userId = "user3",
            bookingDate = "2023-05-17",
            userName = "MICHAEL BROWN"
        ),
        Booking(
            bookingId = "booking4",
            serviceName = "Haircut & Beard Trim",
            price = 55.00,
            status = "done",
            timeSlot = "11:30 AM - 12:30 PM",
            userId = "user4",
            bookingDate = "2023-05-18",
            userName = "ROBERT DAVIS"
        )
    )

    val mockTotalBookings = 12
    val mockTotalRevenue = 687.45
    val mockSalonName = "Elegant Salon & Spa"

    MockSalonDashboardScreen(
        mockBookings = mockBookings,
        mockTotalBookings = mockTotalBookings,
        mockTotalRevenue = mockTotalRevenue,
        mockSalonName = mockSalonName
    )
}

@Composable
fun MockSalonDashboardScreen(
    mockBookings: List<Booking>,
    mockTotalBookings: Int,
    mockTotalRevenue: Double,
    mockSalonName: String
) {
    LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_bg_removed),
                        contentDescription = "Salon Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .padding(5.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = mockSalonName,
                        modifier = Modifier.padding(2.dp),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = poppins,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                    Spacer(modifier = Modifier.width(110.dp))

                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "profile",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(5.dp)
                            .clickable {}
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, top = 12.dp),
                ) {
                    Text(
                        text = "Dashboard",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Aeonik,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )

                    Text(
                        text = "Manage salon operations including bookings, revenue, and services. Track performance and make informed decisions to enhance the business.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = poppins,
                        color = Color.Black
                    )
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // First Box - Total Sales
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(LightGreenHighOpacity, shape = RoundedCornerShape(10.dp))
                            .padding(11.dp)
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.increase),
                                contentDescription = "increase",
                                modifier = Modifier.size(24.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Text(
                                    text = "$mockTotalBookings +",
                                    fontSize = 18.sp, // Slightly reduced font size for better responsiveness
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    maxLines = 1 // Prevent text wrapping
                                )
                                Text(
                                    text = "total sales",
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Second Box - Total Revenue
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(LightGreenHighOpacity, shape = RoundedCornerShape(10.dp))
                            .padding(11.dp)
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pouch),
                                contentDescription = "increase",
                                modifier = Modifier.size(24.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Text(
                                    text = "$${"%.2f".format(mockTotalRevenue)}",
                                    fontSize = 18.sp, // Slightly reduced for responsiveness
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    maxLines = 1 // Prevent text wrapping
                                )
                                Text(
                                    text = "total revenue",
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, top = 16.dp),
                ) {

                    Text(
                        text = "Appointments",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Aeonik,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Pending",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = poppins,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            // Show Pending Appointments
            items(mockBookings.filter { it.status == "pending" }) { booking ->
                BookingItem(booking)
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, top = 16.dp),
                ) {
                    Text(
                        text = "Approved",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Aeonik,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            // Show Done Appointments
            items(mockBookings.filter { it.status == "done" }) { booking ->
                BookingItem(booking)
            }
        }
    }
}