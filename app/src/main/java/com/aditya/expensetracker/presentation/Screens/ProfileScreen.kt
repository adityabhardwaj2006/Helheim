package com.aditya.expensetracker.presentation.Screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aditya.expensetracker.data.ViewModel.AuthViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val progress = (scrollState.value/300f).coerceIn(0f,1f)

    val authViewModel : AuthViewModel = hiltViewModel()


    val iconSize by animateDpAsState(
        targetValue = lerp(96.dp,30.dp,progress)
    )
    val nameSize by animateFloatAsState(
        targetValue = 20f - (4f*progress)
    )
    val emailAlpha by animateFloatAsState(
        targetValue = 1f - progress
    )
    val headerTopPadding by animateDpAsState(
        targetValue = lerp(10.dp,0.dp,progress)
    )
    val spaceBelowIcon by animateDpAsState(
        targetValue = lerp(24.dp, 8.dp, progress),
        label = ""
    )
    Scaffold(
        containerColor = Color(0xFFF7F7F9),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.White)
                    .padding(all = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start)
                {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }

                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight()
                    , verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center)
                {

                    Text(
                        text = "Profile",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight()){



                }



            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = headerTopPadding, bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        tint = Color.White,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(iconSize)
                            .background(Color(0xFF000000), CircleShape)
                            .padding(16.dp)
                    )

                }
            }

            Text(
                text = "Aditya Bhardwaj",
                fontWeight = FontWeight.ExtraBold,
                fontSize = nameSize.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "adityabhardwaj@gmail.com",
                color = Color.Gray,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(emailAlpha),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spaceBelowIcon))


            SectionTitle("Account & Utilities")
            SectionCard {
                CardItem(icon = Icons.Default.Person, text = "Edit Profile") { }
                CardItem(icon = Icons.Default.Lock, text = "Change Password") { }
                CardItem(icon = Icons.Default.Notifications, text = "Notifications") { }
                CardItem(icon = Icons.Default.Payment, text = "Payment Methods") { }
                CardItem(icon = Icons.Default.PrivacyTip, text = "Privacy Policy", showDivider = false) { }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle("Support")
            SectionCard {
                CardItem(icon = Icons.AutoMirrored.Filled.HelpOutline, text = "Help & Support") { }
                CardItem(icon = Icons.Default.Info, text = "About App") { }
                CardItem(icon = Icons.Default.Star, text = "Rate Us", showDivider = false) { }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionCard {
                CardItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    text = "Log Out",
                    textColor = Color(0xFFE53935),
                    iconTint = Color(0xFFE53935),
                    showDivider = false,
                    onClick = {
                        authViewModel.logout()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
    )
}

@Composable
fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
    }
}

@Composable
fun CardItem(
    icon: ImageVector,
    text: String,
    textColor: Color = Color.Black,
    iconTint: Color = Color.Black,
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFF0F0F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
        if (showDivider) {
            Divider(
                modifier = Modifier.padding(start = 68.dp),
                color = Color(0xFFF0F0F0),
                thickness = 1.dp
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfilePreview(){
    ProfileScreen(rememberNavController())
}
