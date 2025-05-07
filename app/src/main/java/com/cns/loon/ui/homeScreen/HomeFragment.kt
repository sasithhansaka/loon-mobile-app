package com.cns.loon.ui.homeScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cns.loon.R
import com.cns.loon.ui.theme.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun HomeFragment(navController: NavHostController) {
    // Custom font for branding elements
    val customFont = FontFamily(
        Font(R.font.londonbridgefontfamily)
    )

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
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Bar with Logo
            TopSection(customFont)

            // Welcome Banner
            WelcomeBanner(customFont)

            // Popular Services Section
            ServiceCategoriesSection(navController)

            // Special Offers
            SpecialOffersSection()

            // Featured Stylists
            FeaturedStylistsSection(navController)

            // Featured Gallery
            FeaturedGallerySection()

            // Bottom spacing
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun TopSection(customFont: FontFamily) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        // Logo centered
        Image(
            painter = painterResource(id = R.drawable.logo_text_2),
            contentDescription = "Loon Logo",
            modifier = Modifier
                .align(Alignment.Center)
                .height(60.dp)
                .width(140.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun WelcomeBanner(customFont: FontFamily) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = GreenColor.copy(alpha = 0.3f),
                spotColor = GreenColor.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            LightGreenColor.copy(alpha = 0.2f),
                            LightGreenLowOpacity.copy(alpha = 0.4f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Discover and Book Beauty Professionals",
                  //  text = "DISCOVER AND BOOK BEAUTY PROFESSIONALS",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Normal,
                    color = DarkGreenColor,
                    fontFamily = Aeonik,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp, bottom = 5.dp)
                )

                Button(
                    onClick = { /* Handle booking */ },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .shadow(4.dp, RoundedCornerShape(25.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenColor
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Book Now",
                        tint = DarkGreenColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BOOK NOW",
                        fontSize = 16.sp,
                        color = DarkGreenColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Aeonik,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ServiceCategoriesSection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OUR SERVICES",
                fontSize = 20.sp,
                fontFamily = GothamBlack,
                fontWeight = FontWeight.Black,
                color = DarkGreenColor,
                modifier = Modifier.padding(start = 5.dp)
            )
        }

        // Services Grid in an elevated box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                /*.shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = LightGrayGreenColor.copy(alpha = 0.5f),
                    spotColor = LightGrayGreenColor.copy(alpha = 0.5f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )*/
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // First Row of Services
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EnhancedServiceBox("Hair Cutting", R.drawable.haircutting, navController)
                    EnhancedServiceBox("Braids", R.drawable.braids, navController)
                    EnhancedServiceBox("Piercing", R.drawable.piercing, navController)
                }

                // Second Row of Services
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EnhancedServiceBox("Facial", R.drawable.facialreatment, navController)
                    EnhancedServiceBox("Eye Lashes", R.drawable.eyelashes, navController)
                    EnhancedServiceBox("Massage", R.drawable.massagetherapy, navController)
                }

                // Additional Service Categories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EnhancedServiceBox("Nails", R.drawable.nails, navController)
                    EnhancedServiceBox("Locs", R.drawable.locs, navController)
                    EnhancedServiceBox("Slick Press", R.drawable.slickpres, navController)
                }
            }
        }
    }
}

