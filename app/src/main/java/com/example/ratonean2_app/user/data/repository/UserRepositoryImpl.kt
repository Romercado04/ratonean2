package com.example.ratonean2_app.user.data.repository

import com.example.ratonean2_app.user.data.local.UserPreferencesManager
import com.example.ratonean2_app.user.domain.model.UserLocation
import com.example.ratonean2_app.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val prefsManager: UserPreferencesManager
) : UserRepository {
    override fun getUserLocation(): Flow<UserLocation?> = prefsManager.userLocation

    override suspend fun saveUserLocation(location: UserLocation) {
        prefsManager.saveLocation(location)
    }
}