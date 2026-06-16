package com.example.btln

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThongKeScreen(isDarkMode: Boolean, onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Hệ Pokémon", "Vùng / Thế hệ")

    val bgColor = if (isDarkMode) Color(0xFF0A1428) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else Color.Black
    val headerColor = if (isDarkMode) Color(0xFF152642) else Color.White

    Column(modifier = Modifier.fillMaxSize().background(bgColor)) {
        TopAppBar(
            title = { Text("Thống kê dữ liệu", color = textColor, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, null, tint = textColor)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = headerColor)
        )

        TabRow(selectedTabIndex = selectedTab, containerColor = headerColor, contentColor = Color.Cyan) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold, color = textColor) },
                    icon = { Icon(if (index == 0) Icons.Default.BarChart else Icons.Default.Public, null, tint = if (selectedTab == index) Color.Cyan else Color.Gray) }
                )
            }
        }

        when (selectedTab) {
            0 -> TypeStatsSection(isDarkMode)
            1 -> GenerationStatsSection(isDarkMode)
        }
    }
}

@Composable
fun TypeStatsSection(isDarkMode: Boolean) {
    var stats by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.service.getAllTypes()
            stats = response.results.filter { it.name != "unknown" && it.name != "shadow" }.map {
                val detail = RetrofitClient.service.getTypeDetail(it.name)
                translateType(it.name) to detail.pokemon.size
            }.sortedByDescending { it.second }
        } catch (e: Exception) {} finally { isLoading = false }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.Cyan) }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(stats) { (name, count) ->
                StatBar(isDarkMode = isDarkMode, label = name, value = count, maxValue = 300, color = getTypeColor(name))
            }
        }
    }
}

@Composable
fun GenerationStatsSection(isDarkMode: Boolean) {
    var stats by remember { mutableStateOf<List<Triple<String, String, Int>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.service.getAllGenerations()
            stats = response.results.mapIndexed { index, _ ->
                val detail = RetrofitClient.service.getGenerationDetail(index + 1)
                Triple("Thế hệ ${index + 1}", detail.main_region.name.uppercase(), detail.pokemon_species.size)
            }
        } catch (e: Exception) {} finally { isLoading = false }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.Cyan) }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(stats) { (gen, region, count) ->
                Card(colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF152642) else Color.White)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(gen, color = Color.Cyan, fontWeight = FontWeight.Bold)
                            Text("Vùng: $region", color = Color.Gray, fontSize = 14.sp)
                        }
                        Text("$count loài", color = if (isDarkMode) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatBar(isDarkMode: Boolean, label: String, value: Int, maxValue: Int, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = if (isDarkMode) Color.White else Color.Black, fontSize = 14.sp)
            Text("$value", color = Color.Gray, fontSize = 14.sp)
        }
        LinearProgressIndicator(progress = { value.toFloat() / maxValue }, modifier = Modifier.fillMaxWidth().height(10.dp), color = color, trackColor = Color.Gray.copy(alpha = 0.2f))
    }
}

fun getTypeColor(typeName: String): Color = when (typeName) {
    "Cỏ" -> Color(0xFF78C850); "Độc" -> Color(0xFFA040A0)
    "Lửa" -> Color(0xFFF08030); "Nước" -> Color(0xFF6890F0)
    "Điện" -> Color(0xFFF8D030); "Siêu Linh" -> Color(0xFFF85888)
    "Băng" -> Color(0xFF98D8D8); "Rồng" -> Color(0xFF7038F8)
    "Bóng Tối" -> Color(0xFF705848); "Tiên" -> Color(0xFFEE99AC)
    "Thường" -> Color(0xFFA8A878); "Giác Đấu" -> Color(0xFFC03028)
    "Bay" -> Color(0xFFA890F0); "Đất" -> Color(0xFFE0C068)
    "Đá" -> Color(0xFFB8A038); "Côn Trùng" -> Color(0xFFA8B820)
    "Ma" -> Color(0xFF705898); "Thép" -> Color(0xFFB8B8D0)
    else -> Color.Cyan
}