package com.example.ratonean2_app.user.domain.usecase

import com.example.ratonean2_app.user.domain.provider.UserProvider

class GetUsersUseCase (private val userProvider: UserProvider) {
    operator fun invoke() = userProvider.getUsers()
}