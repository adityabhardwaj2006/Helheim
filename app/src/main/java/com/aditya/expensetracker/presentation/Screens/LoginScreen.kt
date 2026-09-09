package com.aditya.expensetracker.presentation.Screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.ViewModel.AuthViewModel
import com.aditya.expensetracker.presentation.components.SocialButton
import com.aditya.expensetracker.presentation.navigation.Routes
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aditya.expensetracker.R

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        val loginState by authViewModel.loginState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        val googleSignInLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            authViewModel.onGoogleSignInResult(result.data)
        }

        LaunchedEffect(loginState) {
            when(val state = loginState){
                is ResultState.Success<*> -> {
                    navController.navigate(Routes.HomeUi)
                }
                is ResultState.Error -> {
                    snackbarHostState.showSnackbar(state.message)
                }

                else -> {}
            }
        }

        val scrollState = rememberScrollState()
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Surface(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Here the lottie animation space
                LottieAnimation(
                    composition = rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.lottie_animation)
                    ).value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    iterations = LottieConstants.IterateForever,
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Welcome Back",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Username",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                LoginTextField(
                    value = username,
                    onValueChange = {
                        username = it
                    },
                    placeholder = "Enter username",
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person2,
                            contentDescription = null
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Password",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                LoginTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    placeholder = "Enter password",
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Forgot Password?",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
    //                        onForgotPasswordClick()
                        },
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                LoginButton(
                    loading = loginState is ResultState.Loading,
                    onClick = {
                            authViewModel.login(
                                username,
                                password
                            )

                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = " OR ",
                        color = Color.Gray
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SocialButton(
                    text = "Continue with Google",
                    icon = {

                        Icon(
                             painter = painterResource(R.drawable.google_icon_logo_svgrepo_com) ,
                            contentDescription = "Google",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(
                                24.dp
                            )
                        )

                    },
                    onClick = {
                        googleSignInLauncher.launch(authViewModel.getGoogleSignInIntent())
                    }
                )



                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Don't have an account?",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Sign Up",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.SignUpScreen)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        placeholder = {
            Text(
                placeholder,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        },
        leadingIcon = leadingIcon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        visualTransformation =
            if (isPassword && !passwordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,

        trailingIcon = {
            if (isPassword) {
                Icon(
                    imageVector =
                        if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,

                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.clickable {
                        passwordVisible = !passwordVisible
                    }
                )
            }
        },

        shape = RoundedCornerShape(16.dp),

        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    )
}

@Composable
fun LoginButton(
    loading: Boolean,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !loading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {

        if (loading) {

            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )

        } else {

            Text(
                "Login",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp
            )

        }
    }
}
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(hiltViewModel(),rememberNavController())
    }
}
