package com.kreasaar.shrinkspace.data

class LogRepository(private val logDao: LogDao) {
    suspend fun getAllLogs() = logDao.getAll()
    suspend fun insertLog(logEntry: LogEntry) = logDao.insert(logEntry)
    suspend fun deleteLog(logEntry: LogEntry) = logDao.delete(logEntry)
} 