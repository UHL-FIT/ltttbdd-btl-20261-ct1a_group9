package com.example.btln

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class PokemonViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RetrofitClient.service
    private val sharedPrefs = application.getSharedPreferences("pokedex_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // 1. Quản lý Chế độ tối (Lưu bền vững)
    private val _isDarkMode = MutableStateFlow(sharedPrefs.getBoolean("is_dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // 2. Danh sách Pokemon tổng
    private val _allPokemon = MutableStateFlow<List<PokemonResult>>(emptyList())
    val allPokemon: StateFlow<List<PokemonResult>> = _allPokemon

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 3. Pokemon đang chọn xem chi tiết
    private val _selectedPokemon = MutableStateFlow<PokemonResult?>(null)
    val selectedPokemon: StateFlow<PokemonResult?> = _selectedPokemon

    // 4. Đội hình (Lưu bền vững)
    val myTeam = mutableStateListOf<PokemonResult?>(null, null, null, null, null, null)

    // 5. Error Message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var _targetSlotIdx by mutableIntStateOf(0)
    val targetSlot: Int get() = _targetSlotIdx

    init {
        loadAllPokemon()
        loadTeamFromDisk()
    }

    private fun showMessage(message: String) {
        _errorMessage.value = message
        viewModelScope.launch {
            delay(3000)
            if (_errorMessage.value == message) _errorMessage.value = null
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun loadAllPokemon() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getPokemonList()
                _allPokemon.value = response.results.filter {
                    !it.name.contains("-mega") && !it.name.contains("-totem") &&
                            !it.name.contains("-cap") && !it.name.contains("-battle-bond")
                }
            } catch (e: UnknownHostException) {
                showMessage("⚠️ Không có kết nối mạng! Vui lòng kiểm tra WiFi/4G.")
            } catch (e: Exception) {
                showMessage("❌ Lỗi tải dữ liệu: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadTeamFromDisk() {
        val teamJson = sharedPrefs.getString("my_team_json", null)
        if (teamJson != null) {
            try {
                val type = object : TypeToken<List<PokemonResult?>>() {}.type
                val savedTeam: List<PokemonResult?> = gson.fromJson(teamJson, type)
                savedTeam.forEachIndexed { index, pokemon ->
                    if (index < 6) myTeam[index] = pokemon
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveTeamToDisk() {
        val teamJson = gson.toJson(myTeam.toList())
        sharedPrefs.edit().putString("my_team_json", teamJson).apply()
    }

    fun setSelectedPokemon(pokemon: PokemonResult?) {
        _selectedPokemon.value = pokemon
    }

    fun setTargetSlot(slot: Int) {
        _targetSlotIdx = slot
    }

    fun addToTeam(pokemon: PokemonResult, slot: Int) {
        // Kiểm tra đội hình đã đầy chưa
        val isTeamFull = myTeam.all { it != null }
        if (isTeamFull && slot == -1) {
            showMessage("⚠️ Đội hình đã đầy (6/6)! Vui lòng xóa một Pokémon trước khi thêm mới.")
            return
        }

        // Kiểm tra Pokémon đã có trong đội hình chưa
        val alreadyInTeam = myTeam.any { it?.name == pokemon.name }
        if (alreadyInTeam) {
            showMessage("⚠️ ${pokemon.name.replaceFirstChar { it.uppercase() }} đã có trong đội hình!")
            return
        }

        if (slot in 0..5) {
            myTeam[slot] = pokemon
            saveTeamToDisk()
            showMessage("✅ Đã thêm ${pokemon.name.replaceFirstChar { it.uppercase() }} vào ô ${slot + 1}")
        }
    }

    fun removeFromTeam(slot: Int) {
        if (slot in 0..5) {
            val pokemonName = myTeam[slot]?.name?.replaceFirstChar { it.uppercase() }
            myTeam[slot] = null
            saveTeamToDisk()
            if (pokemonName != null) {
                showMessage("🗑️ Đã xóa $pokemonName khỏi đội hình")
            }
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
        sharedPrefs.edit().putBoolean("is_dark_mode", isDark).apply()
    }

    fun exportTeamToJson(): String {
        return gson.toJson(myTeam.toList())
    }

    fun importTeamFromJson(json: String): Boolean {
        return try {
            val type = object : TypeToken<List<PokemonResult?>>() {}.type
            val imported: List<PokemonResult?> = gson.fromJson(json, type)
            imported.forEachIndexed { index, pokemon ->
                if (index < 6) myTeam[index] = pokemon
            }
            saveTeamToDisk()
            showMessage("✅ Đã nhập đội hình thành công!")
            true
        } catch (e: Exception) {
            showMessage("❌ Lỗi nhập dữ liệu: JSON không hợp lệ")
            false
        }
    }
}