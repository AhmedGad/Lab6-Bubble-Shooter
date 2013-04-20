/**
 * 
 */
package bubbleShooter.main;

import java.util.Queue;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * 
 * 
 * The Main thread which contains the game loop. The thread must have access to
 * the surface view and holder to trigger events every game tick.
 */

public class MainThread extends Thread {

	private static final String TAG = MainThread.class.getSimpleName();
	// Surface holder that can access the physical surface
	private SurfaceHolder surfaceHolder;
	// The actual view that handles inputs
	// and draws to the surface
	private MainGamePanel gamePanel;

	// flag to hold game state
	public boolean running;
	public boolean checkCollision;

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void init(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
		checkCollision = false;
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	private boolean pauseNext = false;

	public void myPause() {
		pauseNext = true;
	}

	public void myResume() {
		running = true;
		pauseNext = false;
		run();
	}

	@Override
	public void run() {
		Canvas canvas;
		Log.d(TAG, "Starting game loop");

		Queue<Ball> activeBalls = this.gamePanel.activeBalls;
		Ball moving = null;
		float dx, dy;
		int diam = Ball.radius * 2;
		while (running) {
			if (pauseNext)
				running = false;
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {

					if (checkCollision) {
						moving = this.gamePanel.movingBall;
						moving.ceiled = false;
						boolean stop = false;

						if (Math.abs(moving.y - Ball.radius) < 5) {
							moving.ceiled = true;
							stop = true;
						} else
							for (Ball cur : activeBalls) {
								dx = cur.x - moving.x;
								dy = cur.y - moving.y;
								if (dx * dx + dy * dy <= diam * diam) {
									stop = true;
									break;
								}
							}

						if (stop) {
							activeBalls.add(moving);
							this.gamePanel.checkFalling();
							this.gamePanel.movingBall = null;
							checkCollision = false;
						}
					}

					// update game state
					this.gamePanel.update();
					// render state to the screen
					// draws the canvas on the panel
					this.gamePanel.render(canvas);
				}
			} finally {
				// in case of an exception the surface is not left in
				// an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			} // end finally
		}
	}
}