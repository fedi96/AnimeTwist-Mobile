package net.nallown.animetwist;

import android.content.Context;
import android.widget.ArrayAdapter;

import net.nallown.animetwist.at.chat.Message;

import java.util.List;

/**
 * Created by Nasir on 27/08/2014.
 */
public class MessageAdapter extends ArrayAdapter{

	int resource;
	Context context;

	public MessageAdapter(Context context, int resource, List<Message> items){
		super(context, resource, items);
		this.resource = resource;
	}

//	http://www.josecgomez.com/2010/05/03/android-putting-custom-objects-in-listview/
}
