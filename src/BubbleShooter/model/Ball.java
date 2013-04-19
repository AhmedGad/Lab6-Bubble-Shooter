package BubbleShooter.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
	public float x, y, dx, dy;
	public int id, color;
	public static Bitmap bitmap; // the actual bitmap
	private int fx, fy, dirx, diry, type, phase;
	private static final int d = 20;
	private static final int fallingSpeed = 4;
	public boolean ceiled;

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
		double cosAlpha = fallingSpeed * 1.0 / (2.0 * d);
		dx = (float) (fallingSpeed * cosAlpha);
		dy = (float) (Math.sqrt(4.0 * d * d - fallingSpeed * fallingSpeed) / (2.0 * d))
				* (fallingSpeed);
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
			x += dx * dirx;
			y += dy * diry;
			if (type == 1 && x >= fx && diry == -1) {
				diry *= -1;
			}
			if (type == 2 && x <= fx && diry == -1) {
				diry *= -1;
			}
			if (y > fy)
				phase = 2;
		} else {
			y += fallingSpeed;
		}
	}

	public static final int radius = 15;
}
