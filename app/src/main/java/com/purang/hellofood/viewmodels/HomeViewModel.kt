package com.purang.hellofood.viewmodels

import androidx.lifecycle.ViewModel
import com.purang.hellofood.repositories.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
) : ViewModel() {

}