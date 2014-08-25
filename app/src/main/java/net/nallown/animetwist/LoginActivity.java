package net.nallown.animetwist;

import android.app.Activity;
import android.os.Bundle;

public class LoginActivity extends Activity {
	private final String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    setContentView(R.layout.activity_login);
	    if (savedInstanceState == null) {
		    getFragmentManager().beginTransaction()
				    .add(R.id.container, new LoginFragment())
				    .commit();
	    }
    }
}
