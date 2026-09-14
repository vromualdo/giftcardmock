package com.vsromualdo.giftcardmock.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vsromualdo.giftcardmock.analytics.aws.sendAwsEvent
import com.vsromualdo.giftcardmock.analytics.trackLoginFailed
import com.vsromualdo.giftcardmock.analytics.trackLoginSuccess
import com.vsromualdo.giftcardmock.data.repository.AuthRepository

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val canSubmit: Boolean
        get() = email.isNotBlank() && password.isNotBlank()

    fun onEmailChange(value: String) {
        email = value
        errorMessage = null
    }

    fun onPasswordChange(value: String) {
        password = value
        errorMessage = null
    }

    fun login(): Boolean {
        val success = authRepository.login(email, password)
        if (success) {
            trackLoginSuccess()
            sendAwsEvent(
                eventCategory = "TODO_CATEGORY_LOGIN",
                eventAction = "TODO_ACTION_LOGIN_SUCCESS",
                eventLabel = "Login com sucesso",
            )
        } else {
            trackLoginFailed()
            sendAwsEvent(
                eventCategory = "TODO_CATEGORY_LOGIN",
                eventAction = "TODO_ACTION_LOGIN_FAILED",
                eventLabel = "Login inválido",
            )
        }
        errorMessage = if (success) null else "E-mail ou senha inválidos."
        return success
    }

    companion object {
        fun factory(authRepository: AuthRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { LoginViewModel(authRepository) }
        }
    }
}
