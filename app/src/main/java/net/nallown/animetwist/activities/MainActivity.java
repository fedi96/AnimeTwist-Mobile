package net.nallown.animetwist.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import net.nallown.animetwist.R;
import net.nallown.animetwist.at.User;
import net.nallown.animetwist.fragments.ChatFragment;
import net.nallown.animetwist.fragments.SeriesListFragment;


public class MainActivity extends Activity {
	private final String LOG_TAG = getClass().getSimpleName();

	User user = null;

	private ChatFragment mNavigationDrawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle data = getIntent().getExtras();
		user = data.getParcelable("user");

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new SeriesListFragment())
					.commit();
		}

		mNavigationDrawerFragment = (ChatFragment)
				getFragmentManager().findFragmentById(R.id.navigation_drawer);

		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	public void setTitle(CharSequence title) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Drawer opened
			return true;
		}

		getMenuInflater().inflate(R.menu.chat, menu);
		// Drawer closed
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences userSetting = getSharedPreferences("USER", 0);

		int id = item.getItemId();

//	    clear User Cache on option Logout
		if (id == R.id.action_logout) {
			SharedPreferences.Editor editor = userSetting.edit();
			editor.clear();
			editor.apply();

			Intent loginIntent = new Intent(this, LoginActivity.class);
			startActivity(loginIntent);
			finish();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Hide app instead of closing
	@Override
	public void onBackPressed() {
		if (mNavigationDrawerFragment.isDrawerOpen()) {
			mNavigationDrawerFragment.getDrawerLayout().closeDrawers();
			return;
		}
		moveTaskToBack(true);
	}
}