package com.arkivanov.minesweeper.game.stopwatch

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class StopwatchViewModel(
    private val stopwatchStateHolder: StopwatchStateHolder,
    private val scope: CoroutineScope,
) {
    private var job: Job? = null
    private val _ticker = MutableStateFlow(0)
    val ticker: StateFlow<Int> = _ticker

    fun start() {
        if (job == null) startJob()
        stopwatchStateHolder.start()
    }

    private fun startJob() {
        scope.launch {
            while (isActive) {
                _ticker.value = stopwatchStateHolder.tick()
                delay(20)
            }
        }
    }

    fun pause() {
        stopwatchStateHolder.pause()
        stopJob()
    }

    fun stop() {
        stopwatchStateHolder.stop()
        stopJob()
        clearValue()
    }

    private fun stopJob() {
        scope.coroutineContext.cancelChildren()
        job = null
    }

    private fun clearValue() {
        _ticker.value = 0
    }
}
