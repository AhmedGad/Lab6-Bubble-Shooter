package BubbleShooter.model;

import main.BubbleShooter.MainActivity;
import main.BubbleShooter.MainGamePanel;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
	public float x, y, dx, dy;
	public int id, color;
	public static Bitmap bitmap; // the actual bitmap
	private int fx, fy, type, phase;
	private double theta;
	private int diameterOfFall;
	private static final int fallingSpeed = 4;
	public boolean ceiled;
	public boolean isFalling = false;

	public Ball(int id) {
		isFalling = false;
		diameterOfFall = (int) (Math.random() * 70.0 + 30);
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
		isFalling = true;
		theta = Math.acos((2.0 * diameterOfFall * diameterOfFall - fallingSpeed
				* fallingSpeed)
				/ (2 * diameterOfFall * diameterOfFall));
		fy = (int) y;
		if (Math.random() > 0.5) {
			type = 1;
			fx = (int) x + diameterOfFall;
		} else {
			type = 2;
			fx = (int) x - diameterOfFall;
		}
		phase = 1;
	}

	public boolean fallingMove() {
		if (phase == 1) {
			double tx, ty;
			if (type == 1) {
				tx = Math.cos(theta) * (x - fx) - Math.sin(theta) * (y - fy)
						+ fx;
				ty = Math.sin(theta) * (x - fx) + Math.cos(theta) * (y - fy)
						+ fy;
			} else {
				tx = Math.cos(-theta) * (x - fx) - Math.sin(-theta) * (y - fy)
						+ fx;
				ty = Math.sin(-theta) * (x - fx) + Math.cos(-theta) * (y - fy)
						+ fy;
			}
			x = (float) tx;
			y = (float) ty;
			if (y > fy)
				phase = 2;
			return false;
		} else {
			y += fallingSpeed;
			if (y > MainGamePanel.height + radius)
				return true;
			return false;
		}
	}

	public static final int radius = 15;
}
