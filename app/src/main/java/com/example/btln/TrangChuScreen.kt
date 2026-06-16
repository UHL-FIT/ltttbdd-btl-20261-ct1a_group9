package com.example.btln

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrangChuScreen(
    allPokemon: List<PokemonResult>,
    isLoading: Boolean,
    isDarkMode: Boolean,
    onMenuClick: () -> Unit,
    onPokemonClick: (PokemonResult) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchError by remember { mutableStateOf<String?>(null) } // THÊM validation

    val bgColor = if (isDarkMode) Color(0xFF0A1428) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else Color.Black
    val cardColor = if (isDarkMode) Color(0xFF152642) else Color.White

    val filteredList = allPokemon.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Từ Điển Pokémon", fontWeight = FontWeight.Bold, color = textColor) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    // VALIDATION: Kiểm tra độ dài tìm kiếm
                    if (it.isNotEmpty() && it.length < 2) {
                        searchError = "Nhập ít nhất 2 ký tự để tìm kiếm"
                    } else {
                        searchError = null
                    }
                },
                label = { Text("Tìm kiếm Pokémon", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                isError = searchError != null, // THÊM
                supportingText = { // THÊM
                    if (searchError != null) {
                        Text(searchError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    errorContainerColor = cardColor,
                    errorTextColor = Color.Red
                ),
                shape = RoundedCornerShape(12.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4FC3F7))
                }
            } else if (filteredList.isEmpty() && searchQuery.isNotEmpty()) {
                // THÊM: Hiển thị khi không tìm thấy kết quả
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Không tìm thấy Pokémon nào", color = Color.Gray, fontSize = 16.sp)
                        Text("Vui lòng thử từ khóa khác", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
                ) {
                    items(filteredList) { pokemon ->
                        val id = pokemon.url.split("/").filter { it.isNotEmpty() }.last()
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .height(160.dp)
                                .clickable { onPokemonClick(pokemon) },
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                AsyncImage(
                                    model = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
                                    modifier = Modifier.size(85.dp),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}