package com.gaur.himanshu.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import java.security.Principal

class JWTConfig(jwtSecret: String) {

    companion object {
        // jwt config
        const val jwtIssuer = "com.gaur"
        const val jwtRealm = "com.gaur.himanshu"

        //jwt claims
        const val CLAIM_USERNAME = "username"
        const val USER_ID = "userId"
    }

    /** required variables  */
    private val algorithm = Algorithm.HMAC256(jwtSecret)
    private val verifier = JWT.require(algorithm).withIssuer(jwtIssuer).build()

    /** Generate token for existing user */
    fun generateToken(user: JwtUser): String =
        JWT.create().withSubject("Authentication").withIssuer(jwtIssuer).withClaim(
            CLAIM_USERNAME, user.username
        ).withClaim(USER_ID, user.id).sign(algorithm)

    /** configure the kotlin feature for authentication */
    fun configureKtorFeatures(config: JWTAuthenticationProvider.Configuration) = with(config) {
        verifier(verifier)
        realm = jwtRealm
        validate {
            val userId = it.payload.getClaim(USER_ID).asInt()
            val userName = it.payload.getClaim(CLAIM_USERNAME).asString()

            if (userId != null && userName != null) {
                JwtUser(username = userName, id = userId.toInt())
            } else {
                null
            }
        }
    }


    data class JwtUser(val username: String, val id: Int) : io.ktor.auth.Principal


}


