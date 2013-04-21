package bubbleShooter.main;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();
	public int score;
	public Ball movingBall = null, waitingBall = null;
	public static int width;
	public static int height;
	private MainThread thread;
	public static final int speed = 3;
	Pool pool;

	public boolean loose;

	public MainGamePanel(MainActivity context, DisplayMetrics displaymetrics,
			MainThread thread, Pool pool, int lev) {
		super(context);
		this.lev = lev;
		loose = false;
		this.pool = pool;
		score = 0;
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		width = displaymetrics.widthPixels;
		height = displaymetrics.heightPixels;
		// initialize Ball Pool

		waitingBall = pool.BallPool.poll();
		waitingBall.x = width / 2;
		waitingBall.y = height - Ball.radius * 2 - 10 - Ball.ceil_shift;
		waitingBall.color = (int) (Math.random() * Ball.colors.length);

		paint.setTextSize(50 * thread.c.displaymetrics.density);
		paint.setColor(Color.BLACK);

		this.thread = thread;
		// create the game loop thread
		thread.init(getHolder(), this);

		initLevel(lev);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	private void initLevel(int levNum) {
		while (!pool.activeBalls.isEmpty()) {
			Ball tmp = pool.activeBalls.poll();
			pool.BallPool.add(tmp);
		}
		int diam = Ball.radius * 2;
		int maxRows = (height - diam * 6 - Ball.ceil_shift) / diam;
		for (int i = 0; i < Math.min(3 + levNum, maxRows); i++) {
			for (int j = 0; j < width / diam;) {
				int same = levNum > 3 ? 1
						: (int) (Math.random() * (5 - levNum)) + 1, cnt = 0;
				int c = (int) (Math.random() * Ball.colors.length);
				for (; j < width / diam && cnt < same; cnt++, j++) {
					Ball tmp = pool.BallPool.poll();
					tmp.y = i * diam + Ball.radius;
					tmp.x = j * diam + Ball.radius;
					tmp.color = c;
					tmp.ceiled = i == 0 ? true : false;
					pool.activeBalls.add(tmp);
				}
			}
		}
	}

	private boolean first = false;

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
			thread.running = false;
			try {
				synchronized (thread.lock) {
					thread.lock.notifyAll();
				}
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
		if (event.getAction() == MotionEvent.ACTION_DOWN && movingBall == null
				&& !first) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			if (y > height - 4 * Ball.radius - Ball.ceil_shift)
				return true;
			movingBall = waitingBall;

			movingBall.dx = x - movingBall.x;
			movingBall.dy = y - movingBall.y;
			float s2 = (movingBall.dx * movingBall.dx)
					+ (movingBall.dy * movingBall.dy);
			float t2 = speed * speed;
			float h = (float) Math.sqrt(t2 / s2);
			movingBall.dx *= h;
			movingBall.dy *= h;

			waitingBall = pool.BallPool.poll();
			waitingBall.x = width / 2;
			waitingBall.y = height - Ball.radius * 2 - 10 - Ball.ceil_shift;
			waitingBall.color = (int) (Math.random() * Ball.colors.length);
			thread.checkCollision = true;
		}
		if (thread.susbend) {
			thread.myResume();
			first = false;
		}
		return true;
	}

	Paint paint = new Paint();
	private int lev;

	public void render(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, width, Ball.ceil_shift, paint);
		paint.setColor(Color.RED);
		canvas.drawText("SCORE: " + score + " level: " + (lev + 1), 5,
				Ball.ceil_shift - 10, paint);

		if (loose) {
			paint.setColor(Color.BLACK);
			canvas.drawColor(Color.RED);
			canvas.drawText("YOU LOSE!!!", 40, height / 2, paint);
			thread.running = false;
		} else {

			if (movingBall != null)
				movingBall.draw(canvas);

			if (waitingBall != null)
				waitingBall.draw(canvas);

			for (Ball ball : pool.activeBalls) {
				ball.draw(canvas);
			}
			Iterator<Ball> it = pool.falling.iterator();
			while (it.hasNext())
				it.next().draw(canvas);

			if (pool.falling.isEmpty() && movingBall == null) {
				thread.susbend = true;
				if (score > 10) {
					canvas.drawColor(Color.CYAN);
					paint.setColor(Color.BLACK);
					canvas.drawText("YOU WIN!!!", 40, height / 2, paint);
					first = true;
					pool.init();
					initLevel(++lev);
					score = 0;
				}
			}
		}
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

		int cnt = 0;

		Iterator<Ball> it = pool.falling.iterator();
		while (it.hasNext()) {
			Ball b = it.next();
			if (!b.fallingMove()) {
				// if still inside add it again !
				pool.tmp_ball_arr[cnt++] = b;
			} else
				pool.BallPool.add(b);
		}
		pool.falling.clear();
		for (int i = 0; i < cnt; i++)
			pool.falling.add(pool.tmp_ball_arr[i]);

	}

	public void checkfalling() {
		Queue<Ball> q = new LinkedList<Ball>();
		float dx, dy;
		int diam = Ball.radius * 2;
		for (Ball ball : pool.activeBalls)
			pool.vis[ball.id] = false;

		// TODO must be deleted and delete ball by ball when pool.falling
		// pool.falling.clear();

		int cnt = 0;

		q.add(movingBall);
		pool.vis[movingBall.id] = true;

		while (!q.isEmpty()) {
			Ball cur = q.poll();
			pool.tmp_ball_arr[cnt++] = cur;
			for (Ball ball : pool.activeBalls) {
				dx = ball.x - cur.x;
				dy = ball.y - cur.y;
				if (!pool.vis[ball.id] && ball.color == cur.color
						&& dx * dx + dy * dy <= diam * diam) {
					q.add(ball);
					pool.vis[ball.id] = true;
				}

			}
		}

		// same touched colors must be at least 3
		if (cnt < 3)
			return;

		score += cnt;

		for (int i = 0; i < cnt; i++)
			pool.falling.add(pool.tmp_ball_arr[i]);

		// remove pool.falling balls from active balls
		// for (Ball ball : pool.falling) pool.activeBalls.remove(ball);

		// another method for deletion(more efficient)
		cnt = 0;
		for (Ball ball : pool.activeBalls) {
			if (!pool.vis[ball.id])
				pool.tmp_ball_arr[cnt++] = ball;
			pool.vis[ball.id] = false;
		}
		pool.activeBalls.clear();
		for (int i = 0; i < cnt; i++)
			pool.activeBalls.add(pool.tmp_ball_arr[i]);
		// /////////

		for (Ball ball : pool.activeBalls) {

			if (ball.ceiled) {
				q.add(ball);
				pool.vis[ball.id] = true;
			}
		}

		while (!q.isEmpty()) {
			Ball cur = q.poll();

			for (Ball ball : pool.activeBalls) {
				dx = ball.x - cur.x;
				dy = ball.y - cur.y;
				if (!pool.vis[ball.id] && dx * dx + dy * dy <= diam * diam) {
					q.add(ball);
					pool.vis[ball.id] = true;
				}
			}
		}

		// add not pool.visited balls to pool.falling and pool.visited balls
		// will
		// still active
		cnt = 0;

		for (Ball ball : pool.activeBalls)
			if (!pool.vis[ball.id]) {
				pool.falling.add(ball);
				score++;
			} else {
				pool.tmp_ball_arr[cnt++] = ball;
			}

		pool.activeBalls.clear();
		for (int i = 0; i < cnt; i++)
			pool.activeBalls.add(pool.tmp_ball_arr[i]);

		for (Ball ball : pool.falling)
			if (!ball.isFalling)
				ball.initFall();

	}
}
