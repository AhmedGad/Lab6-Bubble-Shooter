package bubbleShooter.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class MainActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private static final String TAG = "TAG";
	private MainThread thread;
	DisplayMetrics displaymetrics;
	private Pool pool;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		displaymetrics = new DisplayMetrics();

		Ball.screen_height = displaymetrics.heightPixels;
		Ball.screen_width = displaymetrics.widthPixels;

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

		setContentView(R.layout.menu);

		((Button) findViewById(R.id.newGame)).setOnClickListener(this);
		((Button) findViewById(R.id.exit)).setOnClickListener(this);
		((Button) findViewById(R.id.lev1)).setOnClickListener(this);
		((Button) findViewById(R.id.lev2)).setOnClickListener(this);
		((Button) findViewById(R.id.lev3)).setOnClickListener(this);
		((Button) findViewById(R.id.lev4)).setOnClickListener(this);
		((Button) findViewById(R.id.lev5)).setOnClickListener(this);
		int totalBallNumber = (displaymetrics.widthPixels * displaymetrics.heightPixels)
				/ (Ball.radius * Ball.radius) * 2;
		pool = new Pool(totalBallNumber);

		Log.d(TAG, "View added");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.Main_Menu:
			setContentView(R.layout.menu);
			((Button) findViewById(R.id.newGame)).setOnClickListener(this);
			((Button) findViewById(R.id.exit)).setOnClickListener(this);
			((Button) findViewById(R.id.lev1)).setOnClickListener(this);
			((Button) findViewById(R.id.lev2)).setOnClickListener(this);
			((Button) findViewById(R.id.lev3)).setOnClickListener(this);
			((Button) findViewById(R.id.lev4)).setOnClickListener(this);
			((Button) findViewById(R.id.lev5)).setOnClickListener(this);
			return true;
		case R.id.Exit:
			System.exit(0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.exit)
			System.exit(0);
		Button b = (Button) v;
		String buttonText = b.getText().toString();
		int lev = buttonText.startsWith("New") ? 0 : buttonText
				.charAt(buttonText.length() - 1) - '0';
		System.out.println(lev);
		System.out.println(buttonText);
		pool.init();
		setContentView(new MainGamePanel(this, displaymetrics,
				thread = new MainThread(this), pool, lev));
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
		new AlertDialog.Builder(this)
				.setTitle("Really Exit?")
				.setMessage("Are you sure you want to exit?")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes,
						new android.content.DialogInterface.OnClickListener() {
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