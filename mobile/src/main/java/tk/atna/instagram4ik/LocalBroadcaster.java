package tk.atna.instagram4ik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LocalBroadcaster extends BroadcastReceiver {

	public final static String LOCAL_BROADCAST_FILTER = "tk.atna.instagram4ik";
	private final static String LOCAL_BROADCAST_TARGET = "target";
	private final static String LOCAL_BROADCAST_ACTION = "action";
	private final static String LOCAL_BROADCAST_DATA = "data";

	private ReceivedAction received;
	private int tag;


	public LocalBroadcaster(ReceivedAction received, int tag) {
		this.received = received;
		this.tag = tag;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		int target = intent.getIntExtra(LOCAL_BROADCAST_TARGET, 0);
		if(target == tag) {

            final int action = intent.getIntExtra(LOCAL_BROADCAST_ACTION, 0);
            final Bundle data = intent.getBundleExtra(LOCAL_BROADCAST_DATA);

			(new Handler()).post(new Runnable() {
				
				@Override
				public void run() {
					if(received != null)
						received.onReceiveAction(action, data);
				}
			});
		}
	}
	
	public static void sendLocalBroadcast(int target, int action, Bundle data, Context context) {
		
		Intent intent = new Intent();
		intent.setAction(LOCAL_BROADCAST_FILTER);
		intent.setPackage(LOCAL_BROADCAST_FILTER);
		intent.putExtra(LOCAL_BROADCAST_TARGET, target);
		intent.putExtra(LOCAL_BROADCAST_ACTION, action);
		if(data != null) 
			intent.putExtra(LOCAL_BROADCAST_DATA, data);
		
        context.sendBroadcast(intent);
	}
	
	
	public interface ReceivedAction {
		
		public void onReceiveAction(int action, Bundle data);
	}
	
}
