package tk.atna.instagram4ik.provider;

import android.net.Uri;

public final class InstaContract {

    public static final String AUTHORITY = "tk.atna.instagram4ik.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +  AUTHORITY);

    public static final String TYPE_PREFIX = "vnd.android.cursor.dir/vnd.instagram4ik.";
    public static final String ITEM_TYPE_PREFIX = "vnd.android.cursor.item/vnd.instagram4ik.";


    private InstaContract() {
        // nothing here
    }


    /**
     * Table Feed collumns
     */
    public static class Feed {

        public static final String DEFAULT_MEDIA_TYPE = "image";

        public static final String TABLE_FEED = "feed";
        public static final String FEED_ID = "_id";
        public static final String FEED_MEDIA_ID = "media_id";
        public static final String FEED_TYPE = "type";
        public static final String FEED_CREATED = "created_time";
        public static final String FEED_I_LIKED = "i_liked";
        public static final String FEED_IMAGE_URL = "image_url";
        public static final String FEED_CAPTION = "caption";
        public static final String FEED_LIKES_COUNT = "likes_count";
        public static final String FEED_COMMENTS_COUNT = "comments_count";

        public static final String CONTENT_TYPE = TYPE_PREFIX + TABLE_FEED;
        public static final String CONTENT_ITEM_TYPE = ITEM_TYPE_PREFIX + TABLE_FEED;

        public static final Uri CONTENT_URI = InstaContract.BASE_CONTENT_URI
                .buildUpon().appendPath(TABLE_FEED).build();

    }


    /**
     * Table Comments collumns
     */
    public static class Comments {

        /**
         * Limit of comments per media in feed list
         */
        public static final String LIMIT_COMMENTS_PER_MEDIA = "5";
        public static final String LIMIT = "limit";

        public static final String TABLE_COMMENTS = "comments";
        public static final String COMMENTS_ID = "_id";
        public static final String COMMENTS_MEDIA_ID = "media_id";
        public static final String COMMENTS_USERNAME = "username";
        public static final String COMMENTS_PICTURE = "user_picture";
        public static final String COMMENTS_COMMENT_ID = "comment_id";
        public static final String COMMENTS_COMMENT_TEXT = "comment_text";
        public static final String COMMENTS_COMMENT_CREATED = "comment_created_time";

        public static final String CONTENT_TYPE = TYPE_PREFIX + TABLE_COMMENTS;
        public static final String CONTENT_ITEM_TYPE = ITEM_TYPE_PREFIX + TABLE_COMMENTS;

        public static final Uri CONTENT_URI = InstaContract.BASE_CONTENT_URI
                .buildUpon().appendPath(TABLE_COMMENTS).appendPath(Feed.TABLE_FEED).build();

        public static final Uri CONTENT_URI_LIMITED = InstaContract.BASE_CONTENT_URI
                .buildUpon().appendPath(TABLE_COMMENTS).appendPath(Feed.TABLE_FEED)
                .appendPath(LIMIT).build();

    }


    /**
     * Table Likes collumns
     */
    public static class Likes {

        public static final String TABLE_LIKES = "likes";
        public static final String LIKES_ID = "_id";
        public static final String LIKES_MEDIA_ID = "media_id";
        public static final String LIKES_USERNAME = "username";
        public static final String LIKES_PICTURE = "user_picture";

        public static final String CONTENT_TYPE = TYPE_PREFIX + TABLE_LIKES;
        public static final String CONTENT_ITEM_TYPE = ITEM_TYPE_PREFIX + TABLE_LIKES;

        public static final Uri CONTENT_URI = InstaContract.BASE_CONTENT_URI
                .buildUpon().appendPath(TABLE_LIKES).appendPath(Feed.TABLE_FEED).build();

    }


}
