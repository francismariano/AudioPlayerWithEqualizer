package me.francis.playbackmodule

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.net.toUri
import java.io.IOException

interface PlaybackListener {
    fun onPlaybackStateChanged(state: Boolean)
    fun onPlaybackStopped()
    fun onPositionChanged(position: Int, duration: Int)
}

class PlaybackModule(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var listener : PlaybackListener? = null
    private val handler = Handler(Looper.getMainLooper())
    private var positionUpdateRunnable: Runnable? = null
    private var currentFilePath: String? = null

    // Configuração inicial do player
    fun initialize() {
        release() // Garante que não há vazamento de recursos

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            setOnPreparedListener {
                startPositionUpdates()
                listener?.onPlaybackStateChanged(isPlaying())
            }

            setOnCompletionListener {
                stopPositionUpdates()
                listener?.onPlaybackStateChanged(isPlaying())
            }

            setOnErrorListener { _, what, extra ->
                stopPositionUpdates()
                val errorMsg = when (what) {
                    MediaPlayer.MEDIA_ERROR_UNKNOWN -> "Unknown error"
                    MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "Server died"
                    else -> "Error code $what, extra $extra"
                }
//                listeners.forEach { it.onPlaybackError(errorMsg) }
                false
            }
        }
    }

    // Controles básicos de reprodução
    fun prepare(filePath: String) {

        Log.d("PlaybackModule", "Play")

        if (filePath == currentFilePath && mediaPlayer?.isPlaying == true) {
            return
        }

        try {
            currentFilePath = filePath
            mediaPlayer?.apply {
                reset()
                setDataSource(context, filePath.toUri())
                prepareAsync() // Preparação assíncrona para não bloquear a UI
            }
        } catch (e: IOException) {
            Log.e("PlaybackModule", "Error playing file: $filePath", e)
//            listeners.forEach { it.onPlaybackError("File not found or invalid format") }
        } catch (e: IllegalStateException) {
            Log.e("PlaybackModule", "Error playing file: $filePath", e)
//            listeners.forEach { it.onPlaybackError("Player in invalid state") }
        }
    }

    fun playPause() {
        Log.d("PlaybackModule", "Play / Pause = ${isPlaying()}")
        if (isPlaying()) {
            mediaPlayer?.pause()
            stopPositionUpdates()

//            listeners.forEach { it.onPlaybackPaused() }
        } else {
            mediaPlayer?.start()
        }
    }

    fun stop() {
        Log.d("PlaybackModule", "Stop")
        mediaPlayer?.stop()
        stopPositionUpdates()
        currentFilePath = null
        listener?.onPlaybackStateChanged(isPlaying())
    }

    // Controle de busca (seek)
    fun seekTo(positionMs: Int) {
        mediaPlayer?.takeIf { !it.isPlaying }?.apply {
            seekTo(positionMs)
            // Força atualização imediata da posição
            handler.post {
                listener?.onPositionChanged(positionMs, this.duration)
            }
        }

        mediaPlayer?.takeIf { it.isPlaying }?.apply {
            seekTo(positionMs)
            // A atualização contínua vai manter a posição correta
        }
    }

    // Informações do player
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    fun getDuration(): Int = mediaPlayer?.duration ?: 0

    // Gerenciamento de listeners
    fun addListener(l: PlaybackListener) {
        listener = l
    }

    fun removeListener() {
        listener = null
    }

    // Atualização periódica da posição
    private fun startPositionUpdates() {
        stopPositionUpdates()

        positionUpdateRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        listener?.onPositionChanged(player.currentPosition, player.duration)
                    }
                }
                handler.postDelayed(this, 1000) // Atualiza a cada 1 segundo
            }
        }
        positionUpdateRunnable?.let { handler.post(it) }
    }

    private fun stopPositionUpdates() {
        positionUpdateRunnable?.let { handler.removeCallbacks(it) }
        positionUpdateRunnable = null
    }

    // Limpeza de recursos
    fun release() {
        stop()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }
}