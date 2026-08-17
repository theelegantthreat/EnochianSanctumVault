package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CharacterMasteryDao
import com.example.data.dao.EnochianKeyDao
import com.example.data.dao.InvocationDao
import com.example.data.dao.JournalDao
import com.example.data.dao.SigilDao
import com.example.data.model.CharacterMasteryEntity
import com.example.data.model.EnochianKeyEntity
import com.example.data.model.InvocationRecord
import com.example.data.model.JournalEntry
import com.example.data.model.SavedSigil

@Database(
    entities = [
        InvocationRecord::class,
        JournalEntry::class,
        SavedSigil::class,
        EnochianKeyEntity::class,
        CharacterMasteryEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class EnochianDatabase : RoomDatabase() {
    abstract fun invocationDao(): InvocationDao
    abstract fun journalDao(): JournalDao
    abstract fun sigilDao(): SigilDao
    abstract fun enochianKeyDao(): EnochianKeyDao
    abstract fun characterMasteryDao(): CharacterMasteryDao

    companion object {
        @Volatile
        private var INSTANCE: EnochianDatabase? = null

        fun getDatabase(context: Context): EnochianDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EnochianDatabase::class.java,
                    "enochian_magic.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
