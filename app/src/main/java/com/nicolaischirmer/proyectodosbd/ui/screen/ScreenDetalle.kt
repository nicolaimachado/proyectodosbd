package com.nicolaischirmer.proyectodosbd.ui.screen

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
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
import com.nicolaischirmer.proyectodosbd.model.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenDetalle(
    weaponId: String,
    auth: AuthManager,
    firestore: FirestoreManager,
    navigateToLogin: () -> Unit,
) {
    val user = auth.getCurrentUser()
    val factoryInicio = InicioViewModelFactory(firestore)
    val inicioViewModel = viewModel(InicioViewModel::class.java, factory = factoryInicio)

    val factoryDetalle = DetalleViewModelFactory(firestore, weaponId)
    val detalleViewModel = viewModel(DetalleViewModel::class.java, factory = factoryDetalle)

    val weapon by inicioViewModel.weapon.collectAsState()
    val uiStateInicio by inicioViewModel.uiState.collectAsState()
    val uiStateDetalle by detalleViewModel.uiState.collectAsState()

    LaunchedEffect(weaponId) {
        inicioViewModel.getWeaponById(weaponId)
    }

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
                onClick = { detalleViewModel.onAddCharacterSelected() },
                containerColor = Color.Gray
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add character")
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Characters who use  ${weapon?.name}",  style = TextStyle(fontSize = 24.sp))
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (uiStateInicio.showLogoutDialog) {
                LogoutDialog(
                    onDismiss = { inicioViewModel.dismisShowLogoutDialog() },
                    onConfirm = {
                        auth.signOut()
                        navigateToLogin()
                        inicioViewModel.dismisShowLogoutDialog()
                    }
                )
            }

            if (uiStateDetalle.showAddCharacterDialog) {
                AddCharacterDialog(
                    onCharacterAdded = { character ->
                        detalleViewModel.addCharacter(
                            Character(
                                id = "",
                                weaponId = weapon?.id,
                                userId = auth.getCurrentUser()?.uid,
                                character.name ?: "",
                                character.archetype ?: "",
                                character.ability ?: ""
                            )
                        )
                        detalleViewModel.dismisShowAddCharacterDialog()
                    },
                    onDialogDismissed = { detalleViewModel.dismisShowAddCharacterDialog() },
                    auth
                )
            }

            if (!uiStateDetalle.characters.isNullOrEmpty()) {


                LazyColumn(
                    modifier = Modifier.padding(top = 60.dp)
                ) {
                    items(uiStateDetalle.characters) { character ->
                        CharacterItem(
                            character = character,
                            deleteCharacter = {
                                detalleViewModel.deleteCharacterById(
                                    character.id ?: ""
                                )
                            },
                            updateCharacter = {
                                detalleViewModel.updateCharacter(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data available")
                }
            }
        }
    }
}

@Composable
fun CharacterItem(
    character: Character,
    deleteCharacter:  () -> Unit,
    updateCharacter: (Character) -> Unit,
) {

    var showDeleteCharacterDialog by remember { mutableStateOf(false) }
    var showUpdateCharacterDialog by remember { mutableStateOf(false) }

    if (showDeleteCharacterDialog) {
        DeleteCharacterDialog(
            onConfirmDelete = {
                deleteCharacter()
                showDeleteCharacterDialog = false
            },
            onDismiss = { showDeleteCharacterDialog = false }
        )
    }

    if (showUpdateCharacterDialog) {
        UpdateCharacterDialog(
            character = character,
            onCharacterUpdated = { character ->
                updateCharacter(character)
                showUpdateCharacterDialog = false
            },
            onDialogDismissed = { showUpdateCharacterDialog = false }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)


    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(text = "Character", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Name: ${character.name}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Archetype: ${character.archetype}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Ability: ${character.ability}",
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
                onClick = { showUpdateCharacterDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Update Character"
                )
            }
            IconButton(
                onClick = { showDeleteCharacterDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Character"
                )
            }
        }
    }
}