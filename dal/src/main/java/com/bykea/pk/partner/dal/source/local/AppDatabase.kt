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

@Database(entities = [Job::class], version = 5, exportSchema = false)
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
        private val MIGRATION_3_4 = object : Migration(3,4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE jobs")
                database.execSQL("CREATE TABLE jobs (`isComplete` INTEGER NOT NULL, `id` INTEGER NOT NULL, `state` TEXT, `booking_no` TEXT, `order_no` TEXT, `trip_id` TEXT, `trip_type` TEXT, `service_code` INTEGER NOT NULL, `customer_id` TEXT, `creator_type` TEXT, `fare_est` INTEGER NOT NULL, `cod_value` INTEGER, `amount` INTEGER, `voice_note` TEXT, `dt` TEXT, `rules_priority` INTEGER, `pick_address` TEXT, `pick_zone_en` TEXT, `pick_zone_ur` TEXT, `pick_lat` REAL, `pick_lng` REAL, `pick_distance` INTEGER, `pick_duration` INTEGER, `drop_address` TEXT, `drop_zone_en` TEXT, `drop_zone_ur` TEXT, `drop_lat` REAL, `drop_lng` REAL, `drop_distance` INTEGER, `drop_duration` INTEGER, `receiver_name` TEXT, `receiver_phone` TEXT, `receiver_address` TEXT, `sender_name` TEXT, `sender_phone` TEXT, `sender_address` TEXT, PRIMARY KEY(`id`))")
            }
        }
        private val MIGRATION_4_5 = object : Migration(4,5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE jobs")
                database.execSQL("CREATE TABLE jobs (`isComplete` INTEGER NOT NULL, `id` INTEGER NOT NULL, `state` TEXT, `booking_no` TEXT, `order_no` TEXT, `trip_id` TEXT, `trip_type` TEXT, `service_code` INTEGER NOT NULL, `customer_id` TEXT, `customer_name` TEXT, `creator_type` TEXT, `fare_est` INTEGER NOT NULL, `cod_value` INTEGER, `amount` INTEGER, `voice_note` TEXT, `dt` TEXT, `rules_priority` INTEGER, `pick_address` TEXT, `pick_zone_en` TEXT, `pick_zone_ur` TEXT, `pick_lat` REAL, `pick_lng` REAL, `pick_distance` INTEGER, `pick_duration` INTEGER, `drop_address` TEXT, `drop_zone_en` TEXT, `drop_zone_ur` TEXT, `drop_lat` REAL, `drop_lng` REAL, `drop_distance` INTEGER, `drop_duration` INTEGER, `receiver_name` TEXT, `receiver_phone` TEXT, `receiver_address` TEXT, `sender_name` TEXT, `sender_phone` TEXT, `sender_address` TEXT, PRIMARY KEY(`id`))")
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build()
        }
    }
}
