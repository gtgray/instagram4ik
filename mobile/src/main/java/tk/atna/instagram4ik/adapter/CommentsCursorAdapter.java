package tk.atna.instagram4ik.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tk.atna.instagram4ik.ContentManager;
import tk.atna.instagram4ik.R;
import tk.atna.instagram4ik.Utils;
import tk.atna.instagram4ik.provider.InstaContract;


public class CommentsCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;

//    private int mediaIdIndex;
    private int textIndex;
    private int usernameIndex;
    private int pictureIndex;
    private int commentCreatedIndex;

    private ContentManager contentManager;


    public CommentsCursorAdapter(Context context, Cursor cursor, ContentManager contentManager) {
        super(context, cursor, 0);

        this.inflater = LayoutInflater.from(context);
        this.contentManager = contentManager;

        rememberColumns(cursor);
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.fragment_details_comment_item, parent, false);
        final ItemViewHolder holder = new ItemViewHolder(view);

        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ItemViewHolder holder = (ItemViewHolder) view.getTag();

        contentManager.getImage(cursor.getString(pictureIndex), holder.ivPicture);
        holder.tvUsername.setText(cursor.getString(usernameIndex));
        holder.tvCreated.setText(formatTime(cursor.getString(commentCreatedIndex)));
        holder.tvText.setText(cursor.getString(textIndex));
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);

        rememberColumns(cursor);
    }

    private void rememberColumns(Cursor cursor) {
        if(cursor == null)
            return;

//        this.mediaIdIndex = cursor.getColumnIndex(InstaContract.Comments.COMMENTS_MEDIA_ID);
        this.pictureIndex = cursor.getColumnIndex(InstaContract.Comments.COMMENTS_PICTURE);
        this.usernameIndex = cursor.getColumnIndex(InstaContract.Comments.COMMENTS_USERNAME);
        this.textIndex = cursor.getColumnIndex(InstaContract.Comments.COMMENTS_COMMENT_TEXT);
        this.commentCreatedIndex = cursor.getColumnIndex(
                InstaContract.Comments.COMMENTS_COMMENT_CREATED);
    }

    private String formatTime(String time) {
        return Utils.millisToLocalDate(Long.valueOf(time) * 1000, Utils.FULL_TIMESTAMP_FORMAT);
    }


    class ItemViewHolder {

        @InjectView(R.id.details_comment_user_picture)
        ImageView ivPicture;

        @InjectView(R.id.details_comment_username)
        TextView tvUsername;

        @InjectView(R.id.details_comment_created)
        TextView tvCreated;

        @InjectView(R.id.details_comment_text)
        TextView tvText;


        ItemViewHolder(View v) {
            ButterKnife.inject(this, v);
        }

    }
}
