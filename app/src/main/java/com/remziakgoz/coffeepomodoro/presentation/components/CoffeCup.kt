import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun CoffeeAnimation(innerPadding: PaddingValues, shouldStart: Boolean) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("cup.json"))
    var progress by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(shouldStart, composition) {
        if (shouldStart && composition != null) {
            val totalDuration = 1 * 30 * 1000L
            val startTime = withFrameNanos { it }

            while (progress > 0f) {
                val currentTime = withFrameNanos { it }
                val elapsed = (currentTime - startTime) / 1_000_000L
                progress = 1f - (elapsed.toFloat() / totalDuration)
            }
            progress = 0f
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.size(400.dp)
        )
    }
}