package com.gaur.himanshu.authentication

/** Repository skeleton and also used for testing*/
interface UserInterface {
    fun getUsername(username: String, password: String): User?
    data class User(val username: String, val userId: Int)
}