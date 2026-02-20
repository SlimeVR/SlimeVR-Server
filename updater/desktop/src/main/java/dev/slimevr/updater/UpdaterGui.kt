package dev.slimevr.updater

import java.awt.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JProgressBar


class UpdaterGui : Frame() {

	val mainLabel = Label("Installing SlimeVR Drivers", Label.CENTER)
	val subLabel = Label("", Label.CENTER)
	val mainProgressBar: ProgressBar = ProgressBar()
	val subProgressBar: ProgressBar = ProgressBar()

	init {
		isUndecorated = true
		setBackground(Color(17, 45, 67))
		setSize(300, 350)

		val dim = Toolkit.getDefaultToolkit().screenSize
		this.setLocation(
			dim.width / 2 - this.size.width / 2,
			dim.height / 2 - this.size.height / 2,
		)

		mainLabel.foreground = Color.WHITE
		mainLabel.setFont(Font("Serif", Font.PLAIN, 16))
		subLabel.foreground = Color.WHITE

		setLayout(GridBagLayout())
		val gbc = GridBagConstraints()
		gbc.gridx = 0
		gbc.insets = Insets(0, 10, 5, 10)

		val gifLabel = Label("Loading Animation...", Label.CENTER)
		gifLabel.setForeground(Color.WHITE)

		val animatedGif = GifCanvas("curious-slime.gif")

		gbc.gridy = 0
		add(mainLabel, gbc)
		gbc.gridy = 1
		add(animatedGif, gbc)
		gbc.gridy = 2
		gbc.fill = GridBagConstraints.HORIZONTAL
		add(mainProgressBar, gbc)
		gbc.gridy = 3
		add(subLabel, gbc)
		gbc.gridy = 4
		add(subProgressBar, gbc)

		isVisible = true
	}
}

class GifCanvas(path: String?) : Canvas() {
	private val img: Image? = Toolkit.getDefaultToolkit().getImage(path)
	private val scaledImage: Image? = img?.getScaledInstance(150, 150, Image.SCALE_DEFAULT)
	private var offscreen: Image? = null
	private var offscreenG: Graphics? = null

	init {
		prepareImage(img, this)
		val tracker = MediaTracker(this)
		tracker.addImage(img, 0)
		try {
			tracker.waitForID(0)
		} catch (e: Exception) {

		}

	}

	override fun update(g: Graphics) {
		paint(g)
	}

	override fun paint(g: Graphics) {
		val w = getWidth()
		val h = getHeight()
		offscreen = createImage(width, height)
		offscreenG = offscreen?.graphics

		offscreenG?.color = Color(17, 45, 67, 255)
		offscreenG?.fillRect(0, 0, w, h)

		if (scaledImage != null) {
			val iw = scaledImage.getWidth(this)
			val ih = scaledImage.getHeight(this)
			if (iw > 0 && ih > 0) {
				offscreenG?.drawImage(scaledImage, (w - iw) / 2, (h - ih) / 2, this)
			}
		}

		g.drawImage(offscreen, 0, 0, this)
	}

	override fun imageUpdate(
		img: Image?,
		infoflags: Int,
		x: Int,
		y: Int,
		w: Int,
		h: Int,
	): Boolean {
		if ((infoflags and (FRAMEBITS or ALLBITS)) != 0) {
			repaint()
		}
		return (infoflags and (ALLBITS or ABORT or ERROR)) == 0
	}

	override fun getPreferredSize(): Dimension = Dimension(200, 200)
}

class ProgressBar() : Canvas() {
	var currentProgress: Int = 0
	val progressBarWidth = 200

	fun setProgress(newProgress: Int) {
		currentProgress = ((newProgress) * (progressBarWidth) / 100)
		repaint()
	}
	init {
		setSize(200, 15)
	}

	override fun paint(g: Graphics) {
		// Background
		g.color = Color(8, 30, 48)
		g.fillRoundRect(0, 0, progressBarWidth, 10, 5, 5)

		// Progress
		g.color = Color(101, 69, 154)
		g.fillRoundRect(0, 0, currentProgress, 10, 5, 5) // Static 60% for now
	}
}
