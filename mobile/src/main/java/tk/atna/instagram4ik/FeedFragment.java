package tk.atna.instagram4ik;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FeedFragment extends Fragment implements OnRefreshListener {

    public static final String TAG = FeedFragment.class.getSimpleName();

    public static final int TITLE = R.string.feed;

    private ContentManager contentManager = ContentManager.get();

    private FeedCursorAdapter adapter;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefresh;

    @InjectView(R.id.feed_list)
    ListView lvData;

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
            adapter = new FeedCursorAdapter(inflater.getContext(), null, contentManager) {

                @Override
                public View newView(Context context, final Cursor cursor, ViewGroup parent) {
                    View view = super.newView(context, cursor, parent);

                    final ItemViewHolder holder = (ItemViewHolder) view.getTag();

                    holder.btnImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("myLogs", "---------- onClick: " + holder.btnLike.getTag());
                        }
                    });

                    return view;
                }
            };

        lvData.setAdapter(adapter);
        lvData.setSelection(currItem);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshFeed();

        // fill the list if it is empty
//        if(adapter.getCount() == 0)
//            getList(state.hub, state.batch);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // remember list position
        currItem = lvData.getFirstVisiblePosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // cancel all current requests
        flushCurrent();
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
                adapter.changeCursor(cursor);
            }
        });

    }

    private void flushCurrent() {
//        int size = runningRequests.size();
//        for(int i = 0; i < size; i++) {
//            helper.cancelRequest(runningRequests.get(i));
//        }
//        helper.cancelAllImages();
//        adapter.clear();

    }
}
