package com.example.ratonean2_app.branch.di

import com.example.ratonean2_app.branch.data.local.BranchLocalDataSource
import com.example.ratonean2_app.branch.data.provider.BranchProviderImpl
import com.example.ratonean2_app.branch.data.repository.BranchRepositoryImpl
import com.example.ratonean2_app.branch.domain.provider.BranchProvider
import com.example.ratonean2_app.branch.domain.repository.BranchRepository
import com.example.ratonean2_app.branch.domain.usecase.CreateBranchUseCase
import com.example.ratonean2_app.branch.domain.usecase.DeleteBranchUseCase
import com.example.ratonean2_app.branch.domain.usecase.GetBranchByIdUseCase
import com.example.ratonean2_app.branch.domain.usecase.GetBranchesUseCase
import com.example.ratonean2_app.branch.domain.usecase.GetNearbyBranchesUseCase
import com.example.ratonean2_app.branch.domain.usecase.UpdateBranchUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val branchModule = module{
    single<BranchProvider> { BranchProviderImpl(get()) }
    factory { GetBranchesUseCase(get()) }
    factory { GetBranchByIdUseCase(get()) }
    factory { GetNearbyBranchesUseCase(get()) }
    factory { CreateBranchUseCase(get()) }
    factory { UpdateBranchUseCase(get()) }
    factory { DeleteBranchUseCase(get()) }
    single { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    single { BranchLocalDataSource(get()) }
    single<BranchRepository> {
        BranchRepositoryImpl(
            localDataSource = get(),
            remoteProvider = get(),
        )
    }
}
