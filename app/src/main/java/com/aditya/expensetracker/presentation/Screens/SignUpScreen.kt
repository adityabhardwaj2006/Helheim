package com.aditya.expensetracker.presentation.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

private val Black = Color(0xFF111111)
private val White = Color.White
private val Gray = Color(0xFF757575)
private val FieldColor = Color(0xFFF5F5F5)
private val AccentBlue = Color(0xFF4C6FFF)

@Composable
fun SignUpScreen(
    navController: NavController,
    onSignUp: () -> Unit,
    onLoginClick: () -> Unit = {},
    onGoogleSignUpClick: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var firstname by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var passwordVisible2 by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(128.dp))




            Text(
                text = "Create a new account",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Black
            )


            Spacer(Modifier.height(28.dp))

            Text(
                text = "First Name",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Medium,
                color = Gray
            )

            Spacer(Modifier.height(6.dp))

            NameTextField(
                placeholder = "Enter your first name",
                firstname = firstname,
                onValueChange = {
                    firstname = it
                },
                keyboardType = KeyboardType.Text
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Last Name",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Medium,
                color = Gray
            )

            Spacer(Modifier.height(6.dp))

            NameTextField(
                placeholder = "Enter your last name",
                firstname = lastName,
                onValueChange = {
                    lastName = it
                },
                keyboardType = KeyboardType.Text
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Username",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Medium,
                color = Gray
            )

            Spacer(Modifier.height(6.dp))

            SignUpTextField(
                placeholder = "Enter username",
                firstname = username,
                onValueChange = {
                    username = it
                },
                keyboardType = KeyboardType.Text,
                passwordVisible = passwordVisible,
                onEyeClick = {
                    passwordVisible = !passwordVisible
                }
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Password",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Medium,
                color = Gray
            )

            Spacer(Modifier.height(6.dp))

            SignUpTextField(
                placeholder = "Enter password",
                firstname = password,
                onValueChange = {
                    password = it
                },
                keyboardType = KeyboardType.Password,
                passwordVisible = passwordVisible,
                onEyeClick = {
                    passwordVisible = !it
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            SignUpButton(
                loading,
                onClick = {
                    if (loading == false) {
                        loading = true
                        onSignUp()
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))

                Text(
                    text = " OR ",
                    color = Gray
                )

                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            GoogleSignUpButton(
                onClick = onGoogleSignUpClick
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Already have an account?",
                    color = Gray
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Login",
                    color = AccentBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onLoginClick()
                    }
                )
            }
        }
    }
}

@Composable
fun SignUpButton(
    loading: Boolean,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = true,
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Black
        )
    ) {

        if (loading) {

            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = White
            )

        } else {

            Text(
                "Sign Up",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

        }
    }
}

@Composable
fun GoogleSignUpButton(
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Black.copy(alpha = 0.15f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor = Black
        )
    ) {
        Text(
            text = "G",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = AccentBlue
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Continue with Google",
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NameTextField(placeholder: String, firstname: String,
                  onValueChange: (String) -> Unit,
                  keyboardType: KeyboardType) {
    TextField(
        value = firstname,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        placeholder = {
            Text(
                text = placeholder, color = Gray
            )
        },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FieldColor,
            unfocusedContainerColor = FieldColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Black
        )

    )
}

@Composable
fun SignUpTextField(placeholder: String, firstname: String,
                    onValueChange: (String) -> Unit,
                    keyboardType: KeyboardType,
                    passwordVisible : Boolean,
                    onEyeClick : (Boolean) -> Unit) {
    TextField(
        value = firstname,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        placeholder = {
            Text(
                text = placeholder, color = Gray
            )
        },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = {
            if(placeholder == "Enter username"){
                Icon(
                    imageVector = Icons.Default.Person2,
                    contentDescription = "",
                    tint = Black
                )
            }else{
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "",
                    tint = Black
                )
            }
        },
        visualTransformation =
            if(placeholder == "Enter password" && passwordVisible==false){
                PasswordVisualTransformation()
            }else{
                VisualTransformation.None
            },
        trailingIcon = {
            if(placeholder == "Enter password"){
                Icon(
                    imageVector =
                        if(passwordVisible==false){
                            Icons.Default.VisibilityOff
                        }else{
                            Icons.Default.Visibility
                        },
                    contentDescription = "",
                    tint = Black,
                    modifier = Modifier.clickable{
                        onEyeClick(passwordVisible)
                    }
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FieldColor,
            unfocusedContainerColor = FieldColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Black
        )

    )
}



@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(navController = rememberNavController(), onSignUp = {})
}