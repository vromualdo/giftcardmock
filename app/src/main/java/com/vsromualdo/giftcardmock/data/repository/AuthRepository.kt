package com.vsromualdo.giftcardmock.data.repository

class AuthRepository {

    private val mockUsers = mapOf(
        "demo1@giftcard.com" to "123456",
        "demo2@giftcard.com" to "123456",
        "demo3@giftcard.com" to "123456",
    )

    fun login(email: String, password: String): Boolean {
        val expectedPassword = mockUsers[email.trim().lowercase()] ?: return false
        return expectedPassword == password
    }
}
