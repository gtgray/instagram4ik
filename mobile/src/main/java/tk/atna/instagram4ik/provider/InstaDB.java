package tk.atna.instagram4ik.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InstaDB extends SQLiteOpenHelper {

    static final String DB_NAME = "instgrm4ik.db";
    static final int DB_VERSION = 1;

    // FEED
    static final String TABLE_FEED = "feed";
    static final String FEED_ID = "_id";
    static final String FEED_MEDIA_ID = "media_id";
    static final String FEED_TYPE = "type";
    static final String FEED_CREATED = "created_time";
    static final String FEED_I_LIKED = "i_liked";
    static final String FEED_IMAGE_URL = "image_url";
    static final String FEED_CAPTION = "caption";
    static final String FEED_LIKES_COUNT = "likes_count";
    static final String FEED_COMMENTS_COUNT = "comments_count";

	private static final String DB_CREATE_TABLE_FEED = "create table "
                                + TABLE_FEED + " ("
                                + FEED_ID + " integer primary key, "
                                + FEED_MEDIA_ID + " text, "
                                + FEED_TYPE + " text, "
                                + FEED_CREATED + " text, "
                                + FEED_I_LIKED + " integer, "
                                + FEED_IMAGE_URL + " text, "
                                + FEED_CAPTION + " text, "
                                + FEED_LIKES_COUNT + " integer, "
                                + FEED_COMMENTS_COUNT + " integer);";

    private static final String DB_DROP_TABLE_FEED = "drop table if exists "
                                + TABLE_FEED + ";";

    // COMMENTS
    static final String TABLE_COMMENTS = "comments";
    static final String COMMENTS_ID = "_id";
    static final String COMMENTS_MEDIA_ID = "media_id";
    static final String COMMENTS_USERNAME = "username";
    static final String COMMENTS_PICTURE = "user_picture";
    static final String COMMENTS_COMMENT_ID = "comment_id";
    static final String COMMENTS_COMMENT_TEXT = "comment_text";
    static final String COMMENTS_COMMENT_CREATED = "comment_created_time";

	private static final String DB_CREATE_TABLE_COMMENTS = "create table "
                                + TABLE_COMMENTS + " ("
                                + COMMENTS_ID + " integer primary key, "
                                + COMMENTS_MEDIA_ID + " text, "
                                + COMMENTS_USERNAME + " text, "
                                + COMMENTS_PICTURE + " text, "
                                + COMMENTS_COMMENT_ID + " text, "
                                + COMMENTS_COMMENT_TEXT + " text, "
                                + COMMENTS_COMMENT_CREATED + " text);";

    private static final String DB_DROP_TABLE_COMMENTS = "drop table if exists "
                                + TABLE_COMMENTS + ";";

    // LIKES
    static final String TABLE_LIKES = "likes";
    static final String LIKES_ID = "_id";
    static final String LIKES_MEDIA_ID = "media_id";
    static final String LIKES_USERNAME = "username";
    static final String LIKES_PICTURE = "user_picture";

	private static final String DB_CREATE_TABLE_LIKES = "create table "
                                + TABLE_LIKES + " ("
                                + LIKES_ID + " integer primary key, "
                                + LIKES_MEDIA_ID + " text, "
                                + LIKES_USERNAME + " text, "
                                + LIKES_PICTURE + " text);";

    private static final String DB_DROP_TABLE_LIKES = "drop table if exists "
                                + TABLE_LIKES + ";";


	public InstaDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        createTableFeed(db);
        createTableComments(db);
        createTableLikes(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// nothing here
    }

    void createTableFeed(SQLiteDatabase db) {
		db.execSQL(DB_CREATE_TABLE_FEED);
    }

    void dropTableFeed(SQLiteDatabase db) {
		db.execSQL(DB_DROP_TABLE_FEED);
    }

    void createTableComments(SQLiteDatabase db) {
		db.execSQL(DB_CREATE_TABLE_COMMENTS);
    }

    void dropTableComments(SQLiteDatabase db) {
		db.execSQL(DB_DROP_TABLE_COMMENTS);
    }

    void createTableLikes(SQLiteDatabase db) {
		db.execSQL(DB_CREATE_TABLE_LIKES);
    }

    void dropTableLikes(SQLiteDatabase db) {
		db.execSQL(DB_DROP_TABLE_LIKES);
    }

}
