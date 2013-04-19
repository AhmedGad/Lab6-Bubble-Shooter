package BubbleShooter.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Ball {
	public int x, y, id, color, dx, dy;
	public static Bitmap bitmap; // the actual bitmap

	public Ball(int id) {
		this.id = id;
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2),
				y - (bitmap.getHeight() / 2), null);
	}

	public static final int radius = 15;
}
