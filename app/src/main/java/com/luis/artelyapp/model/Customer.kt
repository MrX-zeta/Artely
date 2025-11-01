package com.luis.artelyapp.model

data class Customer(
    override val id_User:Int,
    override val Email:String,
    override val Psswd:String,
    override val Role:String

) : User(id_User, Email, Psswd, Role)
