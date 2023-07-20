package com.example.munchbox.di

import com.example.munchbox.data.AuthRepo
import com.example.munchbox.data.AuthRepoImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesRepoImpl(firebaseAuth: FirebaseAuth): AuthRepo {
        return AuthRepoImpl(firebaseAuth)
    }
}