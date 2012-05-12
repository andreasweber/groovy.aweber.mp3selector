package groovy.aweber.mp3selector.gui

import java.awt.Graphics
import java.awt.Image
import java.awt.image.BufferedImage

import javax.swing.JPanel
import javax.imageio.ImageIO

/** extended JPanel whose only content is an image. */
class ImagePanel extends JPanel {
	Image _image

	public ImagePanel() {
		setOpaque(false) // let swing repaint the background if there's no image available
	}

	public setImage(Image image) {
		_image = image
		repaint()
	}

	@Override
	public void paintComponent(Graphics g) {
		if (_image == null) {
			super.paintComponents(g)
		}
		// image should fill whole panel
		g.drawImage(_image, 0, 0, getWidth(), getHeight(), null)
	}
}
