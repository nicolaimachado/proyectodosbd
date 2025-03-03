package com.nicolaischirmer.proyectodosbd.data

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.nicolaischirmer.proyectodosbd.model.Character
import com.nicolaischirmer.proyectodosbd.model.CharacterDB
import com.nicolaischirmer.proyectodosbd.model.Weapon
import com.nicolaischirmer.proyectodosbd.model.WeaponDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.retry
import java.io.IOException


class FirestoreManager(auth: AuthManager, context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = auth.getCurrentUser()?.uid

    companion object {
        const val WEAPON_COLLECTION = "Weapons"
        const val CHARACTER_COLLECTION = "Characters"
    }


    fun getWeapons(): Flow<List<Weapon>> {
        return firestore.collection(WEAPON_COLLECTION)
            .snapshots()
            .retry(3) { e ->
                if (e is IOException || e is FirebaseFirestoreException) {
                    delay(1000) // Delay before retrying
                    true // Retry
                } else {
                    false // Do not retry
                }
            }
            .map { qs ->
                qs.documents.mapNotNull { ds ->
                    try {
                        ds.toObject(WeaponDB::class.java)?.let { weaponDB ->
                            val weapon = Weapon(
                                id = ds.id,
                                userId = weaponDB.userId,
                                name = weaponDB.name,
                                description = weaponDB.description,
                                type = weaponDB.type,
                                damage = weaponDB.damage
                            )
                            Log.d("FirestoreManager", "Weapon name: ${weapon.name}")
                            weapon
                        }
                    } catch (e: Exception) {
                        Log.e("FirestoreManager", "Error converting document to Weapon: ${e.message}")
                        null
                    }
                }
            }
    }

    suspend fun addWeapon(weapon: Weapon) {
        val db = FirebaseFirestore.getInstance()
        val weaponRef = db.collection(WEAPON_COLLECTION)

        val documentReference = weaponRef.add(weapon).await()
        val weaponId = documentReference.id

        documentReference.update("id", weaponId).await()
    }

    suspend fun updateWeapon(weapon: Weapon) {
        val weaponRef = weapon.id?.let {
            firestore.collection("Weapons").document(it)
        }
        weaponRef?.set(weapon)?.await()
    }

    suspend fun deleteWeaponById(weaponId: String) {
        firestore.collection(WEAPON_COLLECTION).document(weaponId).delete().await()
    }

    suspend fun getWeaponById(weaponId: String): Weapon? {
        return firestore.collection(WEAPON_COLLECTION).document(weaponId)
            .get().await().toObject(WeaponDB::class.java)?.let { weaponDB ->
            Weapon(
                id = weaponId,
                userId = weaponDB.userId,
                name = weaponDB.name,
                description = weaponDB.description,
                type = weaponDB.type,
                damage = weaponDB.damage
            )
        }
    }

    suspend fun getCharacterByWeaponId(weaponId: String): Flow<List<Character>>{
        return firestore.collection(CHARACTER_COLLECTION)
            .whereEqualTo("weaponId", weaponId)
            .snapshots()
            .map { qs ->
                qs.documents.mapNotNull { ds ->
                    ds.toObject(CharacterDB::class.java)?.let { characterDB ->
                        Character(
                            id = ds.id,
                            weaponId = characterDB.weaponId,
                            userId = characterDB.userId,
                            name = characterDB.name,
                            archetype = characterDB.archetype,
                            ability = characterDB.ability
                        )
                    }
                }
            }
    }

    suspend fun addCharacter(character: Character) {
        val db = FirebaseFirestore.getInstance()
        val characterRef = db.collection(CHARACTER_COLLECTION)

        val documentReference = characterRef.add(character).await()
        val charId = documentReference.id

        documentReference.update("id", charId).await()
    }

    suspend fun updateCharacter(character: Character) {
        val characterRef = character.id?.let {
            firestore.collection(CHARACTER_COLLECTION).document(it)
        }
        characterRef?.set(character)?.await()
    }

    suspend fun deleteCharacterById(characterId: String) {
        firestore.collection(CHARACTER_COLLECTION).document(characterId).delete().await()
    }

    suspend fun checkFirestoreConnection(): Boolean {
        return try {
            firestore.collection("test").document("testDoc").get().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}