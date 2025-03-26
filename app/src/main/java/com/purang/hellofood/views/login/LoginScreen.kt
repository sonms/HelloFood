package com.purang.hellofood.views.login

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.purang.hellofood.utils.CredentialManagerProvider
import com.purang.hellofood.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

sealed class ApiState {
    data object Idle : ApiState()
    data object Loading : ApiState()
    data class Success(val message: String) : ApiState()
    data class Error(val message: String) : ApiState()
}

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    //val loginState by viewModel.loginApiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val credentialManager = CredentialManagerProvider.getCredentialManager(context)
    val request = CredentialManagerProvider.getCredentialRequest()
    Button(onClick = {
        coroutineScope.launch {
            /*try {

            } catch (e : ) {
                handleFailure()
            }*/
            val result = credentialManager.getCredential(
                context, request
            )
            handleSignIn(result, navController)
        }
    }) {
        Text("Google로 로그인")
    }


    /*when (loginState) {
        is ApiState.Loading -> Text("로그인 중...")
        is ApiState.Success -> Text("로그인 성공!")
        is ApiState.Error -> Text("로그인 실패: ${(viewModel.loginApiState as ApiState.Error).message}")
        else -> {}
    }*/
}


private fun handleSignIn(result : GetCredentialResponse, navController: NavController) {
    when (val credential = result.credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                //여기에 이제 파이어베이스건 로그인이건 네비게이션 이동 및 토큰인계
            }
        }
    }
}