package main.BubbleShooter;

import java.util.Arrays;

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

	public Ball MovingBall = null;
	private static final String TAG = MainGamePanel.class.getSimpleName();
	public DisJoint allConnectedSets, sameColorSets;

	private MainThread thread;
	private Droid droid;

	public MainGamePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// initialize Ball Pool
		int totalBallNumber = (getWidth() * getHeight())
				/ (Ball.radius * Ball.radius) + 20;
		BallPool.init(totalBallNumber);

		// initialize disjoint sets
		allConnectedSets = new DisJoint(totalBallNumber);
		sameColorSets = new DisJoint(totalBallNumber);

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

	public static class DisJoint {
		int parent[];

		public DisJoint(int V) {
			parent = new int[V];
			for (int i = 0; i < parent.length; i++)
				parent[i] = i;
		}

		private boolean isConnected(int from, int to) {
			return findParent(from) == findParent(to);
		}

		private int findParent(int index) {
			if (parent[index] == index)
				return index;
			else
				return parent[index] = findParent(parent[index]);
		}

		private void union(int from, int to) {
			parent[findParent(from)] = findParent(to);
		}

		private int numOfStes() {
			// can be implemented using boolean array
			int res = 0;
			for (int i = 0; i < parent.length; i++)
				if (parent[i] == i)
					res++;
			return res;
		}

		@Override
		public String toString() {
			return Arrays.toString(parent);
		}
	}

}
