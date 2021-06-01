package com.gaur.himanshu.authentication

/** authentication which is a mapOf two elements*/
class UserRepositoryImpl : UserInterface {

    private val authMaps = mapOf<String, UserInterface.User>(
        "admin:1" to UserInterface.User("admin", 1),
        "him:2" to UserInterface.User("him", 2)
    )

    override fun getUsername(username: String, password: String): UserInterface.User? {
        return authMaps["$username:$password"]
    }
}