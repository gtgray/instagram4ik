package tk.atna.instagram4ik;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;

import tk.atna.instagram4ik.provider.InstaContract;

public class ContentManager {

    private static ContentManager INSTANCE;

    private Context context;

    /**
     * Instagram user access token to identify user
     * when making server api calls
     */
    private String token;

    private HttpHelper httpHelper;

    private ContentResolver contentResolver;


    private ContentManager(Context context) {
        this.context = context;
        this.httpHelper = new HttpHelper(context);
        this.contentResolver = context.getContentResolver();
    }

    /**
     * Initializes content manager.
     * It is better to give it an application context
     *
     * @param context application context
     */
    public static synchronized void init(Context context) {
        if(context == null)
            throw new NullPointerException("Can't create instance with null context");
        if(INSTANCE != null)
            throw new IllegalStateException("Can't initialize ContentManager twice");

        INSTANCE = new ContentManager(context);
    }

    /**
     * Gets only instance of content manager.
     * Can't be called from non UI thread
     *
     * @return content manager instance
     */
    public static ContentManager get() {
        if(Looper.myLooper() != Looper.getMainLooper())
            throw new IllegalStateException("Must be called from UI thread");

        if(INSTANCE == null)
            throw new IllegalStateException("ContentManager is null. It must have been"
                    + " created at application init");

        return INSTANCE;
    }

    /**
     * Rememberes user access token
     *
     * @param token token to remember
     */
    public void rememberToken(String token) {
        this.token = token;
    }

    /**
     * Returns user access token
     *
     * @return user access token
     */
    public String getToken() {
        return token;
    }

    /**
     * Refreshes user feed
     */
    public void getFeed() {
        loadFeedAsync(null, null);
     }

    /**
     * Retrieves medias later then with id
     *
     * @param minId min media id to load later medias
     */
    public void getFeedLater(String minId) {
        loadFeedAsync(minId, null);
     }

    /**
     * Retrieves medias earlier then with id
     *
     * @param maxId max media id to load earlier medias
     */
    public void getFeedEarlier(String maxId) {
        loadFeedAsync(null, maxId);
     }

     /**
     * Refreshes data for media with id
     *
     * @param mediaId id of media to get
     */
    public void getMediaDetails(String mediaId) {
        loadMediaAsync(mediaId);
        loadLikesAsync(mediaId);
    }

    /**
     * Refreshes list of comments for media with id
     *
     * @param mediaId id of media
     */
    public void getComments(String mediaId) {
        loadCommentsAsync(mediaId);
    }

    /**
     * Likes media with id
     *
     * @param mediaId id id of media to like
     */
    public void like(String mediaId) {
        likeAsync(mediaId);
    }

    /**
     * Unlikes media with id
     *
     * @param mediaId id id of media to unlike
     */
    public void unlike(String mediaId) {
        unlikeAsync(mediaId);
    }

    /**
     * Drops image with url into view
     *
     * @param url image url to get from
     * @param view image view to drop into
     */
    public void getImage(String url, ImageView view) {
        loadImageAsync(url, view);
    }

    /**
     * Makes async sqlite query to get data about single media with id
     * and translates from cursor into detailed media object
     *
     * @param mediaId id of media to get
     * @param callback callback to return object with media data
     */
    public void pullMediaFromCache(final String mediaId,
                                    final ContentCallback<DetailedMedia> callback) {

        (new Worker.Task<DetailedMedia>() {
            @Override
            public void run() {
                Cursor cursor = contentResolver.query(Uri.withAppendedPath(
                        InstaContract.Feed.CONTENT_URI, mediaId), null, null, null, null);

                this.result = ContentMapper.cursorToDetailedMedia(cursor);
                cursor.close();
            }
        }).execute(new Worker.Task.Callback<DetailedMedia>() {
            @Override
            public void onComplete(DetailedMedia result, Exception ex) {
                callback.onResult(result, ex);
            }
        });
    }

    /**
     * Makes async sqlite query to get limited (see InstaContract for tuning)
     * list of comments for media with id
     *
     * @param mediaId id of media to get comments
     * @param callback callback to return cursor with list of likes
     */
    public void pullLimitedCommentsFromCache(final String mediaId,
                                             final ContentCallback<Cursor> callback) {
        (new Worker.Task<Cursor>() {
            @Override
            public void run() {
                this.result = contentResolver.query(Uri.withAppendedPath(
                                InstaContract.Comments.CONTENT_URI_LIMITED, mediaId),
                        null, null, null, null);
            }
        }).execute(new Worker.Task.Callback<Cursor>() {
            @Override
            public void onComplete(Cursor result, Exception ex) {
                callback.onResult(result, ex);
            }
        });
    }

    /**
     * Makes async sqlite query to get list of likes for media with id
     *
     * @param mediaId id of media to get likes
     * @param callback callback to return cursor with list of likes
     */
    public void pullLikesFromCache(final String mediaId,
                                   final ContentCallback<Cursor> callback) {
        (new Worker.Task<Cursor>() {
            @Override
            public void run() {
                this.result = contentResolver.query(Uri.withAppendedPath(
                                InstaContract.Likes.CONTENT_URI, mediaId),
                        null, null, null, null);
            }
        }).execute(new Worker.Task.Callback<Cursor>() {
            @Override
            public void onComplete(Cursor result, Exception ex) {
                callback.onResult(result, ex);
            }
        });
    }

