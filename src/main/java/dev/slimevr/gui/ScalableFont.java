package dev.slimevr.gui;

import java.awt.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;


public class ScalableFont extends Font {

	protected float scale = 1.0f;

	protected int initSize;
	protected float initPointSize;

	public ScalableFont(Map<? extends Attribute, ?> attributes) {
		super(attributes);

		this.initSize = this.size;
		this.initPointSize = this.pointSize;
	}

	public ScalableFont(Font font) {
		super(font);

		if (font instanceof ScalableFont) {
			ScalableFont sourceFont = (ScalableFont) font;

			this.initSize = sourceFont.getInitSize();
			this.initPointSize = sourceFont.getInitSize2D();

			this.size = this.initSize;
			this.pointSize = this.initPointSize;
		} else {
			this.initSize = this.size;
			this.initPointSize = this.pointSize;
		}
	}

	public ScalableFont(Font font, float scale) {
		super(font);

		if (font instanceof ScalableFont) {
			ScalableFont sourceFont = (ScalableFont) font;

			this.initSize = sourceFont.getInitSize();
			this.initPointSize = sourceFont.getInitSize2D();
		} else {
			this.initSize = this.size;
			this.initPointSize = this.pointSize;
		}

		setScale(scale);
	}

	public ScalableFont(String name, int style, int size) {
		super(name, style, size);

		this.initSize = this.size;
		this.initPointSize = this.pointSize;
	}

	public ScalableFont(String name, int style, int size, float scale) {
		super(name, style, size);

		this.initSize = this.size;
		this.initPointSize = this.pointSize;

		setScale(scale);
	}

	public int getInitSize() {
		return initSize;
	}

	public float getInitSize2D() {
		return initPointSize;
	}

	public float getScale() {
		return scale;
	}

	private void setScale(float scale) {
		this.scale = scale;

		float newPointSize = initPointSize * scale;

		this.size = (int) (newPointSize + 0.5);
		this.pointSize = newPointSize;
	}
}
