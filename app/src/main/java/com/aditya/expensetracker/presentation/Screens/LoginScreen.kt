package com.example.expensemanager.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.GMobiledata
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Black = Color(0xFF111111)
private val White = Color.White
private val Gray = Color(0xFF757575)
private val FieldColor = Color(0xFFF5F5F5)

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onGoogleLoginClick: () -> Unit = {}
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(170.dp))

            Text(
                text = "Welcome Back",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Username",
                modifier = Modifier.fillMaxWidth(),
                color = Gray,
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
                color = Gray,
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
                        onForgotPasswordClick()
                    },
                textAlign = TextAlign.End,
                color = Black,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            LoginButton(
                loading = isLoading,
                onClick = {
                    if(isLoading==false){
                        isLoading = true
                        onLoginClick()
                    }

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
                    color = Gray
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SocialButton(
                text = "Continue with Google",
                icon = {

                    Text(
                        text = "G",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                },
                onClick = onGoogleLoginClick
            )



            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "Don't have an account?",
                    color = Gray
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Sign Up",
                    color = Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onSignUpClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
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
                color = Gray
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
                    modifier = Modifier.clickable {
                        passwordVisible = !passwordVisible
                    }
                )
            }
        },

        shape = RoundedCornerShape(16.dp),

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
fun SocialButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Black),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor = Black
        )
    ) {

        icon()

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text,
            fontWeight = FontWeight.Medium
        )
    }
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
        enabled = true,
        shape = RoundedCornerShape(16.dp),
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
                "Login",
                color = White,
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
        LoginScreen(
            onLoginClick = {},
            onForgotPasswordClick = {},
            onSignUpClick = {},
            onGoogleLoginClick = {}
        )
    }
}
