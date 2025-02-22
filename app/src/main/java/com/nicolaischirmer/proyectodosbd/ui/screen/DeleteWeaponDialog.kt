package com.nicolaischirmer.proyectodosbd.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DeleteWeaponDialog(onConfirmDelete: () -> Unit, onDismiss: () -> Unit){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Weapon") },
        text = { Text("¿Are you sure you want to delete this weapon?") },
        confirmButton = {
            Button(
                onClick = onConfirmDelete
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("No")
            }
        }
    )
}