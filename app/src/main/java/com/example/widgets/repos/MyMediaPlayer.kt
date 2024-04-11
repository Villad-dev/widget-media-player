import android.media.MediaPlayer
import java.io.IOException

class MyMediaPlayer(private var audioFilePath: String) : MediaPlayer() {

    init {
        try {
            setDataSource(audioFilePath)
            prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun setOnCompletionListener(listener: OnCompletionListener?) {

        super.setOnCompletionListener(listener)
    }

    override fun setOnErrorListener(listener: OnErrorListener?) {

        super.setOnErrorListener(listener)
    }

    fun play() {
        if (!isPlaying) {
            start()
        }
    }

    override fun pause() {
        if (isPlaying) {
            pause()
        }
    }

    override fun stop() {
        if (isPlaying) {
            stop()
            reset()
            try {
                setDataSource(audioFilePath) // Reset the DataSource
                prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun setAudioFilePath(newAudioFilePath: String) {
        this.audioFilePath = newAudioFilePath
        reset()
        try {
            setDataSource(audioFilePath) // Set new DataSource
            prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}