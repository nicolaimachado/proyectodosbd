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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nicolaischirmer.proyectodosbd.model.Character


@Composable
fun UpdateCharacterDialog(
    character: Character,
    onCharacterUpdated: (Character) -> Unit,
    onDialogDismissed: () -> Unit
) {
    var name by remember { mutableStateOf(character.name) }
    var archetype by remember { mutableStateOf(character.archetype) }
    var ability by remember { mutableStateOf(character.ability) }

    AlertDialog(
        title = { Text(text = "Update character") },
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = {
                    val newCharacter = Character(
                        id = character.id,
                        weaponId = character.weaponId,
                        userId = character.userId,
                        name = name,
                        archetype = archetype,
                        ability = ability
                    )
                    onCharacterUpdated(newCharacter)
                    name = ""
                    archetype = ""
                    ability = ""
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
                    label = { Text("Name") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = archetype ?: "",
                    onValueChange = { archetype = it },
                    label = { Text("Archetype") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = ability ?: "",
                    onValueChange = { ability = it },
                    label = { Text("Ability") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
            }
        }
    )
}