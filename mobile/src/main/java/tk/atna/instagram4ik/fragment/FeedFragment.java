package tk.atna.instagram4ik.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tk.atna.instagram4ik.ContentManager;
import tk.atna.instagram4ik.R;
import tk.atna.instagram4ik.adapter.FeedCursorAdapter;

public class FeedFragment extends BaseFragment implements OnRefreshListener {

    public static final String TAG = FeedFragment.class.getSimpleName();

    public static final int TITLE = R.string.feed;

    private ContentManager contentManager = ContentManager.get();

    private FeedCursorAdapter adapter;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefresh;

    @InjectView(R.id.feed_list)
    ListView feedList;

    private int currItem;


    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        ButterKnife.inject(this, view);

        swipeRefresh.setOnRefreshListener(this);

        if(adapter == null)
            adapter = new FeedCursorAdapter(inflater.getContext(), null, contentManager,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Log.d("myLogs", "---------- onClick: " + v.getTag());

                            // call details fragment
                            Bundle data = new Bundle();
                            data.putString(MEDIA_ID, (String) v.getTag());
                            makeFragmentAction(ACTION_MEDIA_DETAILS, data);
                        }
                    });

        feedList.setAdapter(adapter);
        feedList.setSelection(currItem);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshFeed();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // remember list position
        currItem = feedList.getFirstVisiblePosition();
    }

    @Override
    protected void receiveAction(int action, Bundle data) {

    }

    @Override
    protected int getTAG() {
        return TAG.hashCode();
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);

        // TODO feed refreshing with just created items
    }

    private void refreshWithNextPage() {


        // TODO feed refreshing with older items
    }

    private void refreshFeed() {
        contentManager.getFeed(new ContentManager.ContentCallback<Cursor>() {
            @Override
            public void onResult(Cursor cursor, Exception exception) {
                if(exception != null) {
                    exception.printStackTrace();
                    return;
                }
                if(adapter != null)
                    adapter.changeCursor(cursor);
            }
        });
    }

}
