package BubbleShooter.model;

import android.graphics.Color;

public class Ball {
	public Color color;
	public int x, y, id;

	public Ball(int id) {
		this.id = id;
	}

	public static final int radius = 15;
}
