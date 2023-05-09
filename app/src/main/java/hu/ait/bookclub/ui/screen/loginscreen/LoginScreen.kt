package hu.ait.bookclub.ui.screen.loginscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import hu.ait.bookclub.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("midian@ait.hu") }
    var password by rememberSaveable { mutableStateOf("123456") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .background(Color(0xFFF2E2CE))
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 80.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = "Pages & Pals",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF705E5C),
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Sign in to get started",
                fontSize = 20.sp,
                color = Color(0xFF705E5C),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontFamily = FontFamily.Serif
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.library),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(
                        text = "E-mail",
                        color = Color(0xFF705E5C),
                        fontFamily = FontFamily.Serif
                    )
                },
                value = email,
                onValueChange = {
                    email = it
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = Color(0xFF705E5C)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color(0xFF705E5C),
                    focusedBorderColor = Color(0xFF705E5C)
                )
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(
                        text = "Password",
                        color = Color(0xFF705E5C),
                        fontFamily = FontFamily.Serif
                    )
                },
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Password,
                        contentDescription = "Password Icon",
                        tint = Color(0xFF705E5C)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        if (showPassword) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = "Show Password Icon",
                                tint = Color(0xFF705E5C)
                            )
                        } else {
                            Icon(
                                Icons.Default.VisibilityOff,
                                contentDescription = "Hide Password Icon",
                                tint = Color(0xFF705E5C)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color(0xFF705E5C),
                    focusedBorderColor = Color(0xFF705E5C)
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val result = loginViewModel.loginUser(email,password)
                        if (result?.user != null) {
                            withContext(Dispatchers.Main) {
                                onLoginSuccess()
                            }
                        }
                    }
                }) {
                    Text(
                        text = "Login",
                        fontFamily = FontFamily.Serif)
                }
                OutlinedButton(onClick = {
                    // do registration..
                    coroutineScope.launch {
                        loginViewModel.registerUser(email, password)
                    }
                }) {
                    Text(
                        text = "Register",
                        fontFamily = FontFamily.Serif)
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (loginViewModel.loginUiState) {
                is LoginUiState.Loading -> CircularProgressIndicator()
                is LoginUiState.RegisterSuccess ->
                    Text(text = "Registration OK", fontFamily = FontFamily.Serif)

                is LoginUiState.Error -> Text(text = "Error: ${
                    (loginViewModel.loginUiState as LoginUiState.Error).error
                }", fontFamily = FontFamily.Serif)

                is LoginUiState.LoginSuccess ->
                    Text(text = "Login OK", fontFamily = FontFamily.Serif)
                LoginUiState.Init -> {}
            }
        }
    }
}