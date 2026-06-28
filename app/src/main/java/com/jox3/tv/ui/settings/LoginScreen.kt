package com.jox3.tv.ui.settings

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jox3.tv.ui.theme.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var url by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("80") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var serverName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    LaunchedEffect(uiState.loginSuccess) { if (uiState.loginSuccess) navController.popBackStack() }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(CyanAccent, PurpleAccent))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("JOX3 TV", fontSize = 28.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text("Conectar servidor Xtream Codes", fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
            Spacer(modifier = Modifier.height(32.dp))

            // Fields
            LoginTextField(value = serverName, onValueChange = { serverName = it }, label = "Nombre del servidor (opcional)", icon = Icons.Outlined.Label, modifier = Modifier.focusRequester(focusRequester), imeAction = ImeAction.Next, onNext = { focusManager.moveFocus(FocusDirection.Down) })
            Spacer(Modifier.height(12.dp))
            LoginTextField(value = url, onValueChange = { url = it }, label = "URL del servidor", placeholder = "ej: servidor.com", icon = Icons.Outlined.Language, imeAction = ImeAction.Next, onNext = { focusManager.moveFocus(FocusDirection.Down) })
            Spacer(Modifier.height(12.dp))
            LoginTextField(value = port, onValueChange = { port = it }, label = "Puerto", placeholder = "80", icon = Icons.Outlined.Tag, keyboardType = KeyboardType.Number, imeAction = ImeAction.Next, onNext = { focusManager.moveFocus(FocusDirection.Down) })
            Spacer(Modifier.height(12.dp))
            LoginTextField(value = username, onValueChange = { username = it }, label = "Usuario", icon = Icons.Outlined.Person, imeAction = ImeAction.Next, onNext = { focusManager.moveFocus(FocusDirection.Down) })
            Spacer(Modifier.height(12.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = TextTertiary) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = TextTertiary)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = loginFieldColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); viewModel.login(url, port, username, password, serverName) })
            )

            Spacer(Modifier.height(8.dp))

            // Error
            AnimatedVisibility(visible = uiState.error != null) {
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(0.1f)), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Error, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(uiState.error ?: "", color = ErrorRed, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Login button
            Button(
                onClick = { viewModel.login(url, port, username, password, serverName) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                enabled = !uiState.isLoading
            ) {
                Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent)), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                    if (uiState.isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text("Conectar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Test connection
            OutlinedButton(
                onClick = { viewModel.testConnection(url, port, username, password) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent)
            ) {
                Icon(Icons.Outlined.WifiTethering, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Probar conexión", fontWeight = FontWeight.SemiBold)
            }

            AnimatedVisibility(visible = uiState.testResult != null) {
                val result = uiState.testResult
                Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), colors = CardDefaults.cardColors(containerColor = if (result == true) SuccessGreen.copy(0.1f) else ErrorRed.copy(0.1f)), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (result == true) Icons.Filled.CheckCircle else Icons.Filled.Error, null, tint = if (result == true) SuccessGreen else ErrorRed, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (result == true) "Conexión exitosa" else "No se pudo conectar", color = if (result == true) SuccessGreen else ErrorRed, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onNext: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let {{ Text(it, color = TextTertiary) }},
        leadingIcon = { Icon(icon, null, tint = TextTertiary) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = loginFieldColors(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(onNext = { onNext() })
    )
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CyanAccent,
    unfocusedBorderColor = SurfaceVariantDark,
    focusedContainerColor = SurfaceDark,
    unfocusedContainerColor = SurfaceDark,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = CyanAccent,
    unfocusedLabelColor = TextTertiary,
    cursorColor = CyanAccent
)
