/**
 * 
 */
package main.BubbleShooter;

import java.util.Queue;

import BubbleShooter.model.Ball;
import BubbleShooter.model.BallPool;
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
	private boolean running;
	public boolean checkCollision;

	public void setRunning(boolean running) {
		this.running = running;
	}

	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
		super();
		checkCollision = false;
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	@Override
	public void run() {
		Canvas canvas;
		Log.d(TAG, "Starting game loop");

		Queue<Ball> activeBalls = BallPool.getActiveBalls();
		Ball moving = null;
		int dx, dy;
		while (running) {
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {

					if (checkCollision) {
						moving = this.gamePanel.MovingBall;
						for (Ball cur : activeBalls) {
							dx = cur.x - moving.x;
							dy = cur.y - moving.y;
							if (dx * dx + dy * dy <= Ball.radius * Ball.radius) {
								
							}
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
