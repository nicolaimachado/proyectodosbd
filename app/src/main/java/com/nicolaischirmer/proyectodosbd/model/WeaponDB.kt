package com.nicolaischirmer.proyectodosbd.model

data class WeaponDB(
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val damage: Int = 0
)
