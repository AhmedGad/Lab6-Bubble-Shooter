package main.BubbleShooter;

import java.util.LinkedList;
import java.util.Queue;

import BubbleShooter.model.Ball;
import BubbleShooter.model.BallPool;
import BubbleShooter.model.Droid;
import BubbleShooter.model.components.Speed;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();

	public Ball MovingBall = null;
	private int ceil_shift;

	private MainThread thread;
	private Droid droid;

	public MainGamePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		ceil_shift = 0;

		// initialize Ball Pool
		int totalBallNumber = (getWidth() * getHeight())
				/ (Ball.radius * Ball.radius) * 2;
		BallPool.init(totalBallNumber);
		this.activeBalls = BallPool.getActiveBalls();

		vis = new boolean[totalBallNumber];
		tmp_ball_arr = new Ball[totalBallNumber];

		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		Log.i("GAD", BallPool.getInactiveBalls().size() + "");
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
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// delegating event handling to the droid
			droid.handleActionDown((int) event.getX(), (int) event.getY());

			// check if in the lower part of the screen we exit
			if (event.getY() > getHeight() - 50) {
				thread.setRunning(false);
				((Activity) getContext()).finish();
			} else {
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// the gestures
			if (droid.isTouched()) {
				// the droid was picked up and is being dragged
				droid.setX((int) event.getX());
				droid.setY((int) event.getY());
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// touch was released
			if (droid.isTouched()) {
				droid.setTouched(false);
			}
		}
		return true;
	}

	public void render(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		droid.draw(canvas);
	}

	/**
	 * This is the game update method. It iterates through all the objects and
	 * calls their update method if they have one or calls specific engine's
	 * update method.
	 */
	public void update() {
		// check collision with right wall if heading right
		if (droid.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
				&& droid.getX() + droid.getBitmap().getWidth() / 2 >= getWidth()) {
			droid.getSpeed().toggleXDirection();
		}
		// check collision with left wall if heading left
		if (droid.getSpeed().getxDirection() == Speed.DIRECTION_LEFT
				&& droid.getX() - droid.getBitmap().getWidth() / 2 <= 0) {
			droid.getSpeed().toggleXDirection();
		}
		// check collision with bottom wall if heading down
		if (droid.getSpeed().getyDirection() == Speed.DIRECTION_DOWN
				&& droid.getY() + droid.getBitmap().getHeight() / 2 >= getHeight()) {
			droid.getSpeed().toggleYDirection();
		}
		// check collision with top wall if heading up
		if (droid.getSpeed().getyDirection() == Speed.DIRECTION_UP
				&& droid.getY() - droid.getBitmap().getHeight() / 2 <= 0) {
			droid.getSpeed().toggleYDirection();
		}
		// Update the lone droid
		droid.update();
	}

	private Queue<Ball> falling = new LinkedList<Ball>();
	private Queue<Ball> activeBalls;
	boolean vis[];
	Ball tmp_ball_arr[];

	public void checkFalling() {
		Queue<Ball> q = new LinkedList<Ball>();
		int dx, dy;

		for (Ball ball : activeBalls)
			vis[ball.id] = false;

		q.add(MovingBall);
		vis[MovingBall.id] = true;

		while (!q.isEmpty()) {
			Ball cur = q.poll();
			falling.add(cur);

			for (Ball ball : activeBalls) {
				dx = ball.x - cur.x;
				dy = ball.y - cur.y;
				if (!vis[ball.id] && ball.color == cur.color
						&& dx * dx + dy * dy <= Ball.radius * Ball.radius) {
					q.add(ball);
					vis[ball.id] = true;
				}

			}
		}

		// same touched colors must be at least 3
		if (falling.size() < 3) {
			falling.clear();
			return;
		}

		// remove falling balls from active balls
		// for (Ball ball : falling) activeBalls.remove(ball);

		// another method for deletion(more efficient)
		int cnt = 0;
		for (Ball ball : activeBalls)
			if (!vis[ball.id])
				tmp_ball_arr[cnt++] = ball;

		activeBalls.clear();
		for (int i = 0; i < cnt; i++)
			activeBalls.add(tmp_ball_arr[i]);
		// /////////

		for (Ball ball : activeBalls) {
			vis[ball.id] = false;
			if (ball.y == Ball.radius + ceil_shift) {
				q.add(ball);
				vis[ball.id] = true;
			}
		}

		while (!q.isEmpty()) {
			Ball cur = q.poll();

			for (Ball ball : activeBalls) {
				dx = ball.x - cur.x;
				dy = ball.y - cur.y;
				if (!vis[ball.id] && ball.color == cur.color
						&& dx * dx + dy * dy <= Ball.radius * Ball.radius) {
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
	}
}
