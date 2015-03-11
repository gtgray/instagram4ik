package tk.atna.instagram4ik.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class InstaProvider extends ContentProvider {

    private static final String AUTHORITY = InstaContract.AUTHORITY;

    private static final String PATH_FEED = InstaContract.Feed.TABLE_FEED;
    private static final String PATH_FEED_ITEM = PATH_FEED + "/*";
    private static final String PATH_COMMENTS = InstaContract.Comments.TABLE_COMMENTS;
    private static final String PATH_LIMIT = InstaContract.Comments.LIMIT;
    private static final String PATH_COMMENTS_FEED_ITEM_LIMITED = PATH_COMMENTS + "/"
                                                 + PATH_FEED + "/" + PATH_LIMIT + "/*";
    private static final String PATH_COMMENTS_FEED_ITEM = PATH_COMMENTS + "/" + PATH_FEED + "/*";
    private static final String PATH_LIKES = InstaContract.Likes.TABLE_LIKES;
    private static final String PATH_LIKES_FEED_ITEM = PATH_LIKES + "/" + PATH_FEED + "/*";

    private static final int MATCH_FEED = 0x00000011;
    private static final int MATCH_FEED_ITEM = 0x00000012;
    private static final int MATCH_COMMENTS = 0x00000013;
    private static final int MATCH_COMMENTS_FEED_ITEM_LIMITED = 0x00000014;
    private static final int MATCH_COMMENTS_FEED_ITEM = 0x00000015;
    private static final int MATCH_LIKES = 0x00000016;
    private static final int MATCH_LIKES_FEED_ITEM = 0x00000017;

    private InstaDB db;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH_FEED, MATCH_FEED);
        uriMatcher.addURI(AUTHORITY, PATH_FEED_ITEM, MATCH_FEED_ITEM);
        uriMatcher.addURI(AUTHORITY, PATH_COMMENTS, MATCH_COMMENTS);
        uriMatcher.addURI(AUTHORITY, PATH_COMMENTS_FEED_ITEM_LIMITED, MATCH_COMMENTS_FEED_ITEM_LIMITED);
        uriMatcher.addURI(AUTHORITY, PATH_COMMENTS_FEED_ITEM, MATCH_COMMENTS_FEED_ITEM);
        uriMatcher.addURI(AUTHORITY, PATH_LIKES, MATCH_LIKES);
        uriMatcher.addURI(AUTHORITY, PATH_LIKES_FEED_ITEM, MATCH_LIKES_FEED_ITEM);
    }


    @Override
    public boolean onCreate() {
        db = new InstaDB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        String table;
        String where = null;
        String limit = null;

        switch (uriMatcher.match(uri)) {

            // feed with medias
            case MATCH_FEED:
                table = InstaContract.Feed.TABLE_FEED;
                sortOrder = InstaContract.Feed.FEED_CREATED + " DESC";
                break;

            // common info about media with id #
            case MATCH_FEED_ITEM:
                table = InstaContract.Feed.TABLE_FEED;
                where = InstaContract.Feed.FEED_MEDIA_ID + " = '" + uri.getLastPathSegment() + "'";
                break;

            // limited number of comments for media with id #
            case MATCH_COMMENTS_FEED_ITEM_LIMITED:
                table = InstaContract.Comments.TABLE_COMMENTS;
                where = InstaContract.Comments.COMMENTS_MEDIA_ID + " = '" + uri.getLastPathSegment() + "'";
                sortOrder = InstaContract.Comments.COMMENTS_COMMENT_CREATED + " DESC";
                limit = InstaContract.Comments.LIMIT_COMMENTS_PER_MEDIA;
                break;

            // comments for media with id #
            case MATCH_COMMENTS_FEED_ITEM:
                table = InstaContract.Comments.TABLE_COMMENTS;
                where = InstaContract.Comments.COMMENTS_MEDIA_ID + " = '" + uri.getLastPathSegment() + "'";
                sortOrder = InstaContract.Comments.COMMENTS_COMMENT_CREATED + " DESC";
                break;

            // likes for media with id #
            case MATCH_LIKES_FEED_ITEM:
                table = InstaContract.Likes.TABLE_LIKES;
                where = InstaContract.Likes.LIKES_MEDIA_ID + " = '" + uri.getLastPathSegment() + "'";
                break;

            default:
                return null;
        }

        Cursor cursor = db.getWritableDatabase().query(table, null, where, null, null, null,
                                                        sortOrder, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;

        switch (uriMatcher.match(uri)) {
            case MATCH_FEED:
            case MATCH_COMMENTS:
            case MATCH_COMMENTS_FEED_ITEM_LIMITED:
            case MATCH_LIKES:
            default:
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);

            case MATCH_FEED_ITEM:
                table = InstaContract.Feed.TABLE_FEED;
                break;

            case MATCH_COMMENTS_FEED_ITEM:
                table = InstaContract.Comments.TABLE_COMMENTS;
                break;

            case MATCH_LIKES_FEED_ITEM:
                table = InstaContract.Likes.TABLE_LIKES;
                break;
        }

        // insert row
        long row = db.getWritableDatabase().insert(table, null, values);
        notifyChange(uri);

        return ContentUris.withAppendedId(uri, row);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase dBase = db.getWritableDatabase();
        String table;
        String where;

        switch (uriMatcher.match(uri)) {
            case MATCH_FEED:
                db.dropTableFeed(dBase);
                db.createTableFeed(dBase);
                return 0;

            case MATCH_COMMENTS:
                db.dropTableComments(dBase);
                db.createTableComments(dBase);
                return 0;

            case MATCH_LIKES:
                db.dropTableLikes(dBase);
                db.createTableLikes(dBase);
                return 0;

            case MATCH_FEED_ITEM:
                table = InstaContract.Feed.TABLE_FEED;
                where = InstaContract.Feed.FEED_MEDIA_ID + " = '"
                        + uri.getLastPathSegment() + "'";
                break;

            case MATCH_COMMENTS_FEED_ITEM:
                table = InstaContract.Comments.TABLE_COMMENTS;
                where = InstaContract.Comments.COMMENTS_MEDIA_ID + " = '"
                        + uri.getLastPathSegment() + "'";
                break;

            case MATCH_LIKES_FEED_ITEM:
                table = InstaContract.Likes.TABLE_LIKES;
                where = InstaContract.Likes.LIKES_MEDIA_ID + " = '"
                        + uri.getLastPathSegment() + "'";
                break;

            case MATCH_COMMENTS_FEED_ITEM_LIMITED:
            default:
                throw new UnsupportedOperationException("Unknown delete uri: " + uri);
        }

        selection = (selection == null || selection.length() == 0)
                ? where : selection + " AND " + where;

        int count = dBase.delete(table, selection, null);
        notifyChange(uri);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String where = null;

        switch (uriMatcher.match(uri)) {
            case MATCH_FEED:
            case MATCH_COMMENTS:
            case MATCH_COMMENTS_FEED_ITEM_LIMITED:
            case MATCH_LIKES:
            default:
                throw new UnsupportedOperationException("Unknown update uri: " + uri);

            case MATCH_FEED_ITEM:
                break;

            case MATCH_COMMENTS_FEED_ITEM:
                where = InstaContract.Comments.COMMENTS_COMMENT_ID + " = '"
                        + values.getAsString(InstaContract.Comments.COMMENTS_COMMENT_ID) + "'";
                break;

            case MATCH_LIKES_FEED_ITEM:
                where = InstaContract.Likes.LIKES_USERNAME + " = '"
                        + values.getAsString(InstaContract.Likes.LIKES_USERNAME) + "'";
                break;
        }
            int rows = delete(uri, where, null);
            insert(uri, values);
            return rows;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {

            case MATCH_FEED:
                return InstaContract.Feed.CONTENT_TYPE;

            case MATCH_FEED_ITEM:
                return InstaContract.Feed.CONTENT_ITEM_TYPE;

            case MATCH_COMMENTS:
                return InstaContract.Comments.CONTENT_TYPE;

            case MATCH_COMMENTS_FEED_ITEM_LIMITED:
                return InstaContract.Comments.CONTENT_TYPE;

            case MATCH_COMMENTS_FEED_ITEM:
                return InstaContract.Comments.CONTENT_TYPE;

            case MATCH_LIKES:
                return InstaContract.Likes.CONTENT_TYPE;

            case MATCH_LIKES_FEED_ITEM:
                return InstaContract.Likes.CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

}