@Composable
fun EnhancedServiceBox(title: String, imageResId: Int, navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(95.dp)
            .clickable {
                navController.navigate("search?keyword=$title")
            }
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = LightGreenColor.copy(alpha = 0.3f),
                    spotColor = LightGreenColor.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            // Background image
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "$title Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                BlackColor.copy(alpha = 0.6f)
                            ),
                            startY = 10f,
                            endY = 200f
                        )
                    )
            )

            // Service title
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontFamily = Aeonik,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SpecialOffersSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SPECIAL OFFERS",
                fontSize = 20.sp,
                fontFamily = GothamBlack,
                fontWeight = FontWeight.Black,
                color = DarkGreenColor,
                modifier = Modifier.padding(start = 5.dp)
            )
        }

        // Scrollable Offers
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = LightGreenHighOpacity.copy(alpha = 0.2f),
                    spotColor = LightGreenHighOpacity.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background image
                Image(
                    painter = painterResource(id = R.drawable.banner_4), // Replace with promotional image
                    contentDescription = "Special Offer",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    BlackColor.copy(alpha = 0.7f),
                                    BlackColor.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Offer content
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 24.dp, end = 120.dp)
                ) {
                    Text(
                        text = "20% OFF",
                        fontSize = 28.sp,
                        fontFamily = GothamBlack,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Text(
                        text = "First-time Hair Styling",
                        fontSize = 16.sp,
                        fontFamily = GothamBlack,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Button(
                        onClick = { /* Book now */ },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .shadow(4.dp, RoundedCornerShape(20.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenColor
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "BOOK NOW",
                            fontSize = 14.sp,
                            color = DarkGreenColor,
                            fontFamily = GothamBlack,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedStylistsSection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TOP STYLISTS",
                fontSize = 20.sp,
                fontFamily = GothamBlack,
                fontWeight = FontWeight.Black,
                color = DarkGreenColor,
                modifier = Modifier.padding(start = 5.dp)
            )
        }

        // Stylists Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StylistCard(
                name = "Jennifer",
                specialty = "Hair Stylist",
                rating = 4.9f,
                imageResId = R.drawable.profile_1, // Replace with your stylist image resources
                navController = navController
            )

            StylistCard(
                name = "Michael",
                specialty = "Barber",
                rating = 4.8f,
                imageResId = R.drawable.profile_2, // Replace with your stylist image resources
                navController = navController
            )

            StylistCard(
                name = "Sophia",
                specialty = "Nail Artist",
                rating = 4.7f,
                imageResId = R.drawable.profile_3, // Replace with your stylist image resources
                navController = navController
            )

            StylistCard(
                name = "David",
                specialty = "Massage Therapist",
                rating = 4.9f,
                imageResId = R.drawable.profile_4, // Replace with your stylist image resources
                navController = navController
            )
        }
    }
}

@Composable
fun StylistCard(
    name: String,
    specialty: String,
    rating: Float,
    imageResId: Int,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = LightGrayGreenColor.copy(alpha = 0.3f),
                spotColor = LightGrayGreenColor.copy(alpha = 0.3f)
            )
            .clickable { /* Navigate to stylist profile */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            // Stylist image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .shadow(4.dp, CircleShape)
                    .background(LightGreenLowOpacity),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "$name Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stylist name
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreenColor,
                textAlign = TextAlign.Center
            )

            // Specialty
            Text(
                text = specialty,
                fontSize = 12.sp,
                color = LightGrayGreenColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "Rating",
                    tint = GreenColor,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = rating.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreenColor,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun FeaturedGallerySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STYLE GALLERY",
                fontSize = 20.sp,
                fontFamily = GothamBlack,
                fontWeight = FontWeight.Black,
                color = DarkGreenColor,
                modifier = Modifier.padding(start = 5.dp)
            )
        }

        // Gallery auto-sliding carousel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = LightGreenColor.copy(alpha = 0.2f),
                    spotColor = LightGreenColor.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Auto-sliding image carousel
                AutoSlidingCarousel()

                // Icon overlay in bottom right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(40.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .clickable { /* Like action */ }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = "Like",
                        tint = GreenColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSlidingCarousel() {
    val imageList = listOf(
        R.drawable.banner_1,
        R.drawable.banner_2,
        R.drawable.banner_3,
        R.drawable.loonstyle
    )

    val pagerState = rememberPagerState(pageCount = { imageList.size })

    // Auto-sliding logic
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Change slide every 3 seconds
            val nextPage = (pagerState.currentPage + 1) % imageList.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    // HorizontalPager for the images
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageSpacing = 0.dp,
        userScrollEnabled = true // Allow manual swiping too
    ) { page ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageList[page]),
                contentDescription = "Gallery Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Home Fragment Preview",
    device = "spec:width=1080px,height=5000px,dpi=440"
)
@Composable
fun HomeFragmentPreview() {
    MaterialTheme {
        HomeFragment(navController = rememberNavController())
    }
}