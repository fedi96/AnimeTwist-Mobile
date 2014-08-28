package net.nallown.animetwist.at.Listener;

/**
 * Created by Nasir on 26/08/2014.
 */
public abstract class RequestListener {

	public abstract void onError(Exception e);
	public abstract void onFinish(String result);
	public abstract void onStart();

}
