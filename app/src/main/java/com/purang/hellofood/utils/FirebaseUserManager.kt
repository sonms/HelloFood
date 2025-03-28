package com.purang.hellofood.utils

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth

object FirebaseUserManager {

    // 현재 Firebase 사용자 정보를 가져오는 함수
    val currentUser: FirebaseUser? get() = FirebaseAuth.getInstance().currentUser

    // 현재 사용자 ID를 가져오는 함수
    val userId: String?
        get() = currentUser?.uid

    // 현재 사용자의 이메일을 가져오는 함수
    val userEmail: String?
        get() = currentUser?.email

    // 사용자가 로그인되어 있는지 확인하는 함수
    val isUserLoggedIn: Boolean
        get() = currentUser != null
}