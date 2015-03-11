package tk.atna.instagram4ik.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import tk.atna.instagram4ik.ContentManager;
import tk.atna.instagram4ik.DetailedMedia;
import tk.atna.instagram4ik.R;
import tk.atna.instagram4ik.Utils;
import tk.atna.instagram4ik.adapter.CommentsCursorAdapter;

public class DetailsFragment extends BaseFragment {

    public static final String TAG = DetailsFragment.class.getSimpleName();

    public static final int TITLE = R.string.feed;

    private ContentManager contentManager = ContentManager.get();

    private CommentsCursorAdapter adapter;

    private DetailedMedia detailedMedia;

    private int currItem;

    ListView commentsList;

    @InjectView(R.id.details_header_image)
    ImageView ivImage;

    @InjectView(R.id.details_header_created)
    TextView tvCreated;

    @InjectView(R.id.details_header_commented)
    TextView tvCommented;

    @InjectView(R.id.details_header_liked)
    TextView tvLiked;

    @InjectView(R.id.details_header_caption)
    TextView tvCaption;

    @InjectView(R.id.details_header_btn_like)
    CheckedTextView btnLike;


    public static DetailsFragment newInstance(Bundle data) {
        DetailsFragment fragment = new DetailsFragment();
        fragment.setRetainInstance(true);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // process initializing arguments
        if(getArguments() != null) {
            String mediaId = getArguments().getString(MEDIA_ID);

            if(mediaId == null || mediaId.length() == 0) {
                makeFragmentAction(ACTION_FINISH, null);
                Log.d("myLogs", "mediaId can't be empty");

                if(getActivity() != null)
                    Toast.makeText(getActivity(), "Can't find detailes for this media",
                                   Toast.LENGTH_LONG).show();
                return;
            }

            detailedMedia = new DetailedMedia(mediaId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        // find list
        commentsList = ButterKnife.findById(view, R.id.comments_list);
        // inflate header
        View header = inflater.inflate(R.layout.fragment_details_header, commentsList, false);
        // add header
        commentsList.addHeaderView(header, null, false);
        // inject all other views
        ButterKnife.inject(this, view);

        if(adapter == null)
            adapter = new CommentsCursorAdapter(inflater.getContext(), null, contentManager);

        commentsList.setAdapter(adapter);
        commentsList.setSelection(currItem);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO get dataaaaa
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // remember list position
        currItem = commentsList.getFirstVisiblePosition();
    }

    @Override
    protected void receiveAction(int action, Bundle data) {

        // TODO process action calls

    }

    @Override
    protected int getTAG() {
        return TAG.hashCode();
    }

    @OnClick(R.id.details_header_btn_like)
    public void onClick(View v) {
        boolean checked = ((CheckedTextView) v).isChecked();
        ((CheckedTextView) v).setChecked(checked ^= true);
        if (checked)
            contentManager.like(detailedMedia.getMediaId());
        else
            contentManager.unlike(detailedMedia.getMediaId());
    }

    private Cursor getList() {
        //

        return null;
    }

    private void populateViews(DetailedMedia media) {
        contentManager.getImage(media.getImageUrl(), ivImage);
        tvLiked.setText(getString(R.string.liked)
                + media.getLikesCount());
        tvCommented.setText(getString(R.string.commented)
                + media.getCommentsCount());
        tvCreated.setText(getString(R.string.created)
                + Utils.formatTime(media.getCreatedTime()));
        tvCaption.setText(media.getCaption());
        btnLike.setChecked(media.isiLiked());
    }

}
