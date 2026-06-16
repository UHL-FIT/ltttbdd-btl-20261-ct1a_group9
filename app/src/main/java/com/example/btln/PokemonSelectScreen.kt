package com.example.btln

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import coil.compose.AsyncImage

@Composable
fun PokemonSelectScreen(
    pokemonList: List<PokemonResult>,
    isDarkMode: Boolean,
    onSelect: (PokemonResult) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val bgColor = if (isDarkMode) Color(0xFF0A1428) else Color(0xFFF5F5F5)
    val cardColor = if (isDarkMode) Color(0xFF152642) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    val filteredList = pokemonList.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
    ) {
        // THANH TÌM KIẾM (Y hệt trong ảnh)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Tìm kiếm Pokémon", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // DANH SÁCH HÀNG DỌC (Y hệt trong ảnh)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { pokemon ->
                val id = pokemon.url.split("/").filter { it.isNotEmpty() }.last()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 1. Ảnh Pokémon nhỏ bên trái
                            AsyncImage(
                                model = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
                                modifier = Modifier.size(50.dp),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            // 2. Tên Pokémon ở giữa
                            Text(
                                text = pokemon.name.uppercase(),
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        // 3. Nút THÊM màu vàng bên phải
                        Button(
                            onClick = { onSelect(pokemon) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = "THÊM",
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}