package bubbleShooter.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class MainActivity extends Activity {
	/** Called when the activity is first created. */

	private static final String TAG = "TAG";
	private MainThread thread;

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
		setContentView(new MainGamePanel(this, displaymetrics,
				thread = new MainThread()));

		Log.d(TAG, "View added");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Destroying...");
	}

	@Override
	protected void onPause() {
		super.onPause();
		thread.running = false;
		Log.d(TAG, "pausing...");
	}

	boolean first = true;

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "resuming...");
		if (first) {
			first = false;
		} else {
			thread.running = true;
			thread.run();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this).setTitle("Really Exit?")
				.setMessage("Are you sure you want to exit?")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						MainActivity.super.onBackPressed();
					}
				}).create().show();

		Log.d(TAG, "back pressed...");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "restart...");
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		Log.d(TAG, "user leave...");
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		Log.d(TAG, "user leave...");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "Stopping...");
	}

}