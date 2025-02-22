package com.nicolaischirmer.proyectodosbd.model

data class Weapon(
    var id: String ?= null,
    val userId: String?,
    val name: String?,
    val description: String?,
    val type: String?,
    val damage: Int?
)
