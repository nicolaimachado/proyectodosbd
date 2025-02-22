package com.nicolaischirmer.proyectodosbd.ui.screen

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nicolaischirmer.proyectodosbd.data.AuthManager
import com.nicolaischirmer.proyectodosbd.data.FirestoreManager
import com.nicolaischirmer.proyectodosbd.model.Weapon

@Composable
fun AddWeaponDialog(
    onWeaponAdded: (Weapon) -> Unit,
    onDialogDismissed: () -> Unit,
    auth: AuthManager
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var damage by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    AlertDialog(
        title = { Text("Add Weapon") },
        onDismissRequest = { onDialogDismissed() },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && description.isNotEmpty() && type.isNotEmpty() && damage > 0) {
                        val newWeapon = Weapon(
                            userId = auth.getCurrentUser()?.uid,
                            name = name,
                            description = description,
                            type = type,
                            damage = damage
                        )
                        try {
                            onWeaponAdded(newWeapon)
                            Toast.makeText(context, "Weapon added successfully", Toast.LENGTH_SHORT).show()
                            name = ""
                            description = ""
                            type = ""
                            damage = 0
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error adding weapon: ${e.message}", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                    }
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
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = damage.toString(),
                    onValueChange = { damage = it.toIntOrNull() ?: 0 },
                    label = { Text("Damage") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        }
    )
}
