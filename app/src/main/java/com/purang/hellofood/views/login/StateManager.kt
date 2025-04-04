package com.purang.hellofood.views.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf

object StateManager {
    // 로그인 상태를 관리하는 변수
    var isLoggedIn = mutableStateOf(false)
        private set  // 외부에서 직접 수정하지 않도록 private로 설정

    // 로그인된 계정 정보 관리
    var accountData: String? = null
        private set  // 외부에서 직접 수정 불가

    fun accountLogin(account: String?) {
        accountData = account
    }

    // 로그인 상태를 변경하는 메서드
    fun login() {
        isLoggedIn.value = true
    }

    fun logout() {
        isLoggedIn.value = false
    }
}