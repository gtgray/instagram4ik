package tk.atna.instagram4ik.fragment;

import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tk.atna.instagram4ik.ContentManager;
import tk.atna.instagram4ik.LocalBroadcaster;
import tk.atna.instagram4ik.R;
import tk.atna.instagram4ik.adapter.FeedCursorAdapter;
import tk.atna.instagram4ik.provider.InstaContract;

public class FeedFragment extends BaseFragment
                          implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = FeedFragment.class.getSimpleName();

    public static final int TITLE = R.string.feed;

    public static final int FEED_CURSOR_LOADER = 0x00000cc1;

    private ContentManager contentManager = ContentManager.get();

//    private FeedCursorAdapter adapter;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefresh;

    @InjectView(R.id.feed_list)
    ListView feedList;

    // current list position
    private int currItem;

    /**
     * Initializes FeedFragment
     *
     * @return instance of retained FeedFregment class
     */
    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.inject(this, view);

        swipeRefresh.setOnRefreshListener(this);

        if(adapter == null)
            adapter = new FeedCursorAdapter(inflater.getContext(), null, contentManager,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // calls details fragment
                            Bundle data = new Bundle();
                            data.putString(MEDIA_ID, (String) v.getTag());
                            makeFragmentAction(ACTION_MEDIA_DETAILS, data);

                            Log.d("myLogs", "------------- ID " + v.getTag());

                            ((FeedCursorAdapter) adapter).getFirstMediaId();
                            ((FeedCursorAdapter) adapter).getLastMediaId();

                        }
                    });

        feedList.setAdapter(adapter);
        feedList.setSelection(currItem);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // refresh list at start
        if(savedInstanceState == null)
            contentManager.getFeed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // remember list position
        currItem = feedList.getFirstVisiblePosition();
    }

    /**
     * LocalBroadcaster callback to catch and process commands (with data)
     *
     * @param action action to process
     * @param data received data
     */
    @Override
    public void onReceive(int action, Bundle data) {
        switch (action) {
            // feed loaded, hide progress
            case LocalBroadcaster.ACTION_REFRESH_FEED:
                swipeRefresh.setRefreshing(false);
                break;
            // like/unlike callback
            case LocalBroadcaster.ACTION_REFRESH:
                if (data != null) {
                    String mediaId = data.getString(LocalBroadcaster.MEDIA_ID);
                    if(mediaId != null)
                        // load new details for media with id
                        contentManager.getMediaDetails(mediaId);
                }
                break;
        }
    }

    @Override
    protected CursorLoader getCursorLoader() {
        return new CursorLoader(getActivity(),
                                InstaContract.Feed.CONTENT_URI,
                                null, null, null, null);
    }

    @Override
    protected int getLoaderId() {
        return FEED_CURSOR_LOADER;
    }

    /**
     * Callback of pull-to-refresh layout, the point from where
     * it is needed to start loading a new data
     */
    @Override
    public void onRefresh() {
        // show progress
        swipeRefresh.setRefreshing(true);
        contentManager.getFeedLater(((FeedCursorAdapter) adapter).getFirstMediaId());
    }

}
