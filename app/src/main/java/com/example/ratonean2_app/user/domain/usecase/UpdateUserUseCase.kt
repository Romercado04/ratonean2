package com.example.ratonean2_app.user.domain.usecase

import com.example.ratonean2_app.user.domain.model.User
import com.example.ratonean2_app.user.domain.provider.UserProvider

class UpdateUserUseCase (private val userProvider: UserProvider) {
    operator fun invoke(userUpdate: User) = userProvider.updateUser(userUpdate)
}