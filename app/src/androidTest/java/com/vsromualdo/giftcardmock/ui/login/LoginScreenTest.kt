package com.vsromualdo.giftcardmock.ui.login

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.vsromualdo.giftcardmock.data.repository.AuthRepository
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginButton_disabledWhenFieldsAreEmpty() {
        val viewModel = LoginViewModel(AuthRepository())

        composeTestRule.setContent {
            LoginScreen(viewModel = viewModel, onLoginSuccess = {})
        }

        composeTestRule.onNodeWithText("Entrar").assertIsNotEnabled()
    }

    @Test
    fun invalidCredentials_showErrorAndDoNotNavigate() {
        val viewModel = LoginViewModel(AuthRepository())
        var loggedIn = false

        composeTestRule.setContent {
            LoginScreen(viewModel = viewModel, onLoginSuccess = { loggedIn = true })
        }

        composeTestRule.onNodeWithText("E-mail").performTextInput("wrong@giftcard.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("wrong-password")
        composeTestRule.onNodeWithText("Entrar").performClick()

        composeTestRule.onNodeWithText("E-mail ou senha inválidos.").assertExists()
        assert(!loggedIn)
    }
}
