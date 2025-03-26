package com.purang.hellofood.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
/*import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.purang.interesttodoapp.ui.models.ApiState
import com.purang.hellofood.views.login.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.internal.concurrent.Task*/

class LoginViewModel : ViewModel() {
    /*private val _loginApiState = MutableStateFlow<ApiState>(ApiState.Idle)
    val loginApiState: StateFlow<ApiState> = _loginApiState

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>?) {
        _loginApiState.value = ApiState.Loading

        if (task == null) {
            _loginApiState.value = ApiState.Error("Google 로그인 실패")
            return
        }

        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                Log.d("LoginViewModel", "Google sign in success: ${account.id}")
                _loginApiState.value = ApiState.Success("Google 로그인 성공")
            } ?: run {
                _loginApiState.value = ApiState.Error("Google 로그인 실패")
            }
        } catch (e: ApiException) {
            Log.e("LoginViewModel", "Google sign in failed: ${e.statusCode}")
            _loginApiState.value = ApiState.Error("Google 로그인 실패")
        }
    }*/
}