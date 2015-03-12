package tk.atna.instagram4ik;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import tk.atna.instagram4ik.provider.InstaContract;

public class ContentMapper {

    private static final int LOW_RESOLUTION = 0x00000010;
    private static final int THUMB_RESOLUTION = 0x00000011;
    private static final int STANDARD_RESOLUTION = 0x00000012;

    private static final int DEFAULT_IMAGE_RESOLUTION = STANDARD_RESOLUTION;


    /**
     * Pushes single media into sqlite
     *
     * @param cr values to add
     * @param media media model object
     */
    public static void pushMediaToProvider(ContentResolver cr, Envelope.Media media) {

        if(cr == null || media == null)
            throw new IllegalArgumentException("One or more arguments are null");

        // media type is not 'image'
        if(!InstaContract.Feed.DEFAULT_MEDIA_TYPE.equals(media.type))
            return;

        ContentValues cv = new ContentValues();
        cv.put(InstaContract.Feed.FEED_MEDIA_ID, media.id);
        cv.put(InstaContract.Feed.FEED_TYPE, media.type);
        cv.put(InstaContract.Feed.FEED_CREATED, media.createdTime);
        cv.put(InstaContract.Feed.FEED_I_LIKED, media.iLiked ? 1 : 0);
        cv.put(InstaContract.Feed.FEED_CAPTION, media.caption == null
                ? "" : media.caption.text);
        cv.put(InstaContract.Feed.FEED_IMAGE_URL,
                getImageWithResolution(media.images, DEFAULT_IMAGE_RESOLUTION).url);
        cv.put(InstaContract.Feed.FEED_COMMENTS_COUNT, media.comments == null
                ? 0 : media.comments.count);
        cv.put(InstaContract.Feed.FEED_LIKES_COUNT, media.likes == null
                ? 0 : media.likes.count);
        // update values
        cr.update(Uri.withAppendedPath(InstaContract.Feed.CONTENT_URI,
                media.id), cv, null, null);
    }

    /**
     * Pushes single comment into sqlite
     *
     * @param cr values to add
     * @param mediaId id of media to add comment for
     * @param comment comment model object
     */
    public static void pushCommentToProvider(ContentResolver cr, String mediaId,
                                             Envelope.Media.Comments.Comment comment) {

        if(cr == null || mediaId == null || comment == null)
            throw new IllegalArgumentException("One or more arguments are null");

        ContentValues cv = new ContentValues();
        cv.put(InstaContract.Comments.COMMENTS_MEDIA_ID, mediaId);
        cv.put(InstaContract.Comments.COMMENTS_USERNAME, comment.from.username);
        cv.put(InstaContract.Comments.COMMENTS_PICTURE, comment.from.picture);
        cv.put(InstaContract.Comments.COMMENTS_COMMENT_ID, comment.id);
        cv.put(InstaContract.Comments.COMMENTS_COMMENT_TEXT, comment.text);
        cv.put(InstaContract.Comments.COMMENTS_COMMENT_CREATED, comment.createdTime);
        // update values
        cr.update(Uri.withAppendedPath(InstaContract.Comments.CONTENT_URI,
                mediaId), cv, null, null);
    }

    /**
     * Pushes single like into sqlite
     *
     * @param cr values to add
     * @param mediaId id of media to add like for
     * @param like like model object
     */
    public static void pushLikeToProvider(ContentResolver cr, String mediaId,
                                             Envelope.Media.User like) {

        if(cr == null || mediaId == null || like == null)
            throw new IllegalArgumentException("One or more arguments are null");

        ContentValues cv = new ContentValues();
        cv.put(InstaContract.Likes.LIKES_MEDIA_ID, mediaId);
        cv.put(InstaContract.Likes.LIKES_USERNAME, like.username);
        cv.put(InstaContract.Likes.LIKES_PICTURE, like.picture);
        // update values
        cr.update(Uri.withAppendedPath(InstaContract.Likes.CONTENT_URI,
                mediaId), cv, null, null);
    }

    /**
     * Converts media data from cursor into DetailedMedia object representation
     *
     * @param cursor cursor to take media data from
     * @return media in DetailedMedia
     */
    public static DetailedMedia cursorToDetailedMedia(Cursor cursor) {
        if(cursor != null && cursor.moveToFirst()) {
            if(cursor.getCount() == 1) {
                return new DetailedMedia(cursor.getString(
                        cursor.getColumnIndex(InstaContract.Feed.FEED_MEDIA_ID)))
                    .setImageUrl(cursor.getString(
                            cursor.getColumnIndex(InstaContract.Feed.FEED_IMAGE_URL)))
                    .setiLiked(cursor.getInt(
                            cursor.getColumnIndex(InstaContract.Feed.FEED_I_LIKED)) > 0)
                    .setLikesCount(cursor.getInt(
                            cursor.getColumnIndex(InstaContract.Feed.FEED_LIKES_COUNT)))
                    .setCommentsCount(cursor.getInt(
                            cursor.getColumnIndex(InstaContract.Feed.FEED_COMMENTS_COUNT)))
                    .setCreatedTime(cursor.getString(
                            cursor.getColumnIndex(InstaContract.Feed.FEED_CREATED)))
                    .setCaption(cursor.getString(
                            cursor.getColumnIndex(InstaContract.Feed.FEED_CAPTION)));
            }
        }
        return null;
    }

    /**
     * Gets image object according to resolution
     *
     * @param images set of images
     * @param resolution resolution to choose
     * @return image model object
     */
    private static Envelope.Media.Images.Image getImageWithResolution(
            Envelope.Media.Images images, int resolution) {

        switch (resolution) {
            case THUMB_RESOLUTION:
                return images.thumb;

            default:
            case LOW_RESOLUTION:
                return images.low;

            case STANDARD_RESOLUTION:
                return images.standard;
        }
    }

}
