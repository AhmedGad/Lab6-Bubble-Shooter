package BubbleShooter.model;

import java.util.LinkedList;
import java.util.Queue;

public class BallPool {
	static private Queue<Ball> inactiveBalls;
	static private Queue<Ball> activeBalls;

	public static void init(int totalBallNumber) {
		inactiveBalls = new LinkedList<Ball>();
		activeBalls = new LinkedList<Ball>();
		for (int i = 0; i < totalBallNumber; i++)
			inactiveBalls.add(new Ball(i));
	}

	public static Ball getNewBall() {
		Ball ret = inactiveBalls.poll();
		return ret;
	}

	public static void release(Ball ball) {
		activeBalls.remove(ball);
		inactiveBalls.add(ball);
	}

	public static Queue<Ball> getActiveBalls() {
		return activeBalls;
	}

	public static Queue<Ball> getInactiveBalls() {
		return inactiveBalls;
	}

}
