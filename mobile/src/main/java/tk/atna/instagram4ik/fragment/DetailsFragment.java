package tk.atna.instagram4ik.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import tk.atna.instagram4ik.ContentManager;
import tk.atna.instagram4ik.DetailedMedia;
import tk.atna.instagram4ik.LocalBroadcaster;
import tk.atna.instagram4ik.R;
import tk.atna.instagram4ik.Utils;
import tk.atna.instagram4ik.adapter.CommentsCursorAdapter;
import tk.atna.instagram4ik.provider.InstaContract;

public class DetailsFragment extends BaseFragment {

    public static final String TAG = DetailsFragment.class.getSimpleName();

    public static final int TITLE = R.string.details;

    private static final int COMMENTS_CURSOR_LOADER = 0x00000cc2;

    private ContentManager contentManager = ContentManager.get();

//    private CommentsCursorAdapter adapter;

    private int loaderId;

    private DetailedMedia detailedMedia;

    /**
     * List position
     */
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

    @InjectView(R.id.details_header_likers)
    LinearLayout tvLikers;


    /**
     * Initializes DetailsFragment with data
     *
     * @param data fragment initialize data
     * @return instance of retained DetailsFregment class
     */
    public static DetailsFragment newInstance(Bundle data) {
        DetailsFragment fragment = new DetailsFragment();
        fragment.setRetainInstance(true);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // process init arguments
        if(getArguments() != null) {
            // seeking for media id
            String mediaId = getArguments().getString(MEDIA_ID);
            // no media id - finish
            if(mediaId == null || mediaId.length() == 0) {
                makeFragmentAction(ACTION_FINISH, null);
                Log.d("myLogs", "mediaId can't be empty");

                if(getActivity() != null)
                    Toast.makeText(getActivity(), "Can't find detailes for this media",
                                   Toast.LENGTH_LONG).show();
                return;
            }
            // remember media id
            detailedMedia = new DetailedMedia(mediaId);
            loaderId = (new Random()).nextInt(100500);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        // find list
        commentsList = ButterKnife.findById(view, R.id.comments_list);
        // inflate header
        View header = inflater.inflate(R.layout.fragment_details_header, commentsList, false);
        // add header
        commentsList.addHeaderView(header, null, false);
        // inject all other views
        ButterKnife.inject(this, view);
        // sets image size as 1/3 wide of the screen
        int half = getResources().getDisplayMetrics().widthPixels / 3;
        ivImage.setLayoutParams(new LinearLayout.LayoutParams(half, half));

        if(adapter == null)
            adapter = new CommentsCursorAdapter(inflater.getContext(), null, contentManager);

        commentsList.setAdapter(adapter);
        // recall list position
        commentsList.setSelection(currItem);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // refresh data at start
        if(savedInstanceState == null) {
            // load fresh data
            contentManager.getMediaDetails(detailedMedia.getMediaId());
            contentManager.getComments(detailedMedia.getMediaId());
        }

        // populate non list views
        pullMedia();
        pullLikes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // remember list position
        currItem = commentsList.getFirstVisiblePosition();
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
            case LocalBroadcaster.ACTION_REFRESH:
                if (data != null) {
                    String mediaId = data.getString(LocalBroadcaster.MEDIA_ID);
                    if(mediaId != null)
                        contentManager.getMediaDetails(mediaId);
                }
                break;

            case LocalBroadcaster.ACTION_REFRESH_MEDIA:
                pullMedia();
                break;

            case LocalBroadcaster.ACTION_REFRESH_LIKES:
                pullLikes();
                break;

        }
        // TODO process action calls
    }

    @Override
    protected CursorLoader getCursorLoader() {
        if(getActivity() != null)
            return new CursorLoader(getActivity(), Uri.withAppendedPath(
                    InstaContract.Comments.CONTENT_URI, detailedMedia.getMediaId()),
                    null, null, null, null);

        return null;
    }

    @Override
    protected int getLoaderId() {
//        return COMMENTS_CURSOR_LOADER;
        return loaderId;
    }

    /**
     * Reaction to button like/unlike click
     *
     * @param v like/unlike button
     */
    @OnClick(R.id.details_header_btn_like)
    public void onClick(View v) {
        boolean checked = ((CheckedTextView) v).isChecked();
        ((CheckedTextView) v).setChecked(checked ^= true);
        if (checked)
            contentManager.like(detailedMedia.getMediaId());
        else
            contentManager.unlike(detailedMedia.getMediaId());
    }

    /**
     * Pulls self media from sqlite and popultes views with its data
     */
    public void pullMedia() {
        contentManager.pullMediaFromCache(detailedMedia.getMediaId(),
                new ContentManager.ContentCallback<DetailedMedia>() {
                    @Override
                    public void onResult(DetailedMedia result, Exception exception) {
                        if (exception != null) {
                            exception.printStackTrace();
                            return;
                        }
                        if(result != null) {
                            detailedMedia = result;
                            populateMediaViews(detailedMedia);
                        }
                    }
        });
    }

    /**
     * Pulls self media likes from sqlite and represents them in view
     */
    public void pullLikes() {
        contentManager.pullLikesFromCache(detailedMedia.getMediaId(),
                new ContentManager.ContentCallback<Cursor>() {
                    @Override
                    public void onResult(Cursor result, Exception exception) {
                        if (exception != null) {
                            exception.printStackTrace();
                            return;
                        }
                        if (result != null) {
                            populateLikesView(result);
                        }
                    }
                });
    }

    /**
     * Populates views with media data
     *
     * @param media media model object
     */
    private void populateMediaViews(DetailedMedia media) {
        if(getActivity() != null) {
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

    /**
     * Represents list of media likes
     *
     * @param cursor cursor object with list of likes
     */
    private void populateLikesView(Cursor cursor) {
        if(cursor != null && cursor.moveToFirst()) {
            // flush views
            tvLikers.removeAllViews();
            LinearLayout one = new LinearLayout(new ContextThemeWrapper(
                    tvLikers.getContext(), R.style.Style_LinearLayout_Like));
            LinearLayout two = new LinearLayout(new ContextThemeWrapper(
                    tvLikers.getContext(), R.style.Style_LinearLayout_Like));
            LinearLayout three = new LinearLayout(new ContextThemeWrapper(
                    tvLikers.getContext(), R.style.Style_LinearLayout_Like));
            //
            int count = cursor.getCount();
            int perRow = count / 3 + 2;
            do {
                // take next like
                TextView tvLike = new TextView(new ContextThemeWrapper(
                        tvLikers.getContext(), R.style.Style_TextView_Like));
                tvLike.setText(cursor.getString(
                        cursor.getColumnIndex(InstaContract.Likes.LIKES_USERNAME)));
                //
                if(cursor.getPosition() <= perRow)
                    one.addView(tvLike);
                else if(cursor.getPosition() > 2 * perRow)
                    two.addView(tvLike);
                else
                    three.addView(tvLike);


            } while (cursor.moveToNext());

            tvLikers.addView(one);
            tvLikers.addView(two);
            tvLikers.addView(three);

            cursor.close();
        }
    }



}
