package com.gaur.himanshu


import com.gaur.himanshu.authentication.JWTConfig
import com.gaur.himanshu.authentication.LoginBody
import com.gaur.himanshu.authentication.UserRepositoryImpl
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.lang.IllegalArgumentException
import java.time.Instant


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {


    val list = mutableListOf<Todo>()
    val jwtSecret = JWTConfig("jlkajfljlajgrwetihwegbsbvlnalkjdfglaijsfgiowag")
    val userRepositoryImpl = UserRepositoryImpl()
    list.add(Todo(name = "Learn Ktor", done = false, id = 1))
    list.add(Todo(name = "Jetpack Compose", done = false, id = 2))
    /** Runs on PORT 8080*/
    embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
            /** Install Authentication feature*/
            install(Authentication) {
                jwt {
                    jwtSecret.configureKtorFeatures(this)
                }
            }
        }
        routing {
            /** Welocome to our TODOLIST*/
            get("/") {
                call.respond("Welocme to TODO list App")
            }
            /** Login for the Authenticated user*/
            post("/login") {
                val loginBody = call.receive<LoginBody>()
                val user = userRepositoryImpl.getUsername(username = loginBody.username, password = loginBody.password)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid user")
                    return@post
                }
                val token = jwtSecret.generateToken(JWTConfig.JwtUser(username = user.username, id = user.userId))
                call.respond(token)
            }

            /** get all todos*/
            get("/all_todos") {
                call.respond(list)
            }
            /** post todos*/
            post("/post_todo") {
                val todo = call.receive<Todo>()
                list.add(todo)
                call.respond("Successfully Added your Todo")
            }
            /** delete todos*/
            delete("/todo/{id}") {
                val index = call.parameters["id"]?.toInt()
                index?.let {
                    list.removeAt(index - 1)
                }
                call.respond("Removed Successfully")
            }
            /** update todos*/
            put("/update_todo") {
                val todo = call.receive<Todo>()
                list.add(todo.id.minus(1), todo)
                call.respond("Successfully updated the todo")
            }

        }
    }.start(true)
}


data class Todo(
    val name: String,
    val done: Boolean,
    val id: Int
)
