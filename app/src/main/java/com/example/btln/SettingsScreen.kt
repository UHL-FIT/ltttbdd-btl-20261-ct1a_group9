package com.example.btln

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(isDarkMode: Boolean, onBack: () -> Unit, onToggleDark: (Boolean) -> Unit) {
    val bgColor = if (isDarkMode) Color(0xFF0A1428) else Color(0xFFF5F5F5)
    val cardColor = if (isDarkMode) Color(0xFF152642) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài Đặt", color = textColor) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = textColor) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(padding).padding(16.dp)) {
            Text("Giao diện", color = Color(0xFF4FC3F7), fontWeight = FontWeight.Bold)
            Card(colors = CardDefaults.cardColors(containerColor = cardColor), modifier = Modifier.padding(top = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, null, tint = Color(0xFFFFC107))
                        Spacer(Modifier.width(12.dp))
                        Text("Chế độ tối (Dark Mode)", color = textColor)
                    }
                    Switch(checked = isDarkMode, onCheckedChange = { onToggleDark(it) })
                }
            }
        }
    }
}