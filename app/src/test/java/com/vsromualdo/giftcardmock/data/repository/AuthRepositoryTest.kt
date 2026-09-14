package com.vsromualdo.giftcardmock.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryTest {

    @Test
    fun `login succeeds for any mock demo user with the shared password`() {
        val repository = AuthRepository()

        assertTrue(repository.login("demo1@giftcard.com", "123456"))
        assertTrue(repository.login("demo2@giftcard.com", "123456"))
        assertTrue(repository.login("demo3@giftcard.com", "123456"))
    }

    @Test
    fun `login fails for unknown email`() {
        val repository = AuthRepository()

        assertFalse(repository.login("someone@else.com", "123456"))
    }

    @Test
    fun `login fails for wrong password`() {
        val repository = AuthRepository()

        assertFalse(repository.login("demo1@giftcard.com", "wrong-password"))
    }

    @Test
    fun `login fails for empty credentials`() {
        val repository = AuthRepository()

        assertFalse(repository.login("", ""))
    }
}
