package com.remziakgoz.coffeepomodoro.domain.model

sealed class ResetResult {
    object NoResetNeeded : ResetResult()
    object ResetDone : ResetResult()
}