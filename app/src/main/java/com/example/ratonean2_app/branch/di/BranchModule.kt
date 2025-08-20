package com.example.ratonean2_app.branch.di

import com.example.ratonean2_app.branch.data.provider.BranchProviderImpl
import com.example.ratonean2_app.branch.domain.provider.BranchProvider
import com.example.ratonean2_app.branch.domain.usecase.CreateBranchUseCase
import com.example.ratonean2_app.branch.domain.usecase.DeleteBranchUseCase
import com.example.ratonean2_app.branch.domain.usecase.GetBranchByIdUseCase
import com.example.ratonean2_app.branch.domain.usecase.GetBranchesUseCase
import com.example.ratonean2_app.branch.domain.usecase.UpdateBranchUseCase
import org.koin.dsl.module

val branchModule = module{
    single<BranchProvider> { BranchProviderImpl(get()) }
    factory { GetBranchesUseCase(get()) }
    factory { GetBranchByIdUseCase(get()) }
    factory { CreateBranchUseCase(get()) }
    factory { UpdateBranchUseCase(get()) }
    factory { DeleteBranchUseCase(get()) }
}
