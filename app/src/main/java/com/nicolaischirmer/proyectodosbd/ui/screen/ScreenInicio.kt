package com.nicolaischirmer.proyectodosbd.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nicolaischirmer.proyectodosbd.R
import com.nicolaischirmer.proyectodosbd.data.AuthManager
import com.nicolaischirmer.proyectodosbd.data.FirestoreManager
import com.nicolaischirmer.proyectodosbd.model.Weapon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenInicio(
    auth: AuthManager,
    firestore: FirestoreManager,
    navigateToLogin: () -> Unit,
    navigateToDetalle: (String) -> Unit
) {
    val user = auth.getCurrentUser()
    val factory = InicioViewModelFactory(firestore)
    val inicioViewModel = viewModel(InicioViewModel::class.java, factory = factory)
    val uiState by inicioViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (user?.photoUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user?.photoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(40.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.ic_usuario),
                                contentDescription = "Default profile image",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )

                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = user?.displayName ?: "Anonymous",
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = user?.email ?: "No email",
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(
                        ContextCompat.getColor(
                            LocalContext.current,
                            R.color.white
                        )
                    )
                ),
                actions = {
                    IconButton(onClick = {
                        inicioViewModel.onLogoutSelected()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = "Log out"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { inicioViewModel.onAddWeaponSelected() },
                containerColor = Color.Gray
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add weapon")
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Weapon list",  style = TextStyle(fontSize = 24.sp))
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.showLogoutDialog) {
                LogoutDialog(
                    onDismiss = { inicioViewModel.dismisShowLogoutDialog() },
                    onConfirm = {
                        auth.signOut()
                        navigateToLogin()
                        inicioViewModel.dismisShowLogoutDialog()
                    }
                )
            }

            if (uiState.showAddWeaponDialog) {
                AddWeaponDialog(
                    onWeaponAdded = { weapon ->
                        inicioViewModel.addWeapon(
                            Weapon(
                                id = "",
                                userId = auth.getCurrentUser()?.uid,
                                weapon.name ?: "",
                                weapon.description ?: "",
                                weapon.type ?: "",
                                weapon.damage ?: 0
                            )

                        )
                        inicioViewModel.dismisShowAddWeaponDialog()
                    },
                    onDialogDismissed = { inicioViewModel.dismisShowAddWeaponDialog() },
                    auth
                )
            }

            if (!uiState.weapons.isNullOrEmpty()) {

                LazyColumn(
                    modifier = Modifier.padding(top = 40.dp)
                ) {
                    items(uiState.weapons) { weapon ->
                        WeaponItem(
                            weapon = weapon,
                            deleteWeapon = {
                                inicioViewModel.deleteWeaponById(
                                    weapon.id ?: ""
                                )
                            },
                            updateWeapon = {
                                inicioViewModel.updateWeapon(it)
                            },
                            navigateToDetalle = { weapon.id?.let { it1 -> navigateToDetalle(it1) } }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data")
                }
            }
        }
    }
}


@Composable
fun WeaponItem(
    weapon: Weapon,
    deleteWeapon: () -> Unit,
    updateWeapon: (Weapon) -> Unit,
    navigateToDetalle: (String) -> Unit
){
    var showDeleteWeaponDialog by remember { mutableStateOf(false) }
    var showUpdateWeaponDialog by remember { mutableStateOf(false) }

    if (showDeleteWeaponDialog) {
        DeleteWeaponDialog(
            onDismiss = { showDeleteWeaponDialog = false },
            onConfirmDelete = {
                deleteWeapon()
                showDeleteWeaponDialog = false
            }
        )
    }

    if (showUpdateWeaponDialog) {
        UpdateWeaponDialog(
            weapon = weapon,
            onWeaponUpdated = { weapon ->
                updateWeapon(weapon)
                showUpdateWeaponDialog = false
            },
            onDialogDismissed = { showUpdateWeaponDialog = false }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { weapon.id?.let { navigateToDetalle(it) } },
        elevation = CardDefaults.cardElevation(4.dp)


    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(text = weapon.name ?: "", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Description: ${weapon.description}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Type: ${weapon.type}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Damage: ${weapon.damage}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(AbsoluteAlignment.Right)
        ) {
            IconButton(
                onClick = { showUpdateWeaponDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Update Weapon"
                )
            }
            IconButton(
                onClick = { showDeleteWeaponDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Weapon"
                )
            }
        }
    }
}




@Composable
fun LogoutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log out") },
        text = {
            Text("Are you sure you want to log out?")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}