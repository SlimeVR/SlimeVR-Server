package dev.slimevr.updater

import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.table.grid
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.Panel
import com.github.ajalt.mordant.widgets.Text

object TerminalUtil {
	val t = Terminal()

	fun printVersionGrid(versions: List<String>, title: String = "Available Versions") {
		if (versions.isEmpty()) {
			t.println(TextColors.yellow("No versions found."))
			return
		}

		val versionGrid = grid {
			val columnCount = (t.size.width / 15).coerceAtLeast(1).coerceAtMost(6)

			versions.chunked(columnCount).forEach { rowVersions ->
				row {
					rowVersions.forEach { tag ->
						val style = if ("rc" in tag || "beta" in tag) TextColors.yellow else TextColors.cyan
						cell(style(tag))
					}
				}
			}
		}

		t.println(
			Panel(
				content = versionGrid,
				title = Text(bold(title)),
				titleAlign = TextAlign.LEFT,
				borderStyle = TextColors.blue,
			),
		)
	}

	fun success(msg: String) = t.println(TextColors.green(msg))
	fun info(msg: String) = t.println(TextColors.blue(msg))
	fun warn(msg: String) = t.println(TextColors.yellow(msg))
	fun error(msg: String) = t.println(TextColors.red(msg))
}
