package com.example.ratonean2_app.user.domain.repository

import com.example.ratonean2_app.user.domain.model.UserLocation
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserLocation(): Flow<UserLocation?>
    suspend fun saveUserLocation(location: UserLocation)
}