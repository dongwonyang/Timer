package com.example.timer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timer.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var timerJob: Job? = null
    private var startTime: LocalDateTime? = null
    private var elapsedDuration: Duration = Duration.ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        clickTimerBtn()
        clickClearBtn()
    }

    private fun clickTimerBtn() {
        val tvTimer = binding.tvTime
        val btnTimer = binding.btnTimer

        fun convertTimeToString(duration: Duration): String {
            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60
            val seconds = duration.seconds % 60
            val milliseconds = duration.toMillis() % 1000

            return String.format(
                "%02d : %02d : %02d.%02d",
                hours,
                minutes,
                seconds,
                milliseconds / 10
            )
        }

        fun startTimer() {
            timerJob = CoroutineScope(Dispatchers.Main).launch {
                startTime = LocalDateTime.now()
                btnTimer.text = "stop"

                while (true) {
                    val currentDuration =
                        elapsedDuration.plus(Duration.between(startTime!!, LocalDateTime.now()))
                    tvTimer.text = "time: ${convertTimeToString(currentDuration)}"
                    delay(10)
                }
            }
        }

        fun stopTimer() {
            elapsedDuration =
                elapsedDuration.plus(Duration.between(startTime!!, LocalDateTime.now()))
            timerJob?.cancel()
            timerJob = null
            btnTimer.text = "start"
        }

        binding.btnTimer.setOnClickListener {
            if (timerJob == null) {
                startTimer()
            } else {
                stopTimer()
            }
        }
    }

    private fun clickClearBtn() = with(binding) {
        btnTimeClear.setOnClickListener {
            tvTime.text = "time: 00 : 00 : 00.00"
            btnTimer.text = "start"

            startTime = null
            elapsedDuration = Duration.ZERO // 경과 시간 초기화
            timerJob?.cancel()
            timerJob = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
    }
}
