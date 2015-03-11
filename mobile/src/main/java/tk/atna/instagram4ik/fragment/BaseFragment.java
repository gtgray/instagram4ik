package tk.atna.instagram4ik.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import tk.atna.instagram4ik.LocalBroadcaster;
import tk.atna.instagram4ik.LocalBroadcaster.ReceivedAction;

public abstract class BaseFragment extends Fragment {

    public static final int ACTION_MEDIA_DETAILS = 0x00000a1;
    public static final int ACTION_FINISH = 0x00000a2;
//    public static final int CLICK_GO = 0x00000a1;

    public static final String MEDIA_ID = "media_id";


    private LocalBroadcaster broadcaster = new LocalBroadcaster(new ReceivedAction() {

        @Override
        public void onReceiveAction(int action, Bundle data) {
            receiveAction(action, data);
        }

    }, getTAG());

    /**
     * Action callback
     */
	private FragmentActionCallback callback;


    /**
     * Invokes action callback
     *
     * @param action needed fragment command
     * @param data additional data to send
     */
	public void makeFragmentAction(int action, Bundle data) {
		if (callback != null)
			callback.onAction(action, data);
	}

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        // initialize actions callback and
        // prove that hoster activity implements it
		try {
			callback = (FragmentActionCallback) getActivity();

		} catch (ClassCastException e) {
			e.printStackTrace();
			Log.d("myLogs", BaseFragment.class.getSimpleName()
							        + ".onActivityCreated: activity must implement "
							        + FragmentActionCallback.class.getSimpleName());
		}

        //
        if(getActivity() != null)
            getActivity().registerReceiver(broadcaster,
                    new IntentFilter(LocalBroadcaster.LOCAL_BROADCAST_FILTER));
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(getActivity() != null)
            getActivity().unregisterReceiver(broadcaster);
    }

    protected abstract void receiveAction(int action, Bundle data);

    protected abstract int getTAG();


    /**
     * Callback interface to deliver fragment actions to activity
     */
    public interface FragmentActionCallback {

        /**
         * Called on fragment action event
         *
         * @param action needed command
         * @param data additional data to send
         */
		public void onAction(int action, Bundle data);
	}

}
