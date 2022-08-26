package com.recurly.androidsdk.data.network.core

import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly

interface RecurlyTokenizationHandler {

    @Override
    fun onSuccess(token: String, type:String)

    @Override
    fun onError(error: ErrorRecurly)

}