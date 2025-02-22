package com.nicolaischirmer.proyectodosbd.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nicolaischirmer.proyectodosbd.model.Weapon

@Composable
fun UpdateWeaponDialog(
    weapon: Weapon,
    onWeaponUpdated: (Weapon) -> Unit,
    onDialogDismissed: () -> Unit
) {
    var name by remember { mutableStateOf(weapon.name) }
    var description by remember { mutableStateOf(weapon.description) }
    var type by remember { mutableStateOf(weapon.type) }
    var damage by remember { mutableIntStateOf(weapon.damage!!) }

    AlertDialog(
        title = { Text(text = "Update weapon") },
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = {
                    val newWeapon = Weapon(
                        id = weapon.id,
                        userId = weapon.userId,
                        name = name,
                        description = description,
                        type = type,
                        damage = damage
                    )
                    onWeaponUpdated(newWeapon)
                    name = ""
                    description = ""
                    type = ""
                    damage = 0
                }
            ) {
                Text(text = "Update")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDialogDismissed() }
            ) {
                Text(text = "Cancel")
            }
        },
        text = {
            Column() {
                TextField(
                    value = name ?: "",
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = description ?: "",
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = type ?: "",
                    onValueChange = { type = it },
                    label = { Text("Type") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = damage.toString(),
                    onValueChange = { damage = it.toIntOrNull() ?: 0 },
                    label = { Text("Damage") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType =
                        KeyboardType.Number
                    )
                )
            }
        }
    )
}