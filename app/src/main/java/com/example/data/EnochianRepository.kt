package com.example.data

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
import com.example.data.reference.EnochianData
import kotlinx.coroutines.flow.Flow

class EnochianRepository(
    private val invocationDao: InvocationDao,
    private val journalDao: JournalDao,
    private val sigilDao: SigilDao,
    private val enochianKeyDao: EnochianKeyDao? = null,
    private val characterMasteryDao: CharacterMasteryDao? = null
) {
    // Character Mastery Flashcards
    val allCharacterMasteries: Flow<List<CharacterMasteryEntity>>? = characterMasteryDao?.getAllMasteryRecords()

    suspend fun initializeCharacterMasteriesIfNeeded() {
        characterMasteryDao?.let { dao ->
            if (dao.getMasteryRecordCount() == 0) {
                val initialRecords = EnochianData.ENNOCHIAN_LETTERS.map { letter ->
                    CharacterMasteryEntity(
                        letterName = letter.name,
                        enochianChar = letter.enochianChar,
                        englishChar = letter.englishChar.toString(),
                        masteryLevel = 0,
                        timesReviewed = 0,
                        correctCount = 0,
                        lastReviewedTimestamp = System.currentTimeMillis()
                    )
                }
                dao.insertInitialRecords(initialRecords)
            }
        }
    }

    suspend fun recordFlashcardReview(letterName: String, newMasteryLevel: Int, isCorrect: Boolean) {
        characterMasteryDao?.recordReviewResult(
            letterName = letterName,
            newLevel = newMasteryLevel,
            isCorrect = if (isCorrect) 1 else 0
        )
    }

    suspend fun resetFlashcardProgress() {
        characterMasteryDao?.resetAllProgress()
    }
    // Enochian Keys
    val allEnochianKeys: Flow<List<EnochianKeyEntity>>? = enochianKeyDao?.getAllKeys()

    suspend fun initializeDefaultKeysIfNeeded() {
        enochianKeyDao?.let { dao ->
            if (dao.getKeyCount() == 0) {
                val initialKeys = EnochianData.CALLS.map { call ->
                    EnochianKeyEntity(
                        id = call.id,
                        title = call.title,
                        subtitle = call.subtitle,
                        element = call.element,
                        eNochianPhonetic = call.eNochianPhonetic,
                        englishTranslation = call.englishTranslation,
                        purpose = call.purpose,
                        pronunciationGuide = call.pronunciationGuide,
                        frequencyHz = call.frequencyHz,
                        masteryLevel = 0,
                        totalInvocations = 0,
                        lastInvokedTimestamp = 0L
                    )
                }
                dao.insertKeys(initialKeys)
            }
        }
    }

    suspend fun recordKeyInvocation(keyId: Int) {
        enochianKeyDao?.recordKeyInvocation(keyId)
    }
    // Invocations
    val allInvocations: Flow<List<InvocationRecord>> = invocationDao.getAllInvocations()
    val invocationCount: Flow<Int> = invocationDao.getInvocationCount()
    val totalDurationSeconds: Flow<Int?> = invocationDao.getTotalDurationSeconds()

    suspend fun insertInvocation(record: InvocationRecord): Long {
        return invocationDao.insertInvocation(record)
    }

    suspend fun deleteInvocation(id: Long) {
        invocationDao.deleteInvocation(id)
    }

    // Journal
    val allJournalEntries: Flow<List<JournalEntry>> = journalDao.getAllJournalEntries()

    suspend fun getJournalEntryById(id: Long): JournalEntry? {
        return journalDao.getJournalEntryById(id)
    }

    suspend fun insertJournalEntry(entry: JournalEntry): Long {
        return journalDao.insertJournalEntry(entry)
    }

    suspend fun updateJournalEntry(entry: JournalEntry) {
        journalDao.updateJournalEntry(entry)
    }

    suspend fun deleteJournalEntry(id: Long) {
        journalDao.deleteJournalEntry(id)
    }

    fun searchJournalEntries(query: String): Flow<List<JournalEntry>> {
        return journalDao.searchJournalEntries(query)
    }

    // Sigils
    val allSavedSigils: Flow<List<SavedSigil>> = sigilDao.getAllSavedSigils()

    suspend fun saveSigil(sigil: SavedSigil): Long {
        return sigilDao.insertSigil(sigil)
    }

    suspend fun deleteSigil(id: Long) {
        sigilDao.deleteSigil(id)
    }
}
