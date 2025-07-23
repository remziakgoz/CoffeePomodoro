package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

@Composable
fun SwipeGestureHandler(
    onSwipeLeft: () -> Unit = {},  // Sağdan sola kaydırma
    onSwipeRight: () -> Unit = {}, // Soldan sağa kaydırma
    swipeThreshold: Float = 100f,  // Minimum kaydırma mesafesi
    content: @Composable () -> Unit
) {
    var startOffset by remember { mutableStateOf(Offset.Zero) }
    var endOffset by remember { mutableStateOf(Offset.Zero) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        startOffset = offset
                    },
                    onDragEnd = {
                        val deltaX = endOffset.x - startOffset.x
                        val deltaY = endOffset.y - startOffset.y
                        
                        // Yatay kaydırma dikey kaydırmadan fazla olmalı
                        if (abs(deltaX) > abs(deltaY) && abs(deltaX) > swipeThreshold) {
                            when {
                                deltaX > 0 -> onSwipeRight() // Soldan sağa
                                deltaX < 0 -> onSwipeLeft()  // Sağdan sola
                            }
                        }
                        
                        // Reset
                        startOffset = Offset.Zero
                        endOffset = Offset.Zero
                    },
                    onDrag = { _, dragAmount ->
                        endOffset += dragAmount
                    }
                )
            }
    ) {
        content()
    }
} 