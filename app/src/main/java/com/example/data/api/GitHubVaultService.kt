package com.example.data.api

import android.util.Base64
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GitHubFileResponse(
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "path") val path: String?,
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "content") val content: String?,
    @field:Json(name = "encoding") val encoding: String?
)

@JsonClass(generateAdapter = true)
data class GitHubPutRequest(
    @field:Json(name = "message") val message: String,
    @field:Json(name = "content") val content: String,
    @field:Json(name = "sha") val sha: String? = null
)

@JsonClass(generateAdapter = true)
data class GitHubPutResponse(
    @field:Json(name = "content") val content: GitHubFileResponse?
)

interface GitHubVaultApi {
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContent(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "application/vnd.github+json",
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path", encoded = true) path: String
    ): Response<GitHubFileResponse>

    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun updateFileContent(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "application/vnd.github+json",
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path", encoded = true) path: String,
        @Body body: GitHubPutRequest
    ): Response<GitHubPutResponse>
}

object GitHubVaultClient {
    private const val BASE_URL = "https://api.github.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GitHubVaultApi by lazy {
        retrofit.create(GitHubVaultApi::class.java)
    }

    sealed class VaultResult {
        data class Success(val message: String, val content: String? = null, val sha: String? = null) : VaultResult()
        data class Error(val errorMessage: String) : VaultResult()
    }

    suspend fun testConnection(token: String, repoString: String, path: String, context: android.content.Context? = null): VaultResult {
        if (context != null && !com.example.utils.NetworkUtils.isNetworkAvailable(context)) {
            return VaultResult.Error("Offline: No internet connection detected. Please check your network connection.")
        }
        if (token.isBlank() || repoString.isBlank() || path.isBlank()) {
            return VaultResult.Error("Fill in GitHub Settings first")
        }
        val parts = repoString.trim().split("/")
        if (parts.size != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            return VaultResult.Error("Invalid repository format. Use username/repository-name")
        }
        val owner = parts[0]
        val repo = parts[1]

        return try {
            val response = service.getFileContent(
                authorization = "Bearer ${token.trim()}",
                owner = owner,
                repo = repo,
                path = path.trim().trimStart('/')
            )
            when (response.code()) {
                200 -> VaultResult.Success("Connected! Ready to sync.")
                404 -> VaultResult.Success("Connected! Ready to sync.") // 404 means file doesn't exist yet, which is fine
                401 -> VaultResult.Error("401 Unauthorized: Invalid PAT token.")
                403 -> VaultResult.Error("403 Forbidden: Check token 'repo' permissions.")
                else -> VaultResult.Error("HTTP Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            VaultResult.Error("Network Error: ${e.localizedMessage ?: "Failed to connect to GitHub"}")
        }
    }

    suspend fun pullBackupFile(token: String, repoString: String, path: String, context: android.content.Context? = null): VaultResult {
        if (context != null && !com.example.utils.NetworkUtils.isNetworkAvailable(context)) {
            return VaultResult.Error("Offline: No internet connection detected. Please check your network connection.")
        }
        if (token.isBlank() || repoString.isBlank() || path.isBlank()) {
            return VaultResult.Error("Fill in GitHub Settings first")
        }
        val parts = repoString.trim().split("/")
        if (parts.size != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            return VaultResult.Error("Invalid repository format. Use username/repository-name")
        }
        val owner = parts[0]
        val repo = parts[1]

        return try {
            val response = service.getFileContent(
                authorization = "Bearer ${token.trim()}",
                owner = owner,
                repo = repo,
                path = path.trim().trimStart('/')
            )
            if (response.code() == 200) {
                val body = response.body()
                val encodedContent = body?.content
                if (encodedContent == null) {
                    VaultResult.Error("Remote file content is empty.")
                } else {
                    // Base64 decode handling newlines
                    val decodedBytes = Base64.decode(encodedContent, Base64.DEFAULT)
                    val jsonText = String(decodedBytes, Charsets.UTF_8)
                    VaultResult.Success(message = "File fetched successfully", content = jsonText, sha = body.sha)
                }
            } else if (response.code() == 404) {
                VaultResult.Error("404 Not Found: Remote file '$path' does not exist in repository yet.")
            } else {
                VaultResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            VaultResult.Error("Network Error: ${e.localizedMessage ?: "Pull failed"}")
        }
    }

    suspend fun pushBackupFile(
        token: String,
        repoString: String,
        path: String,
        jsonPayload: String,
        context: android.content.Context? = null
    ): VaultResult {
        if (context != null && !com.example.utils.NetworkUtils.isNetworkAvailable(context)) {
            return VaultResult.Error("Offline: No internet connection detected. Please check your network connection.")
        }
        if (token.isBlank() || repoString.isBlank() || path.isBlank()) {
            return VaultResult.Error("Fill in GitHub Settings first")
        }
        val parts = repoString.trim().split("/")
        if (parts.size != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            return VaultResult.Error("Invalid repository format. Use username/repository-name")
        }
        val owner = parts[0]
        val repo = parts[1]
        val cleanPath = path.trim().trimStart('/')

        return try {
            // First perform a GET request to retrieve the latest sha hash
            var existingSha: String? = null
            val getResponse = service.getFileContent(
                authorization = "Bearer ${token.trim()}",
                owner = owner,
                repo = repo,
                path = cleanPath
            )
            if (getResponse.code() == 200) {
                existingSha = getResponse.body()?.sha
            } else if (getResponse.code() != 404) {
                return VaultResult.Error("Failed to check existing file: HTTP ${getResponse.code()} ${getResponse.message()}")
            }

            // Base64 encode JSON payload
            val encodedContent = Base64.encodeToString(jsonPayload.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            val putRequest = GitHubPutRequest(
                message = "Vault Backup: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}",
                content = encodedContent,
                sha = existingSha
            )

            val putResponse = service.updateFileContent(
                authorization = "Bearer ${token.trim()}",
                owner = owner,
                repo = repo,
                path = cleanPath,
                body = putRequest
            )

            if (putResponse.isSuccessful) {
                VaultResult.Success("Database successfully pushed to GitHub repository!")
            } else {
                VaultResult.Error("Push failed: HTTP ${putResponse.code()} ${putResponse.message()}")
            }
        } catch (e: Exception) {
            VaultResult.Error("Push Error: ${e.localizedMessage ?: "Failed to upload to GitHub"}")
        }
    }
}
