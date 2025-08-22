package com.example.ratonean2_app

import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.product.data.provider.ProductProviderImpl
import com.example.ratonean2_app.product.domain.model.Product
import com.example.ratonean2_app.product.domain.usecase.CreateNewProductUseCase
import com.example.ratonean2_app.product.domain.usecase.DeleteProductUseCase
import com.example.ratonean2_app.product.domain.usecase.GetAllProductsUseCase
import com.example.ratonean2_app.product.domain.usecase.GetProductByIdUseCase
import com.example.ratonean2_app.product.domain.usecase.UpdateProductUseCase
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
class ProductProviderImplTest {

    private lateinit var client: HttpClient

    private lateinit var productProviderImpl: ProductProviderImpl

    private lateinit var createProductUseCase: CreateNewProductUseCase
    private lateinit var deleteProductUseCase: DeleteProductUseCase
    private lateinit var getProductByIdUseCase: GetProductByIdUseCase
    private lateinit var getAllProductsUseCase: GetAllProductsUseCase
    private lateinit var updateProductUseCase: UpdateProductUseCase

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

        productProviderImpl = ProductProviderImpl(client)
        createProductUseCase = CreateNewProductUseCase(productProviderImpl)
        deleteProductUseCase = DeleteProductUseCase(productProviderImpl)
        getProductByIdUseCase = GetProductByIdUseCase(productProviderImpl)
        getAllProductsUseCase = GetAllProductsUseCase(productProviderImpl)
        updateProductUseCase = UpdateProductUseCase(productProviderImpl)

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
    fun `getAllProducts should return a list of branches from the API`() = runBlocking {
        val result = getResult(getAllProductsUseCase())
        println("Raw result: $result")
        when (result) {
            is NetworkResponse.Success<*> -> println("Products: ${result.data}")
            is NetworkResponse.Failure<*> -> println("Error: ${result.error}")
            is NetworkResponse.Loading<*> -> println("Loading")
        }
        assertIs<NetworkResponse.Success<List<Product>>>(result)
        assertTrue(
            (result as NetworkResponse.Success<List<Product>>).data!!.isNotEmpty(),
            "La lista de productos no debe estar vacía."
        )
    }

    @Test
    fun `create, update, get by id and delete commerce should work correctly`() = runBlocking<Unit> {
        val uniqueId = "test-${UUID.randomUUID()}"
        val newProduct = Product(
            commerceId = "COM-001",
            flagId = "FLAG-A",
            branchId = "BR-001",
            productId = uniqueId,
            ean = "7791234567890",
            description = "Leche Entera",
            presentationQuantity = 1.0,
            presentationUnit = "Lts",
            brand = "La Lechera",
            listPrice = 1500.0,
            referencePrice = 1450.0,
            referenceQuantity = 1.0,
            referenceUnit = "Lts",
            promoPrice1 = 1200.0,
            promoLegend1 = "2x1",
            promoPrice2 = 1100.0,
            promoLegend2 = "30% off con tarjeta"
        )

        val createResult = getResult(createProductUseCase(newProduct))
        println("Create Result: $createResult")
        assertIs<NetworkResponse.Success<Unit>>(createResult)

        val productToUpdate = newProduct.copy(description = "Leche a la mitad")
        println("Branch to Update: $productToUpdate")

        val getBranchByIdResult = getResult(getProductByIdUseCase(uniqueId))
        assertIs<NetworkResponse.Success<Unit>>(getBranchByIdResult)
        assertTrue (getBranchByIdResult as NetworkResponse.Success<Product> != null,
            "El producto obtenido no debe ser nulo."
        )

        val updateResult = getResult(updateProductUseCase(productToUpdate))
        println("Update Result: $updateResult")
        assertIs<NetworkResponse.Success<Unit>>(updateResult)

        val deleteResult = getResult(deleteProductUseCase(uniqueId))
        println("Delete Result: $deleteResult")
        assertIs<NetworkResponse.Success<Unit>>(deleteResult)

    }
}

