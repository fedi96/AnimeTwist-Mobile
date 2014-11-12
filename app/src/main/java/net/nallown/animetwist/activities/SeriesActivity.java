package net.nallown.animetwist.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import net.nallown.animetwist.R;
import net.nallown.animetwist.VideoControllerView;
import net.nallown.animetwist.at.User;
import net.nallown.animetwist.at.series.Series;
import net.nallown.animetwist.at.series.SeriesFetcher;
import net.nallown.utils.UIUtils;

import java.io.IOException;

public class SeriesActivity extends Activity
		implements SurfaceHolder.Callback,
		MediaPlayer.OnPreparedListener,
		VideoControllerView.MediaPlayerControl,
		View.OnSystemUiVisibilityChangeListener,
		SeriesFetcher.RequestStates {
	private boolean playerPrepared = false;
	private boolean resume;

	ActionBar actionBar;
	SurfaceView surfaceView;
	MediaPlayer player;
	VideoControllerView controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = getIntent().getExtras();
		String folderName = data.getString("folder_name");

		actionBar = getActionBar();

		SeriesFetcher seriesFetcher = new SeriesFetcher(folderName);
		seriesFetcher.setRequestStates(this);
		seriesFetcher.execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

//	    clear User Cache on option Logout
		if (id == R.id.action_logout) {
			User.clearCachedUser(this);

			Intent loginIntent = new Intent(this, LoginActivity.class);
			startActivity(loginIntent);
			finish();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFetchError(Exception e) {
		e.printStackTrace();
	}

	@Override
	public void onFetchStart() {
	}

	@Override
	public void onFetchFinish(Series series) {
		actionBar.setTitle(series.getTitle());

		setContentView(R.layout.activty_series);

		surfaceView = (SurfaceView) findViewById(R.id.videoSurface);
		surfaceView.setOnSystemUiVisibilityChangeListener(this);

		surfaceView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (controller.isEnabled()) {
					if (!controller.isShowing()) {
						controller.show();
					} else {
						controller.hide();
					}
				}

				return false;
			}
		});
		SurfaceHolder videoHolder = surfaceView.getHolder();
		videoHolder.addCallback(this);

		player = new MediaPlayer();
		controller = new VideoControllerView(this);
		controller.setEnabled(false);
		controller.setMediaPlayer(this);
		controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
		controller.show(0);

		try {
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setDataSource(this, Uri.parse(series.getEpisode(1).URL));
			player.setOnPreparedListener(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!playerPrepared) {
			player.setDisplay(holder);
			player.prepareAsync();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (player != null) {
			player.pause();
			resume = true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (resume) {
			controller.show(0);
			resume = false;
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		controller.setEnabled(true);
		playerPrepared = true;
		player.start();
		controller.updatePausePlay();
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return player.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return player.isPlaying();
	}

	@Override
	public void pause() {
		player.pause();
	}

	@Override
	public void seekTo(int i) {
		player.seekTo(i);
	}

	@Override
	public void start() {
		player.start();
	}

	@Override
	public boolean isFullScreen() {
		return !actionBar.isShowing();
	}

	@Override
	public void toggleFullScreen() {
		if (!isFullScreen()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	public void isHidden(boolean hidden) {
		if (isFullScreen() && hidden) {
			surfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int requestedOrientation = newConfig.orientation;

		if (requestedOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			actionBar.hide();
			surfaceView.getLayoutParams().height = WindowManager.LayoutParams.MATCH_PARENT;
			surfaceView.requestLayout();
		} else if(requestedOrientation == Configuration.ORIENTATION_PORTRAIT) {
			int normalHeight = (int) UIUtils.dipToPixels(
					getApplication(), getResources().getDimension(R.dimen.video_height_normal)) / 2;

			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			actionBar.show();
			surfaceView.getLayoutParams().height = normalHeight;
			surfaceView.requestLayout();
		}
	}

	@Override
	public void onBackPressed() {
		if (isFullScreen()) {
			toggleFullScreen();
			return;
		}

		super.onBackPressed();
	}

	@Override
	public void onSystemUiVisibilityChange(int uiFlag) {
		if (uiFlag != View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
			controller.show();
		}
	}
}