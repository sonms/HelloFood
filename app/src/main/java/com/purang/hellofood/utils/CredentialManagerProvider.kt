package com.purang.hellofood.utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

object CredentialManagerProvider {
    private lateinit var credentialManager: CredentialManager

    fun getCredentialRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("web_client_id")
            .setAutoSelectEnabled(true)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    fun getCredentialManager(context: Context): CredentialManager {
        credentialManager = CredentialManager.create(context)
        if (!this::credentialManager.isInitialized) {
            throw IllegalStateException("CredentialManager must be initialized first.")
        }
        return credentialManager
    }
}