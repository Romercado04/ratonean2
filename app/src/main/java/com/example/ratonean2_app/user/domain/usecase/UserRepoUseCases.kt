package com.example.ratonean2_app.user.domain.usecase

import com.example.ratonean2_app.user.domain.model.UserLocation
import com.example.ratonean2_app.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserPrefsLocationUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<UserLocation?> = repository.getUserLocation()
}

class SaveUserPrefsLocationUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(location: UserLocation) = repository.saveUserLocation(location)
}