package dev.slimevr.updater

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.platform.Font
import org.jetbrains.compose.animatedimage.AnimatedImage
import org.jetbrains.compose.animatedimage.animate
import org.jetbrains.compose.animatedimage.loadResourceAnimatedImage

val BG = Color(0xFF112D43)
val TEXT = Color.White
val ERROR = Color(0xFFE57373)
val BUTTON_PRIMARY = Color(0xFF65459A)
val BUTTON_SECONDARY = Color(0xFF112D43)
val PROGRESS_BG = Color(0xFF081E30)
val PROGRESS_FG = Color(0xFF65459A)

val BUTTON_BORDER_RADIUS = 6.dp

val PoppinsFontFamily = FontFamily(
	Font(
		resource = "fonts/poppins-latin-500-normal.woff2",
		weight = FontWeight.Normal,
		style = FontStyle.Normal
	),
	Font(
		resource = "fonts/poppins-latin-700-normal.woff2",
		weight = FontWeight.Bold,
		style = FontStyle.Normal
	)
)

val BaseTextStyle = TextStyle(
	fontFamily = PoppinsFontFamily,
	color = TEXT
)

@Composable
fun WindowScope.UpdaterScreen(state: UpdaterState, updaterIO: UpdaterIO) {
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
						text = "SlimeVR Installer",
						style = BaseTextStyle.copy(
							fontSize = 20.sp,
							fontWeight = FontWeight.Bold,
							textAlign = TextAlign.Center
						),
						modifier = Modifier.align(Alignment.Center)
					)
				}
				if (state.hasError) {
					ErrorGif(modifier = Modifier.size(160.dp))

					Text(
						text = "Whoops, something went wrong",
						style = BaseTextStyle.copy(
							fontSize = 20.sp,
							fontWeight = FontWeight.Bold,
							textAlign = TextAlign.Center
						)
					)

					Text(
						text = state.errorText,
						style = BaseTextStyle.copy(
							fontSize = 13.sp,
							textAlign = TextAlign.Center
						),
						modifier = Modifier.padding(bottom = 8.dp)
					)

					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(8.dp)
					) {
						val interactionSource = remember { MutableInteractionSource() }
						val isHovered by interactionSource.collectIsHoveredAsState()
						val animatedButtonColor by
						animateColorAsState(
							targetValue = if (isHovered) BUTTON_PRIMARY.copy(alpha = 0.85f) else BUTTON_PRIMARY,
							animationSpec = tween(durationMillis = 200)
						)
						Button(
							onClick = {
								state.hasError = false
								updaterIO.restartApplication()
							},
							shape = RoundedCornerShape(BUTTON_BORDER_RADIUS),
							colors = ButtonDefaults.buttonColors(
								backgroundColor = animatedButtonColor,
								contentColor = TEXT
							),
							modifier = Modifier.fillMaxWidth(0.5f)
						) {
							Text(
								text = "Try update again",
								style = BaseTextStyle.copy(fontWeight = FontWeight.Normal)
							)
						}

						val secInteractionSource = remember { MutableInteractionSource() }
						val isSecHovered by secInteractionSource.collectIsHoveredAsState()

						val animatedSecColor by animateColorAsState(
							targetValue = if (isSecHovered) Color(0xFF1C4566) else BUTTON_SECONDARY,
							animationSpec = tween(durationMillis = 200)
						)
						Button(
							onClick = { /* ... */ },
							shape = RoundedCornerShape(6.dp),
							interactionSource = secInteractionSource,
							colors = ButtonDefaults.buttonColors(backgroundColor = animatedSecColor),
							modifier = Modifier
								.fillMaxWidth(0.5f)
						) {
							Text(
								text = "Start server anyway",
								style = BaseTextStyle.copy(fontWeight = FontWeight.Normal)
							)
						}
					}
				} else {
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
					color = PROGRESS_FG,
					isVisible = state.mainProgressisVisible
				)
				// STATUS TEXT
				Text(
					text = state.subText ?: state.statusText,
					color = TEXT,
					fontSize = 13.sp,
					textAlign = TextAlign.Center
				)
				// SUB PROGRESS
				ProgressBar(
					progress = state.subProgress,
					color = PROGRESS_FG,
					isVisible = state.subProgressisVisible
				)

				Spacer(modifier = Modifier.weight(1f))

				Text(
					text = state.versionTag,
					color = TEXT.copy(alpha = 0.5f),
					fontSize = 10.sp
				)
			}
		}
	}
}

@Composable
fun LoadingGif(modifier: Modifier = Modifier) {
	val animatedImageState = produceState<AnimatedImage?>(initialValue = null) {
		try {
			value = loadResourceAnimatedImage("jumping-slime.gif")
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	val animatedImage = animatedImageState.value

	if (animatedImage != null) {
		Image(
			bitmap = animatedImage.animate(),
			contentDescription = "Loading animation",
			modifier = modifier
		)
	} else {
		Box(modifier = modifier)
	}
}

@Composable
fun ErrorGif(modifier: Modifier = Modifier) {
	val animatedImageState = produceState<AnimatedImage?>(initialValue = null) {
		try {
			value = loadResourceAnimatedImage("curious-slime.gif")
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	val animatedImage = animatedImageState.value

	if (animatedImage != null) {
		Image(
			bitmap = animatedImage.animate(),
			contentDescription = "Error animation",
			modifier = modifier
		)
	} else {
		Box(modifier = modifier)
	}
}

@Composable
fun ProgressBar(
	progress: Float,
	color: Color,
	isVisible: Boolean = false
) {
	val clamped = progress.coerceIn(0f, 1f)

	AnimatedVisibility(
		visible = isVisible,
		enter = fadeIn(),
		exit = fadeOut()
	) {
		Canvas(
			modifier = Modifier
				.fillMaxWidth(0.8f)
				.height(12.dp)
		) {
			// Background
			drawRoundRect(
				color = PROGRESS_BG,
				size = size,
				cornerRadius = CornerRadius(6.dp.toPx())
			)
			// Fill
			drawRoundRect(
				color = color,
				size = Size(size.width * clamped, size.height),
				cornerRadius = CornerRadius(6.dp.toPx())
			)
		}
	}
}
