package com.insightra.app.di

import android.content.Context
import androidx.room.Room
import com.insightra.app.data.db.InsightraDatabase
import com.insightra.app.network.OpenAIRepository
import com.insightra.app.network.OpenAIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): InsightraDatabase =
        Room.databaseBuilder(ctx, InsightraDatabase::class.java, "insightra.db").build()

    @Provides
    @Singleton
    fun provideOpenAI(): OpenAIService = OpenAIService.create()

    @Provides
    @Singleton
    fun provideOpenAIRepository(@ApplicationContext ctx: Context, service: OpenAIService): OpenAIRepository =
        OpenAIRepository(service, ctx.contentResolver) { ctx.cacheDir }
}
