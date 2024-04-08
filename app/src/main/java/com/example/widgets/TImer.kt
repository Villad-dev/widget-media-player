package com.example.widgets

import android.os.CountDownTimer

class MyTimer(private val duration: Long, private val onTick: (Int) -> Unit, private val onFinish: () -> Unit) {

    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false

    fun startTimer() {
        if (!isTimerRunning) {
            countDownTimer = object : CountDownTimer(duration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val progress = ((duration - millisUntilFinished) * 100 / duration).toInt()
                    onTick.invoke(progress)
                }

                override fun onFinish() {
                    isTimerRunning = false
                    onFinish.invoke()
                }
            }.start()

            isTimerRunning = true
        }
    }

    fun stopTimer() {
        countDownTimer.cancel()
        isTimerRunning = false
    }

    fun resetTimer() {
        countDownTimer.cancel()
        onTick.invoke(0)
        isTimerRunning = false
    }
}
