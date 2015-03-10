package tk.atna.instagram4ik;

import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class DetailsFragment extends Fragment {

    public static final String TAG = DetailsFragment.class.getSimpleName();

    public static final int TITLE = R.string.feed;

    private ContentManager helper = ContentManager.get();

//    private CursorAdapter adapter;

    private ListView lvData;

    private int currItem;


    public static DetailsFragment newInstance() {
        DetailsFragment fragment = new DetailsFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        lvData = (ListView) view.findViewById(R.id.comments_list);

        Cursor cursor = getList();

//        adapter = new CursorAdapter(inflater.getContext(), cursor, helper);

//        lvData.setAdapter(adapter);

        lvData.setSelection(currItem);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    private Cursor getList() {
        //

        return null;
    }

    private void flushCurrent() {
//        int size = runningRequests.size();
//        for(int i = 0; i < size; i++) {
//            helper.cancelRequest(runningRequests.get(i));
//        }
        helper.cancelAllImages();
//        adapter.clear();

    }
}
