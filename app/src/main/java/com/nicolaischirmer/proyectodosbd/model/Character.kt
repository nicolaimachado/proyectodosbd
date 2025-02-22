package com.nicolaischirmer.proyectodosbd.model

data class Character(
    val id: String ?= null,
    val weaponId: String?,
    val userId: String?,
    val name: String?,
    val archetype: String?,
    val ability: String?,
)
