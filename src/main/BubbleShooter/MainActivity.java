package main.BubbleShooter;

import BubbleShooter.model.Ball;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

import android.view.WindowManager;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */

	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		// requesting to turn the title OFF
		Ball.bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.droid_1);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		// set our MainGamePanel as the View
		setContentView(new MainGamePanel(this, displaymetrics));
		Log.d(TAG, "View added");
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");
		super.onStop();
	}

}