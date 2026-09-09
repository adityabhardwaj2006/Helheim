package com.aditya.expensetracker.presentation.Screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.ViewModel.AuthViewModel
import com.aditya.expensetracker.presentation.navigation.Routes
import com.aditya.expensetracker.ui.theme.responsiveSp

@Composable
fun ProfileScreen(navController: NavController,
                  authViewModel: AuthViewModel = hiltViewModel()) {
    val scrollState = rememberScrollState()
    val progress = (scrollState.value/300f).coerceIn(0f,1f)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        authViewModel.getProfile()
    }
    val profileState by authViewModel.userState.collectAsState()
    val isLoading = profileState is ResultState.Loading || profileState is ResultState.Idle
    
    var username = ""
    var name = ""
    var email = ""
    var profilePhoto: String? = null
    
    when(val state = profileState){
        is ResultState.Success ->{
            username = state.data.data?.username?:""
            name = state.data.data?.firstName + " "+ state.data.data?.lastName
            email = state.data.data?.email?:""
            profilePhoto = state.data.data?.profilePictureUrl
        }
        is ResultState.Error -> {
            name = "Unknown"
        }
        else -> {}
    }

    val iconSize by animateDpAsState(
        targetValue = lerp(96.dp,30.dp,progress), label = ""
    )
    val nameSize by animateFloatAsState(
        targetValue = 20f - (4f*progress), label = ""
    )
    val emailAlpha by animateFloatAsState(
        targetValue = 1f - progress, label = ""
    )
    val headerTopPadding by animateDpAsState(
        targetValue = lerp(10.dp,0.dp,progress), label = ""
    )
    val spaceBelowIcon by animateDpAsState(
        targetValue = lerp(24.dp, 8.dp, progress),
        label = ""
    )

    // Check notification access and refresh when returning to screen
    var isAutoTrackEnabled by remember {
        mutableStateOf(isNotificationServiceEnabled(context))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAutoTrackEnabled = isNotificationServiceEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Profile",
                    fontSize = responsiveSp(22),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Box(modifier = Modifier.size(48.dp))
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
                    if (profilePhoto != null) {
                        AsyncImage(
                            model = profilePhoto,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(iconSize)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface, CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(iconSize)
                                .background(MaterialTheme.colorScheme.surface, CircleShape)
                                .padding(16.dp)
                        )
                    }

                }
            }

            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            } else {
                Text(
                    text = name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = responsiveSp(nameSize.toInt()),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = username,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = responsiveSp(15),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(emailAlpha),
                    textAlign = TextAlign.Center
                )
                if (email.isNotEmpty()) {
                    Text(
                        text = email,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        fontSize = responsiveSp(13),
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(emailAlpha),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(spaceBelowIcon))


            SectionTitle("Account & Utilities")
            SectionCard {
                CardItem(icon = Icons.Default.Person, text = "Edit Profile") {
                    navController.navigate(Routes.EditProfile)
                }
                CardItem(icon = Icons.Default.Lock, text = "Change Password",
                    onClick = {
                        navController.navigate(Routes.UpdatePassword)
                    }
                )

                CardItem(icon = Icons.Default.Payment, text = "Select Currency") {
                    navController.navigate(Routes.PaymentMethods)
                }

                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                }

                CardItem(
                    icon = Icons.Default.Notifications,
                    text = "Auto-track Transactions",
                    trailingContent = {
                        Switch(
                            checked = isAutoTrackEnabled,
                            onCheckedChange = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                                }
                            }
                        )
                    }
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                }

                CardItem(icon = Icons.Default.PrivacyTip, text = "Privacy Policy", showDivider = false) {
                    navController.navigate(Routes.PrivacyPolicy)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle("Support")
            SectionCard {
                CardItem(icon = Icons.AutoMirrored.Filled.HelpOutline, text = "Help & Support") {
                    navController.navigate(Routes.HelpSupport)
                }
                CardItem(icon = Icons.Default.Info, text = "About App") {
                    navController.navigate(Routes.AboutApp)
                }
                CardItem(icon = Icons.Default.Star, text = "Rate Us", showDivider = false) {
                    navController.navigate(Routes.RateUs)
                }
                CardItem(icon = Icons.Default.Notifications, text = "View Debug Logs", showDivider = false) {
                    navController.navigate(Routes.Notification)
                }
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

fun isNotificationServiceEnabled(context: Context): Boolean {
    val packageNames = NotificationManagerCompat.getEnabledListenerPackages(context)
    return packageNames.contains(context.packageName)
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = responsiveSp(15),
        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
    )
}

@Composable
fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
    }
}

@Composable
fun CardItem(
    icon: ImageVector,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    showDivider: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
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
                    .background(MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                fontSize = responsiveSp(15),
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            
            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
        if (showDivider) {
            Divider(
                modifier = Modifier.padding(start = 68.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                thickness = 1.dp
            )
        }
    }
}
