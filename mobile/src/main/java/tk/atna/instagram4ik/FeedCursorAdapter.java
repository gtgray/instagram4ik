package tk.atna.instagram4ik;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tk.atna.instagram4ik.provider.InstaContract;


public class FeedCursorAdapter extends CursorAdapter {

    private static final int ITEM_RES = R.layout.fragment_feed_item;

    private LayoutInflater inflater;

    private int mediaIdIndex;
    private int imageIndex;
    private int commentedIndex;
    private int likedIndex;
    private int myLikeIndex;
    private int createdIndex;
    private int textIndex;
    private int usernameIndex;
    private int commentCreatedIndex;

    private ContentManager contentManager;


    public FeedCursorAdapter(Context context, Cursor cursor, ContentManager contentManager) {
        super(context, cursor, 0);

        this.inflater = LayoutInflater.from(context);
        this.contentManager = contentManager;

        rememberColumns(cursor);

//        created_time: "1425851681" * 1000 = time in millis

    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(ITEM_RES, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);

        int wide = context.getResources().getDisplayMetrics().widthPixels;
        holder.btnImage.setLayoutParams(new LinearLayout.LayoutParams(wide, wide));

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckedTextView) v).isChecked();
                ((CheckedTextView) v).setChecked(checked ^= true);
                String mediaId = (String) v.getTag();
                if(mediaId != null)
                    if(checked)
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

        contentManager.getMediaImage(cursor.getString(imageIndex), holder.btnImage);
        setText(holder.tvCommented, cursor.getString(commentedIndex));
        setText(holder.tvLiked, cursor.getString(likedIndex));
        setText(holder.tvCreated, formatTime(cursor.getString(createdIndex)));
        holder.btnLike.setChecked(cursor.getInt(myLikeIndex) > 0);
        holder.btnLike.setTag(cursor.getString(mediaIdIndex));


        // TODO now cursor position is at fresh next id row
        // TODO go 4 time down through Cursor and fill comments

        // cache current media id
        String currentMediaId = cursor.getString(mediaIdIndex);

        int index = 0;
        // first comment
        holder.commentViews[index].setText(mixComment(cursor));
        holder.commentViews[index].setVisibility(View.VISIBLE);

//        int currentPosition = cursor.getPosition();
//
//        while(cursor.moveToPrevious()) {
//            // if it is other media row
//            if(!currentMediaId.equals(cursor.getString(mediaIdIndex))) {
//                cursor.moveToNext();
//                break;
//            }
//            //
//            holder.commentViews[++index].setText(mixComment(cursor));
//            holder.commentViews[++index].setVisibility(View.VISIBLE);
//            // TODO fill other comments
//        }
//
//        cursor.moveToPosition(currentPosition);

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
        this.textIndex = cursor.getColumnIndex(InstaContract.Comments.COMMENTS_COMMENT_TEXT);
        this.usernameIndex = cursor.getColumnIndex(InstaContract.Comments.COMMENTS_USERNAME);
        this.commentCreatedIndex = cursor.getColumnIndex(InstaContract.Comments.COMMENTS_COMMENT_CREATED);
    }

    private void setText(TextView view, String text) {
        view.setText(view.getText() + text);
    }

    private String mixComment(Cursor cursor) {
        String mix;
        String created = formatTime(cursor.getString(commentCreatedIndex));
        String username = cursor.getString(usernameIndex);
        String text = cursor.getString(textIndex);

        return "[" + created + "] " + username + ": '" + text + "'";
    }

    private String formatTime(String time) {
        return Utils.millisToLocalDate(Long.valueOf(time) * 1000, Utils.FULL_TIMESTAMP_FORMAT);
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

        @InjectView(R.id.feed_item_comment_one)
        TextView tvCommentOne;

        @InjectView(R.id.feed_item_comment_two)
        TextView tvCommentTwo;

        @InjectView(R.id.feed_item_comment_three)
        TextView tvCommentThree;

        @InjectView(R.id.feed_item_comment_four)
        TextView tvCommentFour;

        @InjectView(R.id.feed_item_comment_five)
        TextView tvCommentFive;

        TextView[] commentViews;

        ItemViewHolder(View v) {
            ButterKnife.inject(this, v);

            commentViews = new TextView[] {
                    tvCommentOne,
                    tvCommentTwo,
                    tvCommentThree,
                    tvCommentFour,
                    tvCommentFive
            };

        }

    }
}
