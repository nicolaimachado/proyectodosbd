package com.nicolaischirmer.proyectodosbd.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object login

@Serializable
object signUp

@Serializable
object forgotPassword

@Serializable
object screenInicio

@Serializable
data class screenDetalle(val id: String)

