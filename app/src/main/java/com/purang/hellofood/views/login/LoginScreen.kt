package com.purang.hellofood.views.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.common.SignInButton
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.purang.hellofood.BottomNavItem
import com.purang.hellofood.BottomNavigation
import com.purang.hellofood.R
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

    val painter = rememberAsyncImagePainter(
        model = "R.drawable.android_light_sq_SU.svg" // SVG 파일 경로
    )

    Button(
        onClick = {
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
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Image(
            painter = painter,//painterResource(id = R.drawable.ic_logo_google),
            contentDescription = "google logo"
        )
        Text(text = "Sign in with Google", modifier = Modifier.padding(6.dp))
    }

    /*when (loginState) {
        is ApiState.Loading -> Text("로그인 중...")
        is ApiState.Success -> Text("로그인 성공!")
        is ApiState.Error -> Text("로그인 실패: ${(viewModel.loginApiState as ApiState.Error).message}")
        else -> {}
    }*/
}


private fun handleSignIn(result : GetCredentialResponse, navController: NavController) {
    val auth = Firebase.auth
    when (val credential = result.credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navController.navigate(BottomNavItem.Home.screenRoute)
                    } else {
                        Log.e("task error", task.exception.toString())
                    }
                }
            }
        }
    }
}