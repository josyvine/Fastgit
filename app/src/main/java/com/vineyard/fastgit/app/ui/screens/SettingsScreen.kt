package com.vineyard.fastgit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vineyard.fastgit.app.ui.theme.*
import com.vineyard.fastgit.app.viewmodel.AuthViewModel
import com.vineyard.fastgit.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val cacheSize by settingsViewModel.cacheSize.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GhBgDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // General Preferences Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GhSurfaceDark),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("App Preferences", fontWeight = FontWeight.Bold, color = GhAccentBlue, fontSize = 14.sp)

                SettingsRow(
                    icon = Icons.Default.Palette,
                    title = "Theme Mode",
                    subtitle = themeMode,
                    onClick = {
                        val next = when (themeMode) {
                            "Dark" -> "Light"
                            "Light" -> "System"
                            else -> "Dark"
                        }
                        settingsViewModel.setTheme(next)
                    }
                )

                Divider(color = GhCardBorderDark)

                SettingsRow(
                    icon = Icons.Default.FolderZip,
                    title = "Downloads Directory",
                    subtitle = "Internal Storage / Downloads / FastGit",
                    onClick = {}
                )

                Divider(color = GhCardBorderDark)

                SettingsRow(
                    icon = Icons.Default.CleaningServices,
                    title = "Clear Cache",
                    subtitle = "Cached trees and offline DB ($cacheSize)",
                    onClick = { settingsViewModel.clearCache() }
                )
            }
        }

        // Account & Security Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GhSurfaceDark),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Account & Security", fontWeight = FontWeight.Bold, color = GhAccentBlue, fontSize = 14.sp)

                SettingsRow(
                    icon = Icons.Default.Security,
                    title = "Authentication Token",
                    subtitle = if (authViewModel.tokenManager.isDemoMode()) "Demo Account" else "Encrypted Token Stored",
                    onClick = {}
                )

                Divider(color = GhCardBorderDark)

                SettingsRow(
                    icon = Icons.Default.ExitToApp,
                    title = "Log Out",
                    subtitle = "Disconnect current account session",
                    iconTint = GhErrorRed,
                    onClick = { showLogoutDialog = true }
                )
            }
        }

        // About & Version Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GhSurfaceDark),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("About FastGit", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Text("Version 1.0.0 (Build 100)", color = GhTextSecondaryDark, fontSize = 13.sp)
                Text("Built with Kotlin, Coroutines & Jetpack Compose for Android", color = GhTextSecondaryDark, fontSize = 12.sp)
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out", color = Color.White) },
            text = { Text("Are you sure you want to log out of FastGit?", color = GhTextSecondaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GhErrorRed)
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = GhSurfaceDark
        )
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = GhAccentBlue,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 15.sp)
            Text(subtitle, color = GhTextSecondaryDark, fontSize = 12.sp)
        }
    }
}
