package net.nallown.animetwist;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.nallown.animetwist.at.User;

import java.util.concurrent.ExecutionException;

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

	private User user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_login, container, false);
		cachedUser = this.getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);

		// Initialize UI Components
		registerButton = (TextView) view.findViewById(R.id.login_signup);
		loginButton = (Button) view.findViewById(R.id.login_submit);

		usernameInput = (EditText) view.findViewById(R.id.login_username);
		passwordInput = (EditText) view.findViewById(R.id.login_password);

		// Listen to register button and redirect to Register
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent browserIntent =
						new Intent(Intent.ACTION_VIEW, Uri.parse("http://twist.moe/register"));
				startActivity(browserIntent);
			}
		});

		// Set login button listener
		loginButton.setOnClickListener(loginListener());

		//  Login with cached user if exists
		if (cachedUser.contains("username")) {
			String cachedUsername = cachedUser.getString("username", "");
			String cachedPassword = cachedUser.getString("password", "");

			if (loginSubmit(cachedUsername, cachedPassword)) {
				Intent chatIntent = new Intent(getActivity(), ChatActivity.class)
						.putExtra("user", user);
				startActivity(chatIntent);
				getActivity().finish();
			} else {
				usernameInput.setError("Failed to login");
			}
		}

		return view;
	}

	// Login button listener
	private View.OnClickListener loginListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String usernameInputStr = usernameInput.getText().toString().trim();
				String passwordInputStr = passwordInput.getText().toString();

				if (usernameInputStr.length() <= 3) {
					usernameInput.setError("Invalid username");
				} else if (passwordInputStr.length() <= 6) {
					passwordInput.setError("Invalid password");
				} else {
					if (loginSubmit(usernameInputStr, passwordInputStr)) {
						Intent chatIntent = new Intent(getActivity(), ChatActivity.class)
								.putExtra("user", user);
						startActivity(chatIntent);
						getActivity().finish();
					} else {
						usernameInput.setError("Invalid Credentials");
					}
				}
			}
		};
	};

	// Login the user and cache, return True if successful
	public boolean loginSubmit(String username, String password) {
		try {
			user = new User(username, password);

			if (user.login()) {
				SharedPreferences.Editor editor = cachedUser.edit();

				editor.putString("username", user.getUsername());
				editor.putString("password", user.getPassword());
				editor.commit();
				return true;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}
}