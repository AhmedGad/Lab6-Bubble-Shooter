package main.BubbleShooter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import BubbleShooter.model.Ball;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();

	public Ball movingBall = null, waitingBall = null;
	public static int width;
	public static int height;
	private MainThread thread;
	public static final int speed = 3;

	public MainGamePanel(Context context, DisplayMetrics displaymetrics) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		width = displaymetrics.widthPixels;
		height = displaymetrics.heightPixels;
		// initialize Ball Pool
		int totalBallNumber = (width * height) / (Ball.radius * Ball.radius)
				* 2;
		for (int i = 0; i < totalBallNumber; i++)
			inactiveBalls.add(new Ball(i));

		waitingBall = inactiveBalls.poll();
		waitingBall.x = width / 2;
		waitingBall.y = height - Ball.radius * 2 - 10;
		waitingBall.color = (int) (Math.random() * Ball.colors.length);

		vis = new boolean[totalBallNumber];
		tmp_ball_arr = new Ball[totalBallNumber];

		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		initLevel(0);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	private void initLevel(int levNum) {
		while (!activeBalls.isEmpty()) {
			Ball tmp = activeBalls.poll();
			inactiveBalls.add(tmp);
		}
		int diam = Ball.radius * 2;
		int maxRows = (height - diam * 5) / diam;
		for (int i = 0; i < Math.min(3 + levNum, maxRows); i++) {
			for (int j = 0; j < width / diam;) {
				int same = levNum > 3 ? 1
						: (int) (Math.random() * (5 - levNum)) + 1, cnt = 0;
				int c = (int) (Math.random() * Ball.colors.length);
				for (; j < width / diam && cnt < same; cnt++, j++) {
					Ball tmp = inactiveBalls.poll();
					tmp.y = i * diam + Ball.radius;
					tmp.x = j * diam + Ball.radius;
					tmp.color = c;
					tmp.ceiled = i == 0 ? true : false;
					activeBalls.add(tmp);
				}
			}
		}
	}

	Queue<Ball> activeBalls = new LinkedList<Ball>();
	Queue<Ball> inactiveBalls = new LinkedList<Ball>();

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && movingBall == null) {
			movingBall = waitingBall;

			int x = (int) event.getX();
			int y = (int) event.getY();
			movingBall.dx = x - movingBall.x;
			movingBall.dy = y - movingBall.y;
			float s2 = (movingBall.dx * movingBall.dx)
					+ (movingBall.dy * movingBall.dy);
			float t2 = speed * speed;
			float h = (float) Math.sqrt(t2 / s2);
			movingBall.dx *= h;
			movingBall.dy *= h;

			waitingBall = inactiveBalls.poll();
			waitingBall.x = width / 2;
			waitingBall.y = height - Ball.radius * 2 - 10;
			waitingBall.color = (int) (Math.random() * Ball.colors.length);
			thread.checkCollision = true;
		}
		return true;
	}

	public void render(Canvas canvas) {
		canvas.drawColor(Color.BLACK);

		if (movingBall != null)
			movingBall.draw(canvas);

		if (waitingBall != null)
			waitingBall.draw(canvas);

		for (Ball ball : activeBalls) {
			ball.draw(canvas);
		}
		Iterator<Ball> it = falling.iterator();
		while (it.hasNext())
			it.next().draw(canvas);
	}

	/**
	 * This is the game update method. It iterates through all the objects and
	 * calls their update method if they have one or calls specific engine's
	 * update method.
	 */
	public void update() {
		if (movingBall != null) {
			// check collision with right wall if heading right
			if (movingBall.dx > 0 && movingBall.x + Ball.radius >= width)
				movingBall.dx *= -1;
			// check collision with left wall if heading left
			if (movingBall.dx < 0 && movingBall.x - Ball.radius <= 0)
				movingBall.dx *= -1;

			// check collision with top wall if heading up
			if (movingBall.dy < 0 && movingBall.y - Ball.radius <= 0)
				movingBall.dy *= -1;

			// check collision with button wall if heading up
			if (movingBall.dy > 0 && movingBall.y - Ball.radius >= height)
				movingBall.dy *= -1;

			// Update the lone droid
			movingBall.x += movingBall.dx;
			movingBall.y += movingBall.dy;
		}

		Iterator<Ball> it = falling.iterator();
		while (it.hasNext()) {
			it.next().fallingMove();
		}
	}

	private Queue<Ball> falling = new LinkedList<Ball>();

	boolean vis[];
	Ball tmp_ball_arr[];
	Queue<Ball> tmp = new LinkedList<Ball>();

	public void checkFalling() {
		Queue<Ball> q = new LinkedList<Ball>();
		float dx, dy;
		int diam = Ball.radius * 2;
		for (Ball ball : activeBalls)
			vis[ball.id] = false;

		// TODO must be deleted and delete ball by ball when falling
		// falling.clear();

		tmp.clear();

		q.add(movingBall);
		vis[movingBall.id] = true;

		while (!q.isEmpty()) {
			Ball cur = q.poll();
			tmp.add(cur);
			for (Ball ball : activeBalls) {
				dx = ball.x - cur.x;
				dy = ball.y - cur.y;
				if (!vis[ball.id] && ball.color == cur.color
						&& dx * dx + dy * dy <= diam * diam) {
					q.add(ball);
					vis[ball.id] = true;
				}

			}
		}

		// same touched colors must be at least 3
		if (tmp.size() < 3) {
			tmp.clear();
			return;
		}

		for (Ball ball : tmp)
			falling.add(ball);

		// remove falling balls from active balls
		// for (Ball ball : falling) activeBalls.remove(ball);

		// another method for deletion(more efficient)
		int cnt = 0;
		for (Ball ball : activeBalls) {
			if (!vis[ball.id])
				tmp_ball_arr[cnt++] = ball;
			vis[ball.id] = false;
		}
		activeBalls.clear();
		for (int i = 0; i < cnt; i++)
			activeBalls.add(tmp_ball_arr[i]);
		// /////////

		for (Ball ball : activeBalls) {

			if (ball.ceiled) {
				q.add(ball);
				vis[ball.id] = true;
			}
		}

		while (!q.isEmpty()) {
			Ball cur = q.poll();

			for (Ball ball : activeBalls) {
				dx = ball.x - cur.x;
				dy = ball.y - cur.y;
				if (!vis[ball.id] && dx * dx + dy * dy <= diam * diam) {
					q.add(ball);
					vis[ball.id] = true;
				}
			}
		}

		// add not visited balls to falling and visited balls will still active
		cnt = 0;

		for (Ball ball : activeBalls)
			if (!vis[ball.id]) {
				falling.add(ball);
			} else {
				tmp_ball_arr[cnt++] = ball;
			}

		activeBalls.clear();
		for (int i = 0; i < cnt; i++)
			activeBalls.add(tmp_ball_arr[i]);

		for (Ball ball : falling)
			if (!ball.isFalling)
				ball.initFall();

	}
}
