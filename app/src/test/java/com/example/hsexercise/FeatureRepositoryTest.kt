package com.example.hsexercise

import com.example.hsexercise.feature.database.FeatureModel
import com.example.hsexercise.feature.database.FeatureTableDao
import com.example.hsexercise.feature.network.PicsumApi
import com.example.hsexercise.feature.paging.FeatureRepository
import com.github.javafaker.Faker
import org.junit.jupiter.api.Test
import io.reactivex.Maybe

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.jupiter.api.Assertions.assertEquals
import java.net.UnknownHostException

class FeatureRepositoryTest {

    @MockK
    private lateinit var networkApi: PicsumApi

    @RelaxedMockK
    private lateinit var databaseApi: FeatureTableDao

    private lateinit var repository: FeatureRepository

    private val faker = Faker()
    private val TEST_PAGE_NUMBER = faker.number().randomDigitNotZero()
    private val testNetworkEntity = FeatureModel(
        faker.idNumber().valid(),
        faker.artist().name(),
        faker.internet().url(),
        faker.number().randomDigitNotZero(),
        faker.number().randomDigitNotZero()
    )
    private val testDbEntity = FeatureModel(
        testNetworkEntity.id,
        testNetworkEntity.author,
        testNetworkEntity.url,
        testNetworkEntity.width,
        testNetworkEntity.height,
        TEST_PAGE_NUMBER
    )
    private val testNetworkPage = listOf(testNetworkEntity)
    private val testDbPage = listOf(testDbEntity)
    private val offlineException = UnknownHostException()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        repository = FeatureRepository(networkApi, databaseApi)
    }

    @Test
    fun `getPicturesByPage should return error`() {
        // Offline
        every {
            networkApi.getPictures(TEST_PAGE_NUMBER)
        }.answers { Maybe.error(offlineException) }
        // No DB entries
        every {
            databaseApi.getPage(TEST_PAGE_NUMBER)
        }.answers { Maybe.just(emptyList()) }
        // -> only error
        repository.getPicturesByPage(TEST_PAGE_NUMBER)
            .test()
            .assertError(offlineException)
            .dispose()
    }

    @Test
    fun `getPicturesByPage should return Network page`() {
        // Online
        every {
            networkApi.getPictures(TEST_PAGE_NUMBER)
        }.answers { Maybe.just(testNetworkPage) }
        // No DB entries
        every {
            databaseApi.getPage(TEST_PAGE_NUMBER)
        }.answers { Maybe.just(emptyList()) }
        // -> Network page
        repository.getPicturesByPage(TEST_PAGE_NUMBER)
            .test()
            .assertValue(testNetworkPage)
            .dispose()
    }

    @Test
    fun `getPicturesByPage should return DB page`() {
        // Offline
        every {
            networkApi.getPictures(TEST_PAGE_NUMBER)
        }.answers { Maybe.error(offlineException) }
        // Has DB entries
        every {
            databaseApi.getPage(TEST_PAGE_NUMBER)
        }.answers { Maybe.just(testDbPage) }
        // -> DB page, without errors
        repository.getPicturesByPage(TEST_PAGE_NUMBER)
            .test()
            .assertNoErrors()
            .assertValue(testDbPage)
            .dispose()

        // Online
        every {
            networkApi.getPictures(TEST_PAGE_NUMBER)
        }.answers { Maybe.just(testDbPage) }
        // Has DB entries
        every {
            databaseApi.getPage(TEST_PAGE_NUMBER)
        }.answers { Maybe.just(testDbPage) }
        // -> DB page
        repository.getPicturesByPage(TEST_PAGE_NUMBER)
            .test()
            .assertValue(testDbPage)
            .dispose()
    }

    @Test
    fun `loadNewNetworkPage should insert response into DB`() {
        every {
            networkApi.getPictures(TEST_PAGE_NUMBER)
        }.answers { Maybe.just(testNetworkPage) }

        repository.loadNewNetworkPage(TEST_PAGE_NUMBER)
            .test()
            .dispose()

        verify {
            databaseApi.insertAll(testDbPage)
        }
    }

    @Test
    fun `mapNetworkPageToDbPage should return DB page`() {
        val mapResult = repository.mapNetworkPageToDbPage(testNetworkPage, TEST_PAGE_NUMBER)
        assertEquals(testDbPage, mapResult)
    }


    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }
}
