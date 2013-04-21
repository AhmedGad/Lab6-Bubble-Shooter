/**
 * 
 */
package bubbleShooter.main;

import java.util.Queue;

import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;

/**
 * 
 * 
 * The Main thread which contains the game loop. The thread must have access to
 * the surface view and holder to trigger events every game tick.
 */

public class MainThread extends Thread {

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

	MainActivity c;

	public MainThread(MainActivity c) {
		this.c = c;
	}

	public void init(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
		checkCollision = false;
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	public boolean susbend = false;

	public void myPause() {
		susbend = true;
	}

	public void myResume() {
		susbend = false;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	MediaPlayer player;

	public void audioPlayer() {
		if (player != null)
			player.release();
		player = MediaPlayer.create(c, R.raw.click);
		player.setVolume(100, 100);
		player.start();
	}

	boolean runSound = true;

	public final Object lock = new Object();

	@Override
	public void run() {
		Canvas canvas;
		Queue<Ball> activeBalls = this.gamePanel.pool.activeBalls;
		Ball moving = null;
		float dx, dy;
		int diam = Ball.radius * 2;

		while (running) {
			if (susbend) {
				try {
					synchronized (lock) {
						lock.wait();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {

					if (checkCollision) {
						if (runSound)
							try {
								runSound = false;
								audioPlayer();
							} catch (Exception e) {
							}
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
							int height = canvas.getHeight();
							if (moving.y > height - 6 * Ball.radius
									- Ball.ceil_shift) {
								gamePanel.loose = true;
							}

							activeBalls.add(moving);
							this.gamePanel.checkfalling();
							this.gamePanel.movingBall = null;
							checkCollision = false;
							runSound = true;
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
