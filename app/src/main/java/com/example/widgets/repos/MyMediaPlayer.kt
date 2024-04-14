import android.content.Context
import android.media.MediaPlayer
class MyMediaPlayer(private val context: Context, private var audioFilePath: Int) : MediaPlayer() {

    fun play(onStartPlaying: () -> Unit, onPlaying: () -> Unit) {
        if (!isPlaying) {
            println("Not playing!!!")
            //start()
            onStartPlaying()
        } else {
            println("Playing!!!")
            //pause()
            onPlaying()
        }
    }

    override fun pause() {
        if (isPlaying) {
            super.pause()
        }
    }

    override fun stop() {
        if (isPlaying) {
            super.stop()
            reset()
        }
    }
}