package com.bykea.pk.partner.dal.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bykea.pk.partner.dal.Job

/**
 * The Room database for this app
 *
 * @Author: Yousuf Sohail
 */

const val DATABASE_NAME = "bykea-db"

@Database(entities = [Job::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jobRequestsDao(): JobsDao
    abstract fun withdrawDao(): WithDrawDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                        ?: buildDatabase(context).also { instance = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE jobRequests RENAME TO jobs")
                database.execSQL("ALTER TABLE jobs ADD COLUMN amount INTEGER")
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE jobs ADD COLUMN rules_priority TEXT")
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
        }
    }
}
