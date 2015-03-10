package tk.atna.instagram4ik;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.widget.ImageView;

import tk.atna.instagram4ik.provider.InstaContract;

public class ContentManager {

    private static ContentManager INSTANCE;

//    private Context context;

    private String token;

    private HttpHelper httpHelper;

    private ContentResolver contentResolver;


    private ContentManager(Context context) {
//        this.context = context;
        this.httpHelper = new HttpHelper(context);
        this.contentResolver = context.getContentResolver();
    }

    public static synchronized void init(Context context) {
        if(context == null)
            throw new NullPointerException("Can't create instance with null context");
        if(INSTANCE != null)
            throw new IllegalStateException("Can't initialize ContentManager twice");

        INSTANCE = new ContentManager(context);
    }

    public static ContentManager get() {
        if(Looper.myLooper() != Looper.getMainLooper())
            throw new IllegalStateException("Must be called from UI thread");

        if(INSTANCE == null)
            throw new IllegalStateException("ContentManager is null. It must have been created at application init");

        return INSTANCE;
    }

    public void rememberToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void getFeed(ContentCallback<Cursor> callback) {
        // get feed from sqlite
        pullFeedFromCache(callback);
        // load fresh feed from server
        loadFeed();

//        Worker.WorkTask<Cursor> task = new Worker.WorkTask<Cursor>() {
//            @Override
//            public void run() {
//
//                // TODO go to content provider
//                final Cursor cursor = contentResolver.query(InstaContract.Feed.CONTENT_URI,
//                        null, null, null, null);
//
//                // TODO CURSOR MUST ACT ON DATA CHANGED
//
//                // TODO post callback with result cursor to mainTread queue
//                (new Handler(Looper.getMainLooper())).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (callback != null) {
//                            callback.onResult(cursor, null);
//                        }
//                    }
//                });
//
//                // TODO go to server (not async)
//                Envelope envelop = httpHelper.getFeed(token, null, null);
//
//                // TODO remember result into content provider
//                for(Envelope.Media media : envelop.data) {
//                    // remember media
//                    ContentMapper.pushMediaToProvider(contentResolver, media);
//                    // add comments if exists
//                    for(Envelope.Media.Comments.Comment comment : media.comments.data) {
//                        ContentMapper.pushCommentToProvider(contentResolver, media.id, comment);
//                    }
//                }
//
//            }
//        };
//        Worker.execute(task, null);
    }

    public void pullFeedFromCache(final ContentCallback<Cursor> callback) {

        (new Worker.WorkTask<Cursor>() {
            @Override
            public void run() {
                this.result = contentResolver.query(InstaContract.Feed.CONTENT_URI,
                                                    null, null, null, null);
            }
        }).execute(new Worker.TaskCallback<Cursor>() {
            @Override
            public void onComplete(Cursor result, Exception ex) {
                callback.onResult(result, ex);
            }
        });
    }

    private void loadFeed() {

        (new Worker.WorkTask<String>() {
            @Override
            public void run() {
                Envelope envelop = httpHelper.getFeed(token, null, null);

                for(Envelope.Media media : envelop.data) {
                    // remember media
                    ContentMapper.pushMediaToProvider(contentResolver, media);
                    // add comments if exists
                    for(Envelope.Media.Comments.Comment comment : media.comments.data) {
                        ContentMapper.pushCommentToProvider(contentResolver, media.id, comment);
                    }
                }
            }
        }).execute(null);
    }

    public void getMedia(String mediaId,
                         ContentCallback<Cursor> callback) {

        pullMediaFromCache(mediaId, callback);
    }

    private void pullMediaFromCache(final String mediaId,
                                   final ContentCallback<Cursor> callback) {

        (new Worker.WorkTask<Cursor>() {
            @Override
            public void run() {
                this.result = contentResolver.query(Uri.withAppendedPath(
                        InstaContract.Feed.CONTENT_URI, mediaId), null, null, null, null);
            }
        }).execute(new Worker.TaskCallback<Cursor>() {
            @Override
            public void onComplete(Cursor result, Exception ex) {
                callback.onResult(result, ex);
            }
        });
    }

    private void pullCommentsFromCache(final ContentCallback<Cursor> callback) {

        (new Worker.WorkTask<Cursor>() {
            @Override
            public void run() {
                this.result = contentResolver.query(InstaContract.Comments.CONTENT_URI,
                                                    null, null, null, null);
            }
        }).execute(new Worker.TaskCallback<Cursor>() {
            @Override
            public void onComplete(Cursor result, Exception ex) {
                callback.onResult(result, ex);
            }
        });
    }

    private void loadComments(final String mediaId) {

        (new Worker.WorkTask<String>() {
            @Override
            public void run() {
                Envelope.Media.Comments comments = httpHelper.getComments(token, mediaId);

                for(Envelope.Media.Comments.Comment comment : comments.data) {
                    ContentMapper.pushCommentToProvider(contentResolver, mediaId, comment);
                }
            }
        }).execute(null);
    }

    public void pullLikesFromCache(final ContentCallback<Cursor> callback) {
        (new Worker.WorkTask<Cursor>() {
            @Override
            public void run() {
                this.result = contentResolver.query(InstaContract.Likes.CONTENT_URI,
                                                    null, null, null, null);
            }
        }).execute(new Worker.TaskCallback<Cursor>() {
            @Override
            public void onComplete(Cursor result, Exception ex) {
                callback.onResult(result, ex);
            }
        });
    }

    private void loadLikes(final String mediaId) {

        (new Worker.WorkTask<String>() {
            @Override
            public void run() {
                Envelope.Media.Likes likes = httpHelper.getLikes(token, mediaId);

                for(Envelope.Media.User like : likes.data) {
                    ContentMapper.pushLikeToProvider(contentResolver, mediaId, like);
                }
            }
        }).execute(null);
    }

    public void like(String mediaId) {
        httpHelper.likeAsync(token, mediaId, null);

        // TODO CURSOR MUST ACT ON DATA CHANGED
        contentResolver.notifyChange(InstaContract.Feed.CONTENT_URI, null);
    }

    public void unlike(String mediaId) {
        httpHelper.unlikeAsync(token, mediaId, null);

        // TODO CURSOR MUST ACT ON DATA CHANGED
        contentResolver.notifyChange(InstaContract.Feed.CONTENT_URI, null);
    }

    public void getMediaImage(String url, ImageView view) {
        httpHelper.getMediaImage(url, view);
    }

    public boolean cancelRequest(int id) {
//        return httpHelper.cancelRequest(id);
        return false;
    }

    public void cancelAllImages() {
//        httpHelper.cancelAllImages();
        //
    }


    public interface ContentCallback<T> {

        public void onResult(T result, Exception exception);
    }

}
