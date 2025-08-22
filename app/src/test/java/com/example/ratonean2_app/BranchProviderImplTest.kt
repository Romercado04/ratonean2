package com.example.ratonean2_app

import com.example.ratonean2_app.branch.data.provider.BranchProviderImpl
import com.example.ratonean2_app.branch.domain.model.Branch
import com.example.ratonean2_app.branch.domain.usecase.CreateBranchUseCase
import com.example.ratonean2_app.branch.domain.usecase.DeleteBranchUseCase
import com.example.ratonean2_app.branch.domain.usecase.GetBranchByIdUseCase
import com.example.ratonean2_app.branch.domain.usecase.GetBranchesUseCase
import com.example.ratonean2_app.branch.domain.usecase.UpdateBranchUseCase
import com.example.ratonean2_app.core.network.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import java.util.*
import kotlin.test.assertIs
import kotlin.test.assertTrue

//Test de integración

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BranchProviderImplTest {

    private lateinit var client: HttpClient
    private lateinit var branchProvider: BranchProviderImpl
    private lateinit var createBranchUseCase: CreateBranchUseCase
    private lateinit var updateBranchUseCase: UpdateBranchUseCase
    private lateinit var deleteBranchUseCase: DeleteBranchUseCase
    private lateinit var getAllBranchesUseCase: GetBranchesUseCase
    private lateinit var getBranchByIdUseCase: GetBranchByIdUseCase

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
        branchProvider = BranchProviderImpl(client)
        createBranchUseCase = CreateBranchUseCase(branchProvider)
        updateBranchUseCase = UpdateBranchUseCase(branchProvider)
        deleteBranchUseCase = DeleteBranchUseCase(branchProvider)
        getAllBranchesUseCase = GetBranchesUseCase(branchProvider)
        getBranchByIdUseCase = GetBranchByIdUseCase(branchProvider)
    }

    @AfterAll
    fun tearDownAll() {
        client.close()
    }

    suspend fun <T> getResult(flow: kotlinx.coroutines.flow.Flow<NetworkResponse<T>>): NetworkResponse<T> {
        val results = mutableListOf<NetworkResponse<T>>()
        flow.collect {
            results.add(it)
        }
        // Return the last non-loading result
        return results.last { it !is NetworkResponse.Loading }
    }


    @Test
    fun `getAllBranches should return a list of branches from the API`() = runBlocking {
        // Collect the result from the flow using the helper function
        val result = getResult(branchProvider.getAllBranches())
        println("Raw result: $result")
        when (result) {
            is NetworkResponse.Success<*> -> println("Branches: ${result.data}")
            is NetworkResponse.Failure<*> -> println("Error: ${result.error}")
            is NetworkResponse.Loading<*> -> println("Loading")
        }
        // Now you can assert on the result itself, not the Flow
        assertIs<NetworkResponse.Success<List<Branch>>>(result)
        assertTrue(
            (result as NetworkResponse.Success<List<Branch>>).data!!.isNotEmpty(),
            "La lista de sucursales no debe estar vacía."
        )
    }

    @Test
    fun `create, update, get by id and delete branch should work correctly`() = runBlocking<Unit> {
        val uniqueId = "test-${UUID.randomUUID()}"
        var newBranch = Branch(
            branchId = uniqueId,
            name = "Sucursal Test",
            commerceId = "1",
            flagId = "2",
            type = "type",
            street = "Calle Falsa",
            number = "123",
            latitude = 1.0,
            longitude = 1.0,
            observations = "Observaciones",
            neighborhood = "Vecindario",
            postalCode = "12345",
            location = "Ciudad",
            province = "Provincia",
            mondaySchedule = "Lunes",
            tuesdaySchedule = "Martes",
            wednesdaySchedule = "Miércoles",
            thursdaySchedule = "Jueves",
            fridaySchedule = "Viernes",
            saturdaySchedule = "Sábado",
            sundaySchedule = "Domingo"
        )

        // Ensure you use getResult here as well
        val createResult = getResult(createBranchUseCase(newBranch))
        println("Create Result: $createResult")
        assertIs<NetworkResponse.Success<Unit>>(createResult)

        val branchToUpdate = newBranch.copy(name = "Sucursal")
        println("Branch to Update: $branchToUpdate")

        val getBranchByIdResult = getResult(getBranchByIdUseCase(uniqueId))
        assertIs<NetworkResponse.Success<Unit>>(getBranchByIdResult)
        assertTrue( getBranchByIdResult as NetworkResponse.Success<Branch> != null,
            "Branch cannot be null"
        )

        val updateResult = getResult(updateBranchUseCase(branchToUpdate))
        println("Update Result: $updateResult")
        assertIs<NetworkResponse.Success<Unit>>(updateResult)

        val deleteResult = getResult(deleteBranchUseCase(uniqueId))
        println("Delete Result: $deleteResult")
        assertIs<NetworkResponse.Success<Unit>>(deleteResult)

    }
}