    /**
     * Makes async instagram server api call to get medias feed
     * and pushes it into sqlite
     *
     * @param minId min media id to load later medias
     * @param maxId max media id to load earlier medias
     */
    private void loadFeedAsync(final String minId, final String maxId) {

        (new Worker.SimpleTask() {
            @Override
            public void run() {
                Envelope envelop = httpHelper.getFeed(token, minId, maxId);

                for(Envelope.Media media : envelop.data) {
                    // remember media
                    ContentMapper.pushMediaToProvider(contentResolver, media);
                    // add comments if exists
                    for(Envelope.Media.Comments.Comment comment : media.comments.data) {
                        ContentMapper.pushCommentToProvider(contentResolver, media.id, comment);
                    }
                }
            }
        }).execute(new Worker.SimpleTask.Callback() {
            @Override
            public void onComplete() {
                notifyChanges(LocalBroadcaster.ACTION_REFRESH_FEED);
            }
        });
    }

    /**
     * Makes async instagram server api call to get data
     * for media with id and pushes it into sqlite
     *
     * @param mediaId id of media to get
     */
    private void loadMediaAsync(final String mediaId) {

        (new Worker.SimpleTask() {
            @Override
            public void run() {
                Envelope.Media media = httpHelper.getMedia(token, mediaId).data;
                // remember media
                ContentMapper.pushMediaToProvider(contentResolver, media);
                // add comments if exists
                for(Envelope.Media.Comments.Comment comment : media.comments.data) {
                    ContentMapper.pushCommentToProvider(contentResolver, media.id, comment);
                }
            }
        }).execute(new Worker.SimpleTask.Callback() {
            @Override
            public void onComplete() {
                notifyChanges(LocalBroadcaster.ACTION_REFRESH_MEDIA);
            }
        });
    }

    /**
     * Makes async instagram server api call to get list of comments
     * for media with id and pushes them into sqlite
     *
     * @param mediaId id of media to get comments
     */
    private void loadCommentsAsync(final String mediaId) {

        (new Worker.SimpleTask() {
            @Override
            public void run() {
                Envelope.Media.Comments comments = httpHelper.getComments(token, mediaId);

                for(Envelope.Media.Comments.Comment comment : comments.data) {
                    ContentMapper.pushCommentToProvider(contentResolver, mediaId, comment);
                }
            }
        }).execute(null);
    }

    /**
     * Makes async instagram server api call to get list of likes
     * for media with id and pushes them into sqlite
     *
     * @param mediaId id of media to get likes
     */
    private void loadLikesAsync(final String mediaId) {

        (new Worker.SimpleTask() {
            @Override
            public void run() {
                Envelope.Media.Likes likes = httpHelper.getLikes(token, mediaId);

                for(Envelope.Media.User like : likes.data) {
                    ContentMapper.pushLikeToProvider(contentResolver, mediaId, like);
                }
            }
        }).execute(new Worker.SimpleTask.Callback() {
            @Override
            public void onComplete() {
                notifyChanges(LocalBroadcaster.ACTION_REFRESH_LIKES);
            }
        });
    }

    /**
     * Makes async instagram server api call to like media
     * with id and notifies fragments
     *
     * @param mediaId id of media to like
     */
    private void likeAsync(final String mediaId) {

        (new Worker.SimpleTask() {
            @Override
            public void run() {
                // make like
                httpHelper.like(token, mediaId);
            }
        }).execute(new Worker.SimpleTask.Callback() {
            @Override
            public void onComplete() {
                Bundle data = new Bundle();
                data.putString(LocalBroadcaster.MEDIA_ID, mediaId);
                notifyChanges(LocalBroadcaster.ACTION_REFRESH, data);
            }
        });
    }

    /**
     * Makes async instagram server api call to unlike media with id
     *
     * @param mediaId id of media to unlike
     */
    private void unlikeAsync(final String mediaId) {

        (new Worker.SimpleTask() {
            @Override
            public void run() {
                // make unlike
                httpHelper.unlike(token, mediaId);
            }
        }).execute(new Worker.SimpleTask.Callback() {
            @Override
            public void onComplete() {
                Bundle data = new Bundle();
                data.putString(LocalBroadcaster.MEDIA_ID, mediaId);
                notifyChanges(LocalBroadcaster.ACTION_REFRESH, data);
            }
        });
    }

    /**
     * Loads image with url from cache/sd/server and shows it in view
     *
     * @param url image url to load from
     * @param view image view to load into
     */
    private void loadImageAsync(String url, ImageView view) {
        httpHelper.loadImage(url, view);
    }

    /**
     * Sends local broadcast notification
     *
     * @param action action to process
     */
    private void notifyChanges(int action) {
        LocalBroadcaster.sendLocalBroadcast(action, null, context);
    }

    /**
     * Sends local broadcast notification with data
     *
     * @param action action to process
     * @param data data to act with
     */
    private void notifyChanges(int action, Bundle data) {
        LocalBroadcaster.sendLocalBroadcast(action, data, context);
    }

    /**
     * Content manager callback to return data after async extraction
     *
     * @param <T> Object to receive as a result
     */
    public interface ContentCallback<T> {
        /**
         * Fires when async data extraction is completed and data/exception
         * is ready to be returned
         *
         * @param result received result
         * @param exception possible exception
         */
        public void onResult(T result, Exception exception);
    }

}
