package me.francis.audioplayerwithequalizer.viewModels

import androidx.lifecycle.ViewModel
import me.francis.audioplayerwithequalizer.services.AudioServiceRepository

class PlayerViewModel(private val audioServiceRepository: AudioServiceRepository) : ViewModel() {

    fun play() = audioServiceRepository.play()

    fun pause() = audioServiceRepository.pause()

    fun seekTo(positionMs: Int) = audioServiceRepository.seekTo(positionMs)

    fun skipToNext(path: String) = audioServiceRepository.skipToNext(path)

    fun skipToPrevious(path: String) = audioServiceRepository.skipToPrevious(path)
}
