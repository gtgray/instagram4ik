package tk.atna.instagram4ik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LocalBroadcaster extends BroadcastReceiver {

    // possible actions
	public static final int ACTION_REFRESH = 0x00000ba1;
	public static final int ACTION_REFRESH_FEED = 0x00000ba2;
	public static final int ACTION_REFRESH_MEDIA = 0x00000ba3;
//	public static final int ACTION_REFRESH_COMMENTS = 0x00000ba4;
	public static final int ACTION_REFRESH_LIKES = 0x00000ba5;

    // transfering data keys
    public static final String MEDIA_ID = "media_id";

    // default target group
//	public static final int NO_TARGET = 0;

	public static final String LOCAL_BROADCAST_FILTER = "tk.atna.instagram4ik";

//	private static final String LOCAL_BROADCAST_TARGET = "target";
	private static final String LOCAL_BROADCAST_ACTION = "action";
	private static final String LOCAL_BROADCAST_DATA = "data";

	private LocalActionListener listener;


	public LocalBroadcaster(LocalActionListener listener) {
		this.listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
        final int action = intent.getIntExtra(LOCAL_BROADCAST_ACTION, 0);
        final Bundle data = intent.getBundleExtra(LOCAL_BROADCAST_DATA);

        (new Handler()).post(new Runnable() {
            @Override
            public void run() {
                if(listener != null)
                    listener.onReceive(action, data);
            }
        });
	}

    /**
     * Sends local (in bounds of package) broadcast
     *
     * @param action command to run
     * @param data action data
     * @param context context
     */
	public static void sendLocalBroadcast(int action, Bundle data, Context context) {
		
		Intent intent = new Intent();
		intent.setAction(LOCAL_BROADCAST_FILTER);
		intent.setPackage(LOCAL_BROADCAST_FILTER);
//		intent.putExtra(LOCAL_BROADCAST_TARGET, target);
		intent.putExtra(LOCAL_BROADCAST_ACTION, action);
		if(data != null) 
			intent.putExtra(LOCAL_BROADCAST_DATA, data);
        // fire!
		if(context != null)
            context.sendBroadcast(intent);
	}


    /**
     * Broadcaster callback
     */
	public interface LocalActionListener {
        /**
         * Method which would be called on broadcast received
         */
        public void onReceive(int action, Bundle data);
    }

}
