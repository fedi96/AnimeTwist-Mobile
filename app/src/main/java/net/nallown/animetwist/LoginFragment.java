package net.nallown.animetwist;

import android.app.Fragment;
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

	SharedPreferences userSetting;
	View view;
	private User user;

	public LoginFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_login, container, false);
		userSetting = this.getActivity().getSharedPreferences("USER", 0);

		final TextView registerButton = (TextView) view.findViewById(R.id.login_signup);
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent browserIntent =
						new Intent(Intent.ACTION_VIEW, Uri.parse("http://twist.moe/register"));
				startActivity(browserIntent);
			}
		});

		final Button loginButton = (Button) view.findViewById(R.id.login_submit);
		final EditText usernameInput = (EditText) view.findViewById(R.id.login_username);
		final EditText passwordInput = (EditText) view.findViewById(R.id.login_password);
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
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
					} else {
						usernameInput.setError("Invalid Credentials");
					}
				}
			}
		});

		if (userSetting.getString("username", "") != "") {
			usernameInput.setText(userSetting.getString("username", ""));
			passwordInput.setText(userSetting.getString("password", ""));
			if (loginSubmit(
				userSetting.getString("username", ""),
				userSetting.getString("password", "")
			)) {
				Intent chatIntent = new Intent(getActivity(), ChatActivity.class)
						.putExtra("user", user);
				startActivity(chatIntent);
			} else {
				usernameInput.setError("Failed to login");
			}
		}

		return view;
	}

	public boolean loginSubmit(String username, String password) {
		try {
			user = new User(username, password);

			if (user.login()) {
				SharedPreferences.Editor editor = userSetting.edit();

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