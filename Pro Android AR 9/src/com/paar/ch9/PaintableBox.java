package com.paar.ch9;

import android.graphics.Canvas;
import android.graphics.Color;

public class PaintableBox extends PaintableObject {
    private float width=0, height=0;
	private int borderColor = Color.rgb(255, 255, 255);
	private int backgroundColor = Color.argb(128, 0, 0, 0);

	public PaintableBox(float width, float height) {
		this(width, height, Color.rgb(255, 255, 255), Color.argb(128, 0, 0, 0));
	}

	public PaintableBox(float width, float height, int borderColor, int bgColor) {
		set(width, height, borderColor, bgColor);
	}

    public void set(float width, float height) {
        set(width, height, borderColor, backgroundColor);
    }

	public void set(float width, float height, int borderColor, int bgColor) {
	    this.width = width;
	    this.height = height;
	    this.borderColor = borderColor;
		this.backgroundColor = bgColor;
	}

	@Override
	public void paint(Canvas canvas) {
		if (canvas==null) throw new NullPointerException();

		setFill(true);
		setColor(backgroundColor);
		paintRect(canvas, 0, 0, width, height);

		setFill(false);
		setColor(borderColor);
		paintRect(canvas, 0, 0, width, height);
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}
}