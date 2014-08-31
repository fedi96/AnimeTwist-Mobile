package net.nallown.utils.States;

/**
 * Created by Nasir on 26/08/2014.
 */
public abstract class RequestStates {

	public abstract void onError(Exception e);

	public abstract void onFinish(String result);

	public abstract void onStart();

}
