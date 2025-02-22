package com.nicolaischirmer.proyectodosbd.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nicolaischirmer.proyectodosbd.data.FirestoreManager
import com.nicolaischirmer.proyectodosbd.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class DetalleViewModel(val firestoreManager: FirestoreManager, val weaponId: String) : ViewModel() {

    val _uiState = MutableStateFlow(UiStateDetalle())
    val uiState: StateFlow<UiStateDetalle> = _uiState

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            firestoreManager.getCharacterByWeaponId(weaponId).collect { characters ->
                _uiState.update { uiState ->
                    uiState.copy(
                        characters = characters,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun addCharacter(character: Character) {
        viewModelScope.launch {
            firestoreManager.addCharacter(character)
        }
    }

    fun updateCharacter(character: Character) {
        viewModelScope.launch {
            firestoreManager.updateCharacter(character)
        }
    }

    fun deleteCharacterById(characterId: String) {
        if (characterId.isEmpty()) return
        viewModelScope.launch {
            firestoreManager.deleteCharacterById(characterId)
        }
    }

    fun onAddCharacterSelected() {
        _uiState.update { it.copy(showAddCharacterDialog = true) }
    }

    fun dismisShowAddCharacterDialog() {
        _uiState.update { it.copy(showAddCharacterDialog = false) }
    }
}

data class UiStateDetalle(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val showAddCharacterDialog: Boolean = false,
    val showLogoutDialog: Boolean = false
)

class DetalleViewModelFactory(private val firestoreManager: FirestoreManager, private val weaponId: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetalleViewModel(firestoreManager, weaponId) as T
    }
}