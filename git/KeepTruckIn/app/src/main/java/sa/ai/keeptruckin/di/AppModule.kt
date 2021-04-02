package sa.ai.keeptruckin.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.lang.math.NumberUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sa.ai.keeptruckin.BuildConfig
import sa.ai.keeptruckin.data.CityRepository
import sa.ai.keeptruckin.data.CityService
import sa.ai.keeptruckin.data.ICityRepository
import sa.ai.keeptruckin.data.sources.CityRemoteSource
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

//@Module
//@InstallIn(ApplicationComponent::class)
//object DataSourceModule {
//    @Singleton
//    @Provides
//    fun provideCityDataSource(): CityRemoteSource = CityRemoteSource()
//}

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideCityRepository(): ICityRepository {
        return CityRepository()
    }
}

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    /**
     * retrofit http interceptor to manage api logs, only available in debug view
     */
    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor.Level {
        return if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
    }

    /**
     * retrofit okhttp client to handle network calls
     */
    @Singleton
    @Provides
    fun provideHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            followRedirects(true)
            followSslRedirects(true)
            retryOnConnectionFailure(true)
            cache(null)
            connectTimeout(NumberUtils.LONG_ONE, TimeUnit.MINUTES)
            readTimeout(NumberUtils.LONG_ONE, TimeUnit.MINUTES)
            writeTimeout(NumberUtils.LONG_ONE, TimeUnit.MINUTES)
            interceptors().add(loggingInterceptor)
        }.build()
    }

    /**
     * this method will create a retrofit instance to execute server calls for City
     * [baseUrl] for nyt client
     */
    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): CityService {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CityService::class.java)
    }
}