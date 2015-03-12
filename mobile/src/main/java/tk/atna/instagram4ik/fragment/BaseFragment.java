package tk.atna.instagram4ik.fragment;

import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;

import tk.atna.instagram4ik.LocalBroadcaster;

public abstract class BaseFragment extends Fragment
                                   implements LocalBroadcaster.LocalActionListener,
                                              LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Actions to use in activity-fragment communication.
     * As usual represents fragment clicks
     */
    public static final int ACTION_MEDIA_DETAILS = 0x00000a1;
    public static final int ACTION_FINISH = 0x00000a2;

    public static final String MEDIA_ID = "media_id";

    private LocalBroadcaster broadcaster;

    CursorAdapter adapter;

    /**
     * Fragment action listener
     */
	private FragmentActionCallback callback;


    /**
     * Invokes fragment action callback
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

        // initializes actions callback and
        // proves that hoster activity implements it
		try {
			callback = (FragmentActionCallback) getActivity();

		} catch (ClassCastException e) {
			e.printStackTrace();
			Log.d("myLogs", BaseFragment.class.getSimpleName()
							        + ".onActivityCreated: activity must implement "
							        + FragmentActionCallback.class.getSimpleName());
		}

        // initializes loader manager for cursors
        if(getActivity() != null) {
            getActivity().getSupportLoaderManager()
                         .initLoader(getLoaderId(), null, this);
//                    .restartLoader(getLoaderId(), null, this);

        }

        broadcaster = new LocalBroadcaster(this);
        // start listen to local broadcaster
        if(getActivity() != null)
            getActivity().registerReceiver(broadcaster,
                    new IntentFilter(LocalBroadcaster.LOCAL_BROADCAST_FILTER));
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // forget local broadcaster
        if(getActivity() != null)
            getActivity().unregisterReceiver(broadcaster);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == getLoaderId()) {
            adapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.changeCursor(null);
    }

    /**
     * Each child fragment who needs to use loader manager
     * have to implement this method and return it's loader object
     *
     * @return loader for fragment's loader manager
     */
    protected abstract CursorLoader getCursorLoader();

    /**
     * Loader id to register in loader manager
     *
     * @return fragment's loader id
     */
    protected abstract int getLoaderId();

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
