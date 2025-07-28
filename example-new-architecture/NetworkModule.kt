package com.petchat.android.core.network.di

import com.petchat.android.BuildConfig
import com.petchat.android.core.network.interceptor.AuthInterceptor
import com.petchat.android.core.network.interceptor.ErrorInterceptor
import com.petchat.android.core.network.adapter.NetworkResultCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .add(
                BuildConfig.API_DOMAIN,
                "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=" // 替换为实际的证书指纹
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        certificatePinner: CertificatePinner
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(loggingInterceptor)
            .certificatePinner(certificatePinner)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(NetworkResultCallAdapterFactory())
            .build()
    }
}

// 认证拦截器
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 跳过不需要认证的请求
        if (shouldSkipAuth(originalRequest)) {
            return chain.proceed(originalRequest)
        }
        
        val accessToken = runBlocking { tokenManager.getAccessToken() }
        
        if (accessToken.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        // 添加认证头
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        
        val response = chain.proceed(authenticatedRequest)
        
        // 处理token过期
        if (response.code == 401) {
            response.close()
            
            val newToken = runBlocking { 
                tokenManager.refreshToken()
            }
            
            return if (newToken != null) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
                chain.proceed(newRequest)
            } else {
                // 跳转到登录页面
                response
            }
        }
        
        return response
    }
    
    private fun shouldSkipAuth(request: Request): Boolean {
        val path = request.url.encodedPath
        return path.contains("/auth/login") || 
               path.contains("/auth/register") ||
               path.contains("/auth/refresh")
    }
}

// 错误处理拦截器
class ErrorInterceptor @Inject constructor() : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        return try {
            val response = chain.proceed(request)
            
            if (!response.isSuccessful) {
                // 解析错误响应
                val errorBody = response.body?.string()
                throw ApiException(
                    code = response.code,
                    message = parseErrorMessage(errorBody)
                )
            }
            
            response
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: Exception) {
            if (e is ApiException || e is NetworkException) throw e
            throw UnknownException("Unknown error: ${e.message}", e)
        }
    }
    
    private fun parseErrorMessage(errorBody: String?): String {
        // 解析服务器错误响应
        return errorBody ?: "Unknown error"
    }
}

// 自定义异常
sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause)

class NetworkException(message: String, cause: Throwable? = null) : AppException(message, cause)
class ApiException(val code: Int, message: String) : AppException(message)
class UnknownException(message: String, cause: Throwable? = null) : AppException(message, cause)