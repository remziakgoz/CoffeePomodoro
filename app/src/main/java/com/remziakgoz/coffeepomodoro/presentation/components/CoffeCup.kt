import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@Composable
fun CoffeeAnimation(modifier: Modifier = Modifier, innerPadding: PaddingValues, shouldStart: Boolean) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("cup-colored-border-light.json"))
    var progress by remember { mutableFloatStateOf(1f) }
    var pausedTime by remember { mutableLongStateOf(0L) }
    val totalDuration = 10_000L

    LaunchedEffect(shouldStart) {
        if (shouldStart && composition != null) {
            val startTime = System.currentTimeMillis() - pausedTime

            while (shouldStart && progress > 0f) {
                val currentTime = System.currentTimeMillis()
                val elapsed = currentTime - startTime

                progress = 1f - (elapsed.toFloat() / totalDuration)

                if (progress <= 0f) {
                    progress = 0f
                    pausedTime = 0L
                    break
                }
                delay(16)
            }
        } else if (!shouldStart && progress > 0f) {
            pausedTime = ((1f - progress) * totalDuration).toLong()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.size(400.dp)
        )
    }
}