package com.nicolaischirmer.proyectodosbd.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.nicolaischirmer.proyectodosbd.data.AuthManager
import com.nicolaischirmer.proyectodosbd.model.Character

@Composable
fun AddCharacterDialog(
    onCharacterAdded: (Character) -> Unit,
    onDialogDismissed: () -> Unit,
    auth: AuthManager,
) {

    var weaponId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var archetype by remember { mutableStateOf("") }
    var ability by remember { mutableStateOf("") }

    AlertDialog(
        title = { Text("Add character") },
        onDismissRequest = { onDialogDismissed() },
        confirmButton = {
            Button(
                onClick = {
                    val newCharacter = Character(
                        weaponId = weaponId,
                        userId = auth.getCurrentUser()?.uid,
                        name = name,
                        archetype = archetype,
                        ability = ability
                    )
                    onCharacterAdded(newCharacter)
                    weaponId = ""
                    name = ""
                    archetype = ""
                    ability = ""
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDialogDismissed() }
            ) {
                Text("Cancel")
            }
        },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = archetype,
                    onValueChange = { archetype = it },
                    label = { Text("Archetype") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = ability,
                    onValueChange = { ability = it },
                    label = { Text("Ability") }
                )
            }


        }

    )

}
