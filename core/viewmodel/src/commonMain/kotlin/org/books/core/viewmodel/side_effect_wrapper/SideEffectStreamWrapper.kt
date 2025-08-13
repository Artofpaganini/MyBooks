package org.books.core.viewmodel.side_effect_wrapper

import androidx.annotation.AnyThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import org.books.core.viewmodel.logStage
import java.util.concurrent.LinkedBlockingDeque

@AnyThread
internal class SideEffectStreamWrapper<SideEffect>(private val vmName: String) {

    private val pendingEffects by lazy { LinkedBlockingDeque<SideEffect>() }

    private val stream = MutableStateFlow<SideEffect?>(null)

    private var reEntranceCondition: (() -> Boolean)? = null

    fun getStream(): Flow<SideEffect> = stream.filterNotNull()
        .onStart {
            if (pendingEffects.isEmpty()) return@onStart
            val condition = reEntranceCondition?.invoke()
            vmName.logStage(
                methodName = "getStream",
                message = "При подписке, доступен список необработанных эффектов ${getPendingEffectNames()}!"
            )

            if (condition == true) {
                postPending()
            } else {
                val pendingEffect = pendingEffects.pollLast()
                vmName.logStage(
                    methodName = "getStream",
                    message = "Условие не было выполнено, эффект ${pendingEffect.getName()} удален!"
                )
            }
        }

    infix fun post(newEffect: SideEffect) {
        if (stream.value == null) {
            vmName.logStage(methodName = "postSideEffect", message = "Эффект ${newEffect.getName()} отправлен!")
            stream.update { newEffect }
        } else {
            vmName.logStage(
                methodName = "postSideEffect",
                message = "Эффект ${newEffect.getName()} добавлен, в очередь, отложенных эффектов ${getPendingEffectNames()}!"
            )
            pendingEffects.addLast(newEffect)
        }
    }

    @Suppress("MaxLineLength")
    fun postOnReturnSideEffect(newEffect: SideEffect, condition: () -> Boolean) {
        if (pendingEffects.contains(newEffect)) return
        vmName.logStage(
            methodName = "postDelayForReEntrance",
            message = "Эффект ${newEffect.getName()} добавлен, в очередь, отложенных эффектов ${getPendingEffectNames()} и будет выполнен при возврате на экран!"
        )
        if (reEntranceCondition != condition) reEntranceCondition = condition
        pendingEffects.addLast(newEffect)
    }

    fun handle() {
        val handledEffect = stream.value
        if (handledEffect != null) {
            stream.update { null }
            vmName.logStage(
                methodName = "handle",
                message = "Эффект ${handledEffect.getName()} обработан!"
            )
            if (pendingEffects.contains(handledEffect)) {
                vmName.logStage(
                    methodName = "handle",
                    message = "Эффект ${handledEffect.getName()} был удален из очереди ${getPendingEffectNames()}!"
                )
                pendingEffects.remove(handledEffect)
            }
        }
        postPending()
    }

    private fun postPending() {
        if (pendingEffects.isNotEmpty() && stream.value == null) {
            val effect = pendingEffects.pollFirst()
            vmName.logStage(
                methodName = "postPending",
                message = "Отложенный эффект ${effect?.getName()} отправлен!"
            )
            reEntranceCondition = null
            stream.update { effect }
        }
    }

    private fun getPendingEffectNames(): List<String> =
        pendingEffects.mapNotNull { sideEffect -> sideEffect?.let { effect -> effect::class.simpleName } }

    private fun SideEffect?.getName(): String =
        this?.let { effect -> effect::class.java.simpleName } ?: ("Unknown effect")
}