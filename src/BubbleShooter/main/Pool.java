package bubbleShooter.main;

import java.util.LinkedList;
import java.util.Queue;

public class Pool {
	public Queue<Ball> activeBalls = new LinkedList<Ball>();
	public Queue<Ball> BallPool = new LinkedList<Ball>();
	public Queue<Ball> falling = new LinkedList<Ball>();

	public boolean vis[];
	public Ball tmp_ball_arr[];

	public Pool(int totalNumber) {
		for (int i = 0; i < totalNumber; i++)
			BallPool.add(new Ball(i));

		vis = new boolean[totalNumber];
		tmp_ball_arr = new Ball[totalNumber];
	}

	public void init() {
		while (!falling.isEmpty())
			BallPool.add(falling.poll());
		while (!activeBalls.isEmpty())
			BallPool.add(activeBalls.poll());
	}
}
