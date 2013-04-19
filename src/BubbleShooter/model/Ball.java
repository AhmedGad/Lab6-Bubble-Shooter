package BubbleShooter.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
	public float x, y, dx, dy;
	public int id, color;
	public static Bitmap bitmap; // the actual bitmap

	public Ball(int id) {
		this.id = id;
	}

	static Paint paint = new Paint();
	public static int colors[] = { Color.YELLOW, Color.GREEN, Color.RED, Color.CYAN,
			Color.WHITE, Color.BLUE, Color.GRAY };

	public void draw(Canvas canvas) {
		// canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2),
		// y - (bitmap.getHeight() / 2), null);
		paint.setColor(colors[color]);
		canvas.drawCircle(x, y, radius, paint);
	}

	public static final int radius = 15;
}
