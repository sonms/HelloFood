package com.purang.hellofood.di

import com.google.firebase.firestore.FirebaseFirestore
import com.purang.hellofood.repositories.FoodLogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFoodLogRepository(firestore: FirebaseFirestore): FoodLogRepository {
        return FoodLogRepository(firestore)
    }
}