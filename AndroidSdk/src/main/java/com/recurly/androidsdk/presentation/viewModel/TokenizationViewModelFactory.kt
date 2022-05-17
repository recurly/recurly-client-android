package com.recurly.androidsdk.presentation.viewModel

import com.recurly.androidsdk.data.TokenRepository
import com.recurly.androidsdk.data.network.TokenService
import com.recurly.androidsdk.domain.GetRecurlyToken

internal class TokenizationViewModelFactory {

    /**
     * This fun returns a View Model with all the dependency injection realized
     */
    fun createTokenizationViewModel(): TokenizationViewModel{
        val apiClient = TokenService()
        val repository = TokenRepository(apiClient)
        val getRecurlyToken = GetRecurlyToken(repository)
        return TokenizationViewModel(getRecurlyToken)
    }

}