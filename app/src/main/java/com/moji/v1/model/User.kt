package com.moji.v1.model

data class User(
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String
)