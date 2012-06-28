package com.paar.ch9;

import android.graphics.Canvas;

public class PaintablePoint extends PaintableObject {
    private static int width=2;
    private static int height=2;
    private int color = 0;
    private boolean fill = false;
    
    public PaintablePoint(int color, boolean fill) {
    	set(color, fill);
    }

    public void set(int color, boolean fill) {
        this.color = color;
        this.fill = fill;
    }

	@Override
    public void paint(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();
    	
        setFill(fill);
        setColor(color);
        paintRect(canvas, -1, -1, width, height);
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