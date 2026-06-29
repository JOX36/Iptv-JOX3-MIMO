package com.jox3.tv.data.repository

import com.jox3.tv.data.local.dao.ServerAccountDao
import com.jox3.tv.data.local.entity.ServerAccount
import com.jox3.tv.data.remote.api.XtreamApi
import com.jox3.tv.domain.model.ServerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val serverAccountDao: ServerAccountDao,
    private val xtreamApi: XtreamApi
) {
    fun getAllAccounts(): Flow<List<ServerConfig>> =
        serverAccountDao.getAll().map { list ->
            list.map { it.toDomain() }
        }

    fun getActiveAccount(): Flow<ServerConfig?> =
        serverAccountDao.getActive().map { it?.toDomain() }

    suspend fun getActiveAccountSync(): ServerConfig? =
        serverAccountDao.getActiveSync()?.toDomain()

    suspend fun login(url: String, port: String, username: String, password: String): Result<ServerConfig> {
        return try {
            val scheme = if (url.startsWith("https://")) "https" else "http"
            val baseUrl = url.trimEnd('/').removePrefix("http://").removePrefix("https://")
            val api = createApiForServer(baseUrl, port, scheme)
            val response = api.login(username, password)

            if (response.userInfo?.auth == 1) {
                val name = response.serverInfo?.url ?: baseUrl
                val account = ServerAccount(
                    name = name,
                    url = baseUrl,
                    port = port,
                    username = username,
                    password = password,
                    useHttps = scheme == "https",
                    isActive = false
                )
                val id = serverAccountDao.insert(account)
                Result.success(account.copy(id = id).toDomain())
            } else {
                Result.failure(Exception(response.userInfo?.message ?: "Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activateAccount(id: Long) {
        serverAccountDao.setActive(id)
    }

    suspend fun deleteAccount(id: Long) {
        val account = serverAccountDao.getById(id) ?: return
        serverAccountDao.delete(account)
    }

    suspend fun testConnection(url: String, port: String, username: String, password: String): Result<Boolean> {
        return try {
            val scheme = if (url.startsWith("https://")) "https" else "http"
            val baseUrl = url.trimEnd('/').removePrefix("http://").removePrefix("https://")
            val api = createApiForServer(baseUrl, port, scheme)
            val response = api.login(username, password)
            if (response.userInfo?.auth == 1) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.userInfo?.message ?: "Connection failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createApiForServer(url: String, port: String, scheme: String = "http"): XtreamApi {
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("$scheme://$url:$port/")
            .addConverterFactory(retrofit2.converter.moshi.MoshiConverterFactory.create())
            .client(
                okhttp3.OkHttpClient.Builder()
                    .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            )
            .build()
        return retrofit.create(XtreamApi::class.java)
    }

    private fun ServerAccount.toDomain() = ServerConfig(
        id = id,
        name = name,
        url = url,
        port = port,
        username = username,
        password = password,
        useHttps = useHttps,
        isActive = isActive
    )
}
