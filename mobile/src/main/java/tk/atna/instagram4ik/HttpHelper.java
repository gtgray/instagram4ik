package tk.atna.instagram4ik;


import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HttpHelper {


    private final static int DEFAULT_PICTURE = R.drawable.ic_picture_default;

    public final static String NETWORK_DOWN = "Network is unavailable at the moment";
    public final static int KEY = R.string.client_id;

    private ServerApi api;

    private final Context context;

//    private SparseArray<Future> currentRequests;
//    private SparseArray<ImageViewFuture> currentIconRequests;

    private String key;


    public HttpHelper(Context context) {
        this.context = context;
//        this.currentRequests = new SparseArray<>();
//        this.currentIconRequests = new SparseArray<>();

        this.key = context.getString(KEY);

        RestAdapter ra = new RestAdapter.Builder()
                .setEndpoint(ServerApi.SERVER_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        api = ra.create(ServerApi.class);

    }

    public Envelope getFeed(String token, String minId, String maxId) {
        if(token == null) {
            throw new IllegalArgumentException("Token can't be null");
        }

        return api.getFeed(token, ServerApi.DEFAULT_COUNT,
                            minId == null ? "" : minId,
                            maxId == null ? "" : maxId);
    }

    public int getFeedAsync(String token, String minId, String maxId,
                            final HttpCallback<Envelope> callback) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        api.getFeedAsync(token, ServerApi.DEFAULT_COUNT,
                          minId == null ? "" : minId,
                          maxId == null ? "" : maxId,
                          new Callback<Envelope>() {
            @Override
            public void success(Envelope envelope, Response response) {
                callback.onResult(envelope, null);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                callback.onResult(null, error);
            }
        });

        return 0;
    }

    public Envelope.Media.Comments getComments(String token, String mediaId) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        return api.getComments(token, mediaId);
    }

    public int getCommentsAsync(String token, String mediaId,
                                final HttpCallback<Envelope.Media.Comments> callback) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        api.getCommentsAsync(token, mediaId, new Callback<Envelope.Media.Comments>() {
            @Override
            public void success(Envelope.Media.Comments comments, Response response) {
                callback.onResult(comments, null);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                callback.onResult(null, error);
            }
        });

        return 0;
    }

    public Envelope.Media.Likes getLikes(String token, String mediaId) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        return api.getLikes(token, mediaId);
    }

    public int getLikesAsync(String token, String mediaId,
                             final HttpCallback<Envelope.Media.Likes> callback) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        api.getLikesAsync(token, mediaId, new Callback<Envelope.Media.Likes>() {
            @Override
            public void success(Envelope.Media.Likes likes, Response response) {
                if(callback != null)
                    callback.onResult(likes, null);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                if(callback != null)
                    callback.onResult(null, error);
            }
        });

        return 0;
    }

    public int likeAsync(String token, String mediaId,
                         final HttpCallback<Envelope.Media.Likes> callback) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        api.likeAsync(token, mediaId, new Callback<Envelope.Media.Likes>() {
            @Override
            public void success(Envelope.Media.Likes likes, Response response) {
                if(callback != null)
                    callback.onResult(likes, null);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                if(callback != null)
                    callback.onResult(null, error);
            }
        });

        return 0;
    }

    public int unlikeAsync(String token, String mediaId,
                             final HttpCallback<Envelope.Media.Likes> callback) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        api.unlikeAsync(token, mediaId, new Callback<Envelope.Media.Likes>() {
            @Override
            public void success(Envelope.Media.Likes likes, Response response) {
                if(callback != null)
                    callback.onResult(likes, null);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                if(callback != null)
                    callback.onResult(null, error);
            }
        });

        return 0;
    }

    public void loadImage(String url, ImageView view) {

        Picasso.with(context)
               .load(url)
               .placeholder(DEFAULT_PICTURE)
               .error(DEFAULT_PICTURE)
               .into(view);
    }


    public interface HttpCallback<T> {

        public void onResult(T result, Exception exception);
    }

}

