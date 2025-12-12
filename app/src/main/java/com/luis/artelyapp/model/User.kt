package com.luis.artelyapp.model

/**
 * Modelo base de Usuario
 * Nota: La contraseña NO se guarda aquí por seguridad.
 * Firebase Authentication maneja las contraseñas de forma segura.
 *
 * Los valores por defecto son necesarios para que Firebase pueda deserializar
 * Usamos var en lugar de val para que Firebase pueda setear los valores
 *
 * ACTUALIZACIÓN: Cambiado id_User de Int a String para consistencia con Firebase
 */
open class User(
    open var id_User: String = "",
    open var UserName: String = "",
    open var Email: String = "",
    open var Role: String = ""
)
