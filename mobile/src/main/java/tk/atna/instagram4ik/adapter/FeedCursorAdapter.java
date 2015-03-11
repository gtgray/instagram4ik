package tk.atna.instagram4ik.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tk.atna.instagram4ik.ContentManager;
import tk.atna.instagram4ik.R;
import tk.atna.instagram4ik.Utils;
import tk.atna.instagram4ik.provider.InstaContract;


public class FeedCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    private int mediaIdIndex;
    private int imageIndex;
    private int commentedIndex;
    private int likedIndex;
    private int myLikeIndex;
    private int captionIndex;
    private int createdIndex;
    private int textIndex;
    private int usernameIndex;
    private int commentCreatedIndex;

    private ContentManager contentManager;

    private View.OnClickListener imageClickListener;


    public FeedCursorAdapter(Context context, Cursor cursor, ContentManager contentManager,
                             View.OnClickListener listener) {
        super(context, cursor, 0);

        this.inflater = LayoutInflater.from(context);
        this.contentManager = contentManager;
        this.imageClickListener = listener;

        rememberColumns(cursor);

//        created_time: "1425851681" * 1000 = time in millis

    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.fragment_feed_item, parent, false);
        final ItemViewHolder holder = new ItemViewHolder(view);

        int wide = context.getResources().getDisplayMetrics().widthPixels;
        holder.btnImage.setLayoutParams(new LinearLayout.LayoutParams(wide, wide));
        holder.btnImage.setOnClickListener(imageClickListener);

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckedTextView) v).isChecked();
                ((CheckedTextView) v).setChecked(checked ^= true);
                String mediaId = (String) holder.btnImage.getTag();
                if (mediaId != null)
                    if (checked)
                        contentManager.like(mediaId);
                    else
                        contentManager.unlike(mediaId);
            }
        });

        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ItemViewHolder holder = (ItemViewHolder) view.getTag();

        contentManager.getImage(cursor.getString(imageIndex), holder.btnImage);
        holder.btnImage.setTag(cursor.getString(mediaIdIndex));

        holder.tvCommented.setText(context.getString(R.string.commented)
                + cursor.getString(commentedIndex));
        holder.tvLiked.setText(context.getString(R.string.liked)
                + cursor.getString(likedIndex));
        holder.tvCreated.setText(context.getString(R.string.created)
                + Utils.formatTime(cursor.getString(createdIndex)));

        String caption = cursor.getString(captionIndex);
        holder.tvCaption.setText(caption);
        holder.tvCaption.setVisibility((caption != null && caption.length() > 0)
                ? View.VISIBLE : View.GONE);

        holder.btnLike.setChecked(cursor.getInt(myLikeIndex) > 0);
//        holder.btnLike.setTag(cursor.getString(mediaIdIndex));

        populateLimitedComments(holder.llComments, cursor.getString(mediaIdIndex));

    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);

        rememberColumns(cursor);
    }

    private void rememberColumns(Cursor cursor) {
        if(cursor == null)
            return;

        this.mediaIdIndex = cursor.getColumnIndex(InstaContract.Feed.FEED_MEDIA_ID);
        this.imageIndex = cursor.getColumnIndex(InstaContract.Feed.FEED_IMAGE_URL);
        this.commentedIndex = cursor.getColumnIndex(InstaContract.Feed.FEED_COMMENTS_COUNT);
        this.likedIndex = cursor.getColumnIndex(InstaContract.Feed.FEED_LIKES_COUNT);
        this.myLikeIndex = cursor.getColumnIndex(InstaContract.Feed.FEED_I_LIKED);
        this.createdIndex = cursor.getColumnIndex(InstaContract.Feed.FEED_CREATED);
        this.captionIndex = cursor.getColumnIndex(InstaContract.Feed.FEED_CAPTION);
    }

    private void populateLimitedComments(final ViewGroup container, String mediaId) {
        contentManager.pullLimitedCommentsFromCache(mediaId,
                                                    new ContentManager.ContentCallback<Cursor>() {
                    @Override
                    public void onResult(Cursor result, Exception exception) {

                        container.removeAllViews();

                        if(result != null && result.moveToFirst()) {

                            textIndex = result.getColumnIndex(
                                    InstaContract.Comments.COMMENTS_COMMENT_TEXT);
                            usernameIndex = result.getColumnIndex(
                                    InstaContract.Comments.COMMENTS_USERNAME);
                            commentCreatedIndex = result.getColumnIndex(
                                    InstaContract.Comments.COMMENTS_COMMENT_CREATED);

                            do {
                                TextView tvComment = new TextView(new ContextThemeWrapper(
                                        container.getContext(), R.style.Style_TextView_Comment));
                                tvComment.setText(mixComment(result));
                                container.addView(tvComment);

                            } while (result.moveToNext());

                            result.close();
                        }
                    }
                });
    }

    private String mixComment(Cursor cursor) {
        String mix;
        String created = Utils.formatTime(cursor.getString(commentCreatedIndex));
        String username = cursor.getString(usernameIndex);
        String text = cursor.getString(textIndex);

        return "[" + created + "] " + username + ": '" + text + "'";
    }


    class ItemViewHolder {

        @InjectView(R.id.feed_item_image)
        ImageView btnImage;

        @InjectView(R.id.feed_item_commented)
        TextView tvCommented;

        @InjectView(R.id.feed_item_liked)
        TextView tvLiked;

        @InjectView(R.id.feed_item_btn_like)
        CheckedTextView btnLike;

        @InjectView(R.id.feed_item_created)
        TextView tvCreated;

        @InjectView(R.id.feed_item_caption)
        TextView tvCaption;

        @InjectView(R.id.feed_item_comments)
        LinearLayout llComments;


        ItemViewHolder(View v) {
            ButterKnife.inject(this, v);
        }

    }
}
