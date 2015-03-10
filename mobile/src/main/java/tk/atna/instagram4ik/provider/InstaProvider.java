package tk.atna.instagram4ik.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class InstaProvider extends ContentProvider {

    private static final String AUTHORITY = InstaContract.AUTHORITY;

    private static final String PATH_FEED = InstaContract.Feed.TABLE_FEED;
    private static final String PATH_FEED_ITEM = PATH_FEED + "/*";
    private static final String PATH_COMMENTS = InstaContract.Comments.TABLE_COMMENTS;
    private static final String PATH_COMMENTS_FEED_ITEM = PATH_COMMENTS + "/" + PATH_FEED + "/*";
    private static final String PATH_LIKES = InstaContract.Likes.TABLE_LIKES;
    private static final String PATH_LIKES_FEED_ITEM = PATH_LIKES + "/" + PATH_FEED + "/*";

    private static final int MATCH_FEED = 0x00000011;
    private static final int MATCH_FEED_ITEM = 0x00000012;
    private static final int MATCH_COMMENTS = 0x00000013;
    private static final int MATCH_COMMENTS_FEED_ITEM = 0x00000014;
    private static final int MATCH_LIKES = 0x00000015;
    private static final int MATCH_LIKES_FEED_ITEM = 0x00000016;

    private InstaDB db;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH_FEED, MATCH_FEED);
        uriMatcher.addURI(AUTHORITY, PATH_FEED_ITEM, MATCH_FEED_ITEM);
        uriMatcher.addURI(AUTHORITY, PATH_COMMENTS, MATCH_COMMENTS);
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

        Cursor cursor;
        String where;

        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {

            // feed with medias 5 times commented
            case MATCH_FEED:
                String query = "SELECT * FROM ("
                        + "    SELECT * FROM feed INNER JOIN comments"
                        + "    ON (feed.media_id = comments.media_id)"
                        + ") AS feed_comments "
                        + "WHERE feed_comments.comment_id IN ("
                        + "    SELECT comments.comment_id FROM comments"
                        + "    WHERE comments.media_id = feed_comments.media_id"
//                        + "    AND comments.text IS NOT NULL"
                        + "    ORDER BY comments.comment_created_time DESC"
                        + "    LIMIT " + InstaContract.Feed.LIMIT_COMMENTS_PER_MEDIA
                        + ") "
                        + "ORDER BY feed_comments.created_time DESC";

                cursor = db.getWritableDatabase().rawQuery(query, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;

                sortOrder = InstaContract.Feed.FEED_CREATED + " DESC";

            // common info about media with id #
            case MATCH_FEED_ITEM:
                qBuilder.setTables(InstaContract.Feed.TABLE_FEED);
                where = InstaContract.Feed.FEED_MEDIA_ID + " = '" + uri.getLastPathSegment() + "'";
                break;

            // only comments for media with id #
            case MATCH_COMMENTS_FEED_ITEM:
                qBuilder.setTables(InstaContract.Comments.TABLE_COMMENTS);
                where = InstaContract.Comments.COMMENTS_MEDIA_ID + " = '" + uri.getLastPathSegment() + "'";
                sortOrder = InstaContract.Comments.COMMENTS_COMMENT_CREATED + " DESC";
                break;

            // only likes for media with id #
            case MATCH_LIKES_FEED_ITEM:
                qBuilder.setTables(InstaContract.Likes.TABLE_LIKES);
                where = InstaContract.Likes.LIKES_MEDIA_ID + " = '" + uri.getLastPathSegment() + "'";
                break;

            default:
                return null;
        }

        qBuilder.appendWhere(where);
        cursor = qBuilder.query(db.getWritableDatabase(), projection, selection,
                                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

/*

SELECT a.* FROM articles AS a
  LEFT JOIN articles AS a2
    ON a.section = a2.section AND a.article_date <= a2.article_date
GROUP BY a.article_id
HAVING COUNT(*) <= 10;

--------------------------
select pos, name , schoole, count(school) as teamSize
from tableName
where teamSize = 4
groupby(school)

----------------------
SELECT * FROM (
    SELECT * FROM BOOK, AUTHOR
    WHERE BOOK.AUTHORID = AUTHOR.AUTHORID
) T1
WHERE T1.BOOKID IN (
    SELECT T2.BOOKID FROM BOOK T2
    WHERE T2.AUTHORID = T1.AUTHORID
    ORDER BY T2.BOOKTITLE
    LIMIT 2
)
ORDER BY T1.BOOKTITLE

 */


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;

        switch (uriMatcher.match(uri)) {
            case MATCH_FEED:
            case MATCH_COMMENTS:
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
                where = InstaContract.Feed.FEED_MEDIA_ID + " = '" + uri.getLastPathSegment() + "'";
                break;

            case MATCH_COMMENTS_FEED_ITEM:
                table = InstaContract.Comments.TABLE_COMMENTS;
                where = InstaContract.Comments.COMMENTS_MEDIA_ID + " = '" + uri.getLastPathSegment() + "'";
                break;

            case MATCH_LIKES_FEED_ITEM:
                table = InstaContract.Likes.TABLE_LIKES;
                where = InstaContract.Likes.LIKES_MEDIA_ID + " = '" + uri.getLastPathSegment() + "'";
                break;

            default:
                throw new UnsupportedOperationException("Unknown delete uri: " + uri);
        }

        selection = (selection == null || selection.length() == 0)
                ? where : selection + " AND " + where;

        int count = dBase.delete(table, selection, selectionArgs);
        notifyChange(uri);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case MATCH_FEED:
            case MATCH_COMMENTS:
            case MATCH_LIKES:
            default:
                throw new UnsupportedOperationException("Unknown update uri: " + uri);

            case MATCH_FEED_ITEM:
            case MATCH_COMMENTS_FEED_ITEM:
            case MATCH_LIKES_FEED_ITEM:
                int rows = delete(uri, null, null);
                insert(uri, values);
                return rows;
        }
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
