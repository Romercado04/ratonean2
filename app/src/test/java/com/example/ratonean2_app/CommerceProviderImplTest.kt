package com.example.ratonean2_app

import com.example.ratonean2_app.commerce.data.provider.CommerceProviderImpl
import com.example.ratonean2_app.commerce.domain.model.Commerce
import com.example.ratonean2_app.commerce.domain.usecase.CreateCommerceUseCase
import com.example.ratonean2_app.commerce.domain.usecase.DeleteCommerceUseCase
import com.example.ratonean2_app.commerce.domain.usecase.GetCommerceByIdUseCase
import com.example.ratonean2_app.commerce.domain.usecase.GetCommercesUseCase
import com.example.ratonean2_app.commerce.domain.usecase.UpdateCommerceUseCase
import com.example.ratonean2_app.core.network.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID
import kotlin.test.assertIs
import kotlin.test.assertTrue

//Test de integración

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommerceProviderImplTest {

    private lateinit var client: HttpClient

    private lateinit var commerceProviderImpl: CommerceProviderImpl

    private lateinit var createCommerceUseCase: CreateCommerceUseCase
    private lateinit var deleteCommerceUseCase: DeleteCommerceUseCase
    private lateinit var getCommerceByIdUseCase: GetCommerceByIdUseCase
    private lateinit var getCommercesUseCase: GetCommercesUseCase
    private lateinit var updateCommerceUseCase: UpdateCommerceUseCase

    @BeforeAll
    fun setupAll() {
        client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        commerceProviderImpl = CommerceProviderImpl(client)
        createCommerceUseCase = CreateCommerceUseCase(commerceProviderImpl)
        deleteCommerceUseCase = DeleteCommerceUseCase(commerceProviderImpl)
        getCommerceByIdUseCase = GetCommerceByIdUseCase(commerceProviderImpl)
        getCommercesUseCase = GetCommercesUseCase(commerceProviderImpl)
        updateCommerceUseCase = UpdateCommerceUseCase(commerceProviderImpl)

    }

    @AfterAll
    fun tearDownAll() {
        client.close()
    }

    suspend fun <T> getResult(flow: Flow<NetworkResponse<T>>): NetworkResponse<T> {
        val results = mutableListOf<NetworkResponse<T>>()
        flow.collect {
            results.add(it)
        }
        return results.last { it !is NetworkResponse.Loading }
    }


    @Test
    fun `getAllBranches should return a list of branches from the API`() = runBlocking {
        val result = getResult(getCommercesUseCase())
        println("Raw result: $result")
        when (result) {
            is NetworkResponse.Success<*> -> println("Commerces: ${result.data}")
            is NetworkResponse.Failure<*> -> println("Error: ${result.error}")
            is NetworkResponse.Loading<*> -> println("Loading")
        }
        assertIs<NetworkResponse.Success<List<Commerce>>>(result)
        assertTrue(
            (result as NetworkResponse.Success<List<Commerce>>).data!!.isNotEmpty(),
            "La lista de sucursales no debe estar vacía."
        )
    }

    @Test
    fun `create, update, get by id and delete commerce should work correctly`() = runBlocking<Unit> {
        val uniqueId = "test-${UUID.randomUUID()}"
        val newCommerce = Commerce(
            id = uniqueId,
            flagId = "FLAG-A",
            cuit = "30-12345678-9",
            businessName = "Empresa de Ejemplo S.A.",
            flagName = "Visa",
            flagUrl = "https://example.com/visa.png",
            sepaVersion = "1.0",
            lastUpdate = "2025-08-21T10:00:00Z"
        )

        val createResult = getResult(createCommerceUseCase(newCommerce))
        println("Create Result: $createResult")
        assertIs<NetworkResponse.Success<Unit>>(createResult)

        val commerceToUpdate = newCommerce.copy(businessName = "Empresa :O")
        println("Branch to Update: $commerceToUpdate")

        val getBranchByIdResult = getResult(getCommerceByIdUseCase(uniqueId))
        assertIs<NetworkResponse.Success<Unit>>(getBranchByIdResult)
        assertTrue (getBranchByIdResult as NetworkResponse.Success<Commerce> != null,
            "La sucursal no debe ser nula."
        )

        val updateResult = getResult(updateCommerceUseCase(commerceToUpdate))
        println("Update Result: $updateResult")
        assertIs<NetworkResponse.Success<Unit>>(updateResult)

        val deleteResult = getResult(deleteCommerceUseCase(uniqueId))
        println("Delete Result: $deleteResult")
        assertIs<NetworkResponse.Success<Unit>>(deleteResult)

    }
}

