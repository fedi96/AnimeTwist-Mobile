package net.nallown.animetwist.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.nallown.animetwist.R;
import net.nallown.animetwist.activities.ChatActivity;
import net.nallown.animetwist.at.User;
import net.nallown.animetwist.at.UserFetcher;

import java.io.IOException;

/**
 * Created by Nasir on 24/08/2014.
 */
public class LoginFragment extends Fragment {
	private final String LOG_TAG = getClass().getSimpleName();

	SharedPreferences cachedUser;
	View view;

	Button loginButton = null;
	TextView registerButton = null;
	EditText usernameInput = null;
	EditText passwordInput = null;
	ProgressBar loginProgressBar = null;

	private User user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Cache
		cachedUser = this.getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);

		// Initialize UI Components
		view = inflater.inflate(R.layout.fragment_login, container, false);

		registerButton = (TextView) view.findViewById(R.id.login_signup);
		loginButton = (Button) view.findViewById(R.id.login_submit);

		usernameInput = (EditText) view.findViewById(R.id.login_username);
		passwordInput = (EditText) view.findViewById(R.id.login_password);

		loginProgressBar = (ProgressBar) view.findViewById(R.id.loginProgress);

		//  Login with cached user if exists
		if (cachedUser.contains("username")) {
			String cachedUsername = cachedUser.getString("username", "");
			String cachedPassword = cachedUser.getString("password", "");

			loginSubmit(cachedUsername, cachedPassword);
		}

		// Listen to register button and redirect to Register
		registerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent browserIntent =
						new Intent(Intent.ACTION_VIEW, Uri.parse("http://twist.moe/register"));
				startActivity(browserIntent);
			}
		});

		// Set login button listener
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String usernameInputStr = usernameInput.getText().toString().trim();
				String passwordInputStr = passwordInput.getText().toString();

				if (usernameInputStr.length() <= 3) {
					usernameInput.setError("Invalid username");
				} else if (passwordInputStr.length() <= 6) {
					passwordInput.setError("Invalid password");
				} else {
					loginSubmit(usernameInputStr, passwordInputStr);
				}
			}
		});

		return view;
	}

	// Login the user and cache, return True if successful
	public void loginSubmit(final String username, final String password) {
		UserFetcher userFetcher = new UserFetcher(username, password);

		userFetcher.setRequestStates(new UserFetcher.RequestStates() {
			@Override
			public void onError(Exception e) {
				if (e instanceof IOException) {

				} else {
					e.printStackTrace();
				}
			}

			@Override
			public void onStart() {
				// Disable input and show loader
				loginProgressBar.setVisibility(View.VISIBLE);
				usernameInput.setEnabled(false);
				passwordInput.setEnabled(false);
				loginButton.setEnabled(false);

				usernameInput.setText(username);
				passwordInput.setText(password);
			}

			@Override
			public void onFinish(User user) {
				if (user != null) {
					SharedPreferences.Editor editor = cachedUser.edit();

					editor.putString("username", username);
					editor.putString("password", password);
					editor.commit();

					Intent chatIntent = new Intent(getActivity(), ChatActivity.class)
							.putExtra("user", user);
					startActivity(chatIntent);
					getActivity().finish();
				}

				// Enable input just in case wrong credentials and hide loader
				loginProgressBar.setVisibility(View.GONE);
				usernameInput.setEnabled(true);
				passwordInput.setEnabled(true);
				loginButton.setEnabled(true);
			}
		});

		userFetcher.execute();
	}
}