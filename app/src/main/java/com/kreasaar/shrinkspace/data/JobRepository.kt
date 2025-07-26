package com.kreasaar.shrinkspace.data

class JobRepository(private val jobDao: JobDao) {
    suspend fun getAllJobs() = jobDao.getAll()
    suspend fun insertJob(jobState: JobState) = jobDao.insert(jobState)
    suspend fun updateJob(jobState: JobState) = jobDao.update(jobState)
    suspend fun deleteJob(jobState: JobState) = jobDao.delete(jobState)
} 