package com.nicolaischirmer.proyectodosbd.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nicolaischirmer.proyectodosbd.data.FirestoreManager
import com.nicolaischirmer.proyectodosbd.model.Weapon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class InicioViewModel(val firestoreManager: FirestoreManager) : ViewModel() {

    val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _weapon = MutableStateFlow<Weapon?>(null)
    val weapon: StateFlow<Weapon?> = _weapon

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            firestoreManager.getWeapons().collect { weapons ->
                _uiState.update { uiState ->
                    uiState.copy(
                        weapons = weapons,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun addWeapon(weapon: Weapon) {
        viewModelScope.launch {
            firestoreManager.addWeapon(weapon)
        }
    }

    fun deleteWeaponById(weaponId: String) {
        if (weaponId.isEmpty()) return
        viewModelScope.launch {
            firestoreManager.deleteWeaponById(weaponId)
        }
    }

    fun updateWeapon(weaponNew: Weapon) {
        viewModelScope.launch {
            firestoreManager.updateWeapon(weaponNew)
        }
    }

    fun getWeaponById(weaponId: String) {
        viewModelScope.launch {
            _weapon.value = firestoreManager.getWeaponById(weaponId)
        }
    }

    fun onAddWeaponSelected() {
        _uiState.update { it.copy(showAddWeaponDialog = true) }
    }

    fun dismisShowAddWeaponDialog() {
        _uiState.update { it.copy(showAddWeaponDialog = false) }
    }

    fun onLogoutSelected() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun dismisShowLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }
}

data class UiState(
    val weapons: List<Weapon> = emptyList(),
    val isLoading: Boolean = false,
    val showAddWeaponDialog: Boolean = false,
    val showLogoutDialog: Boolean = false
)

class InicioViewModelFactory(private val firestoreManager: FirestoreManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InicioViewModel(firestoreManager) as T
    }
}