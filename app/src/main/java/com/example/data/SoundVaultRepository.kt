package com.example.data

import kotlinx.coroutines.flow.Flow

class SoundVaultRepository(
    private val trackDao: TrackDao,
    private val bundleDao: BundleDao
) {
    val allTracks: Flow<List<TrackEntity>> = trackDao.getAllTracks()
    val allBundles: Flow<List<SoundBundleEntity>> = bundleDao.getAllBundles()

    suspend fun insertTrack(track: TrackEntity) {
        trackDao.insertTrack(track)
    }

    suspend fun updateTrack(track: TrackEntity) {
        trackDao.updateTrack(track)
    }

    suspend fun deleteTrackById(id: String) {
        trackDao.deleteTrackById(id)
    }

    suspend fun clearAllTracks() {
        trackDao.clearAllTracks()
    }

    suspend fun insertBundle(bundle: SoundBundleEntity) {
        bundleDao.insertBundle(bundle)
    }

    suspend fun deleteBundleById(id: String) {
        bundleDao.deleteBundleById(id)
    }
}
