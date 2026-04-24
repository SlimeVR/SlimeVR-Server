package dev.slimevr.updater

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowScope
import androidx.compose.foundation.window.WindowDraggableArea
import coil3.compose.AsyncImage

val BG = Color(0xFF112D43)
val TEXT = Color.White
val ERROR = Color(0xFFE57373)
val PROGRESS_BG = Color(0xFF081E30)
val PROGRESS_FG = Color(0xFF65459A)

@Composable
fun WindowScope.UpdaterScreen(state: UpdaterState) {
	WindowDraggableArea {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.clip(RoundedCornerShape(12.dp))
				.background(BG)
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {

			// TITLE
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(40.dp)
			) {
				Text(
					text = "SlimeVR Updater",
					color = TEXT,
					fontSize = 20.sp,
					fontWeight = FontWeight.Bold,
					textAlign = TextAlign.Center,
					modifier = Modifier.align(Alignment.Center)
				)
			}

			Box(
				modifier = Modifier.size(160.dp),
				contentAlignment = Alignment.Center
			) {
				LoadingGif()
			}

			// STATUS HEADER
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(40.dp)
			) {
				Text(
					text = state.statusText,
					color = TEXT,
					fontSize = 20.sp,
					fontWeight = FontWeight.Bold,
					textAlign = TextAlign.Center,
					modifier = Modifier.align(Alignment.Center)
				)
			}

			// MAIN PROGRESS
			ProgressBar(
				progress = state.mainProgress,
				color = if (state.errorMessage != null) ERROR else PROGRESS_FG
			)

			// STATUS TEXT
			Text(
				text = state.errorMessage ?: state.statusText,
				color = if (state.errorMessage != null) ERROR else TEXT,
				fontSize = 13.sp,
				textAlign = TextAlign.Center
			)

			// SUB PROGRESS
			ProgressBar(
				progress = state.subProgress,
				color = if (state.errorMessage != null) ERROR else PROGRESS_FG
			)

			Spacer(modifier = Modifier.weight(1f))

			Text(
				text = "v${state.version}",
				color = TEXT.copy(alpha = 0.5f),
				fontSize = 10.sp
			)
		}
	}
}

@Composable
fun LoadingGif() {
	val stream = object {}.javaClass.getResourceAsStream("/jumping-slime.gif")
		?: error("Missing resource: jumping-slime.gif")

	AsyncImage(
		model = stream.readBytes(),
		contentDescription = "Loading animation"
	)
}

@Composable
fun ProgressBar(
	progress: Float,
	color: Color
) {
	val clamped = progress.coerceIn(0f, 1f)

	Canvas(
		modifier = Modifier
			.fillMaxWidth(0.8f)
			.height(12.dp)
	) {
		// background
		drawRoundRect(
			color = PROGRESS_BG,
			size = size,
			cornerRadius = CornerRadius(6.dp.toPx())
		)
		// fill
		drawRoundRect(
			color = color,
			size = Size(size.width * clamped, size.height),
			cornerRadius = CornerRadius(6.dp.toPx())
		)
	}
}
