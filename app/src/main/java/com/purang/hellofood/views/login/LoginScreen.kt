package com.purang.hellofood.views.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.purang.hellofood.BottomNavItem
import com.purang.hellofood.R
import com.purang.hellofood.ui.theme.greenFoodColor2
import com.purang.hellofood.utils.CredentialManagerProvider
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.utils.PreferenceDataStore
import com.purang.hellofood.viewmodels.LoginViewModel
import com.purang.hellofood.views.camera.analysis.saveFoodLogToFireStore
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
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val credentialManager = CredentialManagerProvider.getCredentialManager(context)
    val request = CredentialManagerProvider.getCredentialRequest()
    //Log.d("Login", "Credential Request: $request")
    val loginStateManager by StateManager.isLoggedIn

    var loginState by remember { mutableStateOf<ApiState>(ApiState.Idle) }
    LaunchedEffect(loginStateManager) {
        if (loginStateManager) {
            navController.navigate(BottomNavItem.Home.screenRoute) {
                popUpTo(0) { inclusive = true }  // 뒤로가기 방지
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.wrapContentHeight(),
                containerColor = Color.Transparent,
                contentColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Google 로그인 버튼
                    Image(
                        painter = rememberAsyncImagePainter(model = R.drawable.android_light_sq_su_4x),
                        contentDescription = "Google Sign In",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                coroutineScope.launch {
                                    loginState = ApiState.Loading
                                    try {
                                        val result = credentialManager.getCredential(context, request)
                                        Log.d("Login", "로그인 성공: ${result.credential}")

                                        handleSignIn(result, navController) {
                                            loginState = ApiState.Success("로그인 성공!")
                                        }
                                    } catch (e: GetCredentialCancellationException) {
                                        Log.e("Login", "사용자가 로그인 창을 닫았습니다.")
                                        loginState = ApiState.Error("로그인이 취소되었습니다.")
                                    } catch (e: Exception) {
                                        Log.e("Login Error", "로그인 실패", e)
                                        loginState = ApiState.Error("로그인 실패: ${e.message}")
                                    }
                                }
                            }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hello_food_logo),
                    contentDescription = "HelloFood Logo",
                )
            }


            Spacer(modifier = Modifier.weight(1f))
            /*Text(
                text = "HelloFood에 오신 것을 환영합니다!",
                fontSize = 20.sp,
                color = greenFoodColor2,
                modifier = Modifier.padding(bottom = 16.dp)
            )*/

            when (loginState) {
                is ApiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                is ApiState.Success -> Text("로그인 성공!", color = Color.Green, modifier = Modifier.padding(top = 16.dp))
                is ApiState.Error -> Text("로그인 실패: ${(loginState as ApiState.Error).message}", color = Color.Red, modifier = Modifier.padding(top = 16.dp))
                else -> {}
            }
        }
    }
}

private fun handleSignIn(
    result: GetCredentialResponse,
    navController: NavController,
    onSuccess: () -> Unit
) {
    val auth = Firebase.auth
    when (val credential = result.credential) {
        is CustomCredential -> {
            Log.d("Login", "받은 Credential 타입: ${credential.type}")
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Login", "Firebase 로그인 성공")
                        onSuccess()
                        /*navController.navigate(BottomNavItem.Home.screenRoute) {
                            popUpTo("login") { inclusive = true }
                        }*/
                        StateManager.login()
                    } else {
                        Log.e("Login Error", "Firebase 로그인 실패: ${task.exception?.message}")
                    }
                }
            }
        }
        else -> {
            Log.e("Login Error", "알 수 없는 Credential 타입: ${credential.javaClass.name}")
        }
    }
}
