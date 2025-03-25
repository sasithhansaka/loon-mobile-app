package com.cns.loon.ui.signUpScreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cns.loon.R
import com.cns.loon.ui.loginScreen.LoginScreen
import com.cns.loon.ui.theme.Aeonik
import com.cns.loon.ui.theme.DarkGreenColor
import com.cns.loon.ui.theme.GothamBlack
import com.cns.loon.ui.theme.GreenColor

class SignUpSelectionScreen : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignUpSelectionUI(
                onUserSignUpClick = {
                    startActivity(Intent(this, UserSignUpScreen::class.java))
                },
                onSalonOwnerSignUpClick = {
                    startActivity(Intent(this, SalonOwnerSignUpScreen::class.java))
                },
                onLoginClick = {
                    startActivity(Intent(this, LoginScreen::class.java))
                }
            )
        }
    }
}

@Composable
fun SignUpSelectionUI(
    onUserSignUpClick: () -> Unit,
    onSalonOwnerSignUpClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_1),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(16.dp)
        ) {
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
                text = "Choose Registration Type",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = Aeonik,
                    fontWeight = FontWeight.Bold,
                ),
                color = DarkGreenColor
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    onClick = onUserSignUpClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenColor
                    )
                ) {
                    Text(
                        text = "USER",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = GothamBlack,
                            fontWeight = FontWeight.Black,
                        ),
                        color = DarkGreenColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onSalonOwnerSignUpClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenColor
                    )
                ) {
                    Text(
                        text = "SALON",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = GothamBlack,
                            fontWeight = FontWeight.Black,
                        ),
                        color = DarkGreenColor
                    )
                }
            }
            
            TextButton(onClick = onLoginClick) {
                Text(
                    text = "Already have an account? Login",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = Aeonik,
                    ),
                    color = DarkGreenColor
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
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
fun SignUpSelectionScreenPreview() {
    SignUpSelectionUI(
        onUserSignUpClick = { },
        onSalonOwnerSignUpClick = { },
        onLoginClick = { }
    )
}

