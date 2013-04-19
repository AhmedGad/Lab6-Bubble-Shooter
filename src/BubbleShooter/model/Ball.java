package BubbleShooter.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Ball {
	public float x, y, dx, dy;
	public int id, color;
	public static Bitmap bitmap; // the actual bitmap
	private int fx, fy, dirx, diry, type, phase;
	private static final int d = 20;
	private static final int s = 8;

	public Ball(int id) {
		this.id = id;
	}

	static Paint paint = new Paint();
	public static int colors[] = { Color.YELLOW, Color.GREEN, Color.RED,
			Color.CYAN, Color.WHITE, Color.BLUE, Color.GRAY };

	public void draw(Canvas canvas) {
		// canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2),
		// y - (bitmap.getHeight() / 2), null);
		paint.setColor(colors[color]);
		canvas.drawCircle(x, y, radius, paint);
	}

	public void initFall() {
		double cosAlpha = s * 1.0 / (2.0 * d);
		dx = (float) (s * cosAlpha);
		dy = (float) (Math.sqrt(4.0 * d * d - s * s) / (2.0 * d)) * (s);
		fy = (int) y;
		if (Math.random() > 0.5) {
			type = 1;
			dirx = 1;
			fx = (int) x + d;
		} else {
			type = 2;
			dirx = -1;
			fx = (int) x - d;
		}
		phase = 1;
		diry = -1;
	}

	public void fallingMove() {
		if (phase == 1) {
			x += dx*dirx;
			y += dy*diry;
			if (type == 1 && x >= fx && diry == -1) {
				diry *= -1;
			}
			if (type == 2 && x <= fx && diry == -1) {
				diry *= -1;
			}
			if (y > fy)
				phase = 2;
		} else {
			y += s;
		}
	}

	public static final int radius = 15;
}
