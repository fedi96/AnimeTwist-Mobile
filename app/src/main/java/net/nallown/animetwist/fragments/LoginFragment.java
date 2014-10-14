package net.nallown.animetwist.fragments;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.Toast;

import net.nallown.animetwist.R;
import net.nallown.animetwist.activities.MainActivity;
import net.nallown.animetwist.at.User;
import net.nallown.animetwist.at.UserFetcher;

import java.io.IOException;

/**
 * Created by Nasir on 24/08/2014.
 */
public class LoginFragment extends Fragment implements UserFetcher.RequestStates {
	private final String LOG_TAG = getClass().getSimpleName();
	private View view;

	private Button loginButton = null;
	private TextView registerButton = null;
	private EditText usernameInput = null;
	private EditText passwordInput = null;
	private ProgressBar loginProgressBar = null;

	private String usernameInputStr;
	private String passwordInputStr;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_login, container, false);

		registerButton = (TextView) view.findViewById(R.id.login_signup);
		loginButton = (Button) view.findViewById(R.id.login_submit);
		usernameInput = (EditText) view.findViewById(R.id.login_username);
		passwordInput = (EditText) view.findViewById(R.id.login_password);
		loginProgressBar = (ProgressBar) view.findViewById(R.id.loginProgress);

		if (User.cachedUserExists(getActivity())) {
			User cachedUser = User.getCachedUser(getActivity());
			usernameInputStr = cachedUser.getUsername();
			passwordInputStr = cachedUser.getPassword();

			loginSubmit();
		}

		registerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent browserIntent =
						new Intent(Intent.ACTION_VIEW, Uri.parse("http://animetwist.net/register"));
				startActivity(browserIntent);
			}
		});

		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				usernameInputStr = usernameInput.getText().toString().trim();
				passwordInputStr = passwordInput.getText().toString();

				if (usernameInputStr.length() <= 3) {
					usernameInput.setError("Invalid username");
				} else if (passwordInputStr.length() <= 6) {
					passwordInput.setError("Invalid password");
				} else {
					loginSubmit();
				}
			}
		});

		return view;
	}

	public void loginSubmit() {
		UserFetcher userFetcher = new UserFetcher(usernameInputStr, passwordInputStr);
		userFetcher.setRequestStates(this);
		userFetcher.execute();
	}

	@Override
	public void onFetchError(Exception e) {
		final Toast noNetworkToast = Toast.makeText(
				getActivity(), "Failed to connect to the Anime Twist servers.", Toast.LENGTH_LONG);

		if (e instanceof IOException) {
			noNetworkToast.show();
		} else {
			e.printStackTrace();
		}
	}

	@Override
	public void onFetchFinish(User user) {
		if (user != null) {
			User.storeCachedUser(user, getActivity());

			Intent mainIntent = new Intent(getActivity(), MainActivity.class)
					.putExtra("user", user);
			startActivity(mainIntent);
			getActivity().finish();
		} else {
			loginProgressBar.setVisibility(View.GONE);
			usernameInput.setEnabled(true);
			passwordInput.setEnabled(true);
			loginButton.setEnabled(true);
			registerButton.setEnabled(true);

			usernameInput.setError("Invalid Credentials");
			usernameInput.requestFocus();
		}
	}

	@Override
	public void onFetchStart() {
		loginProgressBar.setVisibility(View.VISIBLE);
		usernameInput.setEnabled(false);
		passwordInput.setEnabled(false);
		loginButton.setEnabled(false);
		registerButton.setEnabled(false);

		usernameInput.setText(usernameInputStr);
		passwordInput.setText(passwordInputStr);
	}
}