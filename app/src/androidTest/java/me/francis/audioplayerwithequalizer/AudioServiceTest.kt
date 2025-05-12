package me.francis.audioplayerwithequalizer

import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import me.francis.audioplayerwithequalizer.utils.AppNotificationTargetProvider
import me.francis.notificationmodule.NotificationModule
import me.francis.playbackmodule.PlaybackModuleImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify


@RunWith(AndroidJUnit4::class)
class AudioServiceTest {

    @Mock
    private val context: Context = ApplicationProvider.getApplicationContext<Context>()

    @Mock
    lateinit var mockMediaPlayer: MediaPlayer

    @Mock
    lateinit var mockNotificationManager: NotificationManager

    private var audioService: PlaybackModuleImpl? = null
    private var notificationService: NotificationModule? = null
    private val musicUri = Uri.parse("android.resource://${context.packageName}/raw/test1")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        audioService =
            object : PlaybackModuleImpl(context = context, mediaPlayer = mockMediaPlayer) {
                override var mediaPlayer: MediaPlayer
                    get() = super.mediaPlayer
                    set(value) {}

            }
        audioService!!.mediaPlayer = mockMediaPlayer

        notificationService = object : NotificationModule(
            context = context,
            notificationTargetProvider = AppNotificationTargetProvider()
        ) {
            override var notificationManager: NotificationManager
                get() = super.notificationManager
                set(value) {}
        }
        notificationService!!.notificationManager = mockNotificationManager
    }

    @Test
    fun testPlayAudio() {
        try {
            audioService!!.setDataSource(musicUri)
            audioService!!.play()
            assertEquals(true, audioService!!.playbackState.value.isPlaying)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun testPauseAudio() {
        try {
            audioService!!.mediaPlayer = mockMediaPlayer
            audioService!!.setDataSource(musicUri)
            audioService!!.pause()
            assertEquals(false, audioService!!.playbackState.value.isPlaying)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun testStopAudio() {
        try {
            audioService!!.setDataSource(musicUri)
            audioService!!.stop()
            assertEquals(false, audioService!!.playbackState.value.isReady)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun testCreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assertEquals(true, notificationService!!.createNotificationChannel())
        }
    }

    @Test
    fun testCreateNotification() {
        `when`(mockMediaPlayer.isPlaying).thenReturn(true)
        val notification =
            notificationService!!.buildNotification(currentTrack = musicUri, isPlaying = true)
        assertNotNull(notification)
    }
}
