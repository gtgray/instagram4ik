package tk.atna.instagram4ik;


import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import retrofit.RestAdapter;

public class HttpHelper {

    private final static int DEFAULT_PICTURE = R.drawable.ic_picture_default;

    private ServerApi api;

    private final Context context;


    public HttpHelper(Context context) {
        this.context = context;
        // setup retrofit
        RestAdapter ra = new RestAdapter.Builder()
                .setEndpoint(ServerApi.SERVER_URL)
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        // retrofit server api inflater
        api = ra.create(ServerApi.class);
    }

    /**
     * Makes instagram server api call to get medias feed
     * for user with token
     *
     * @param token instagram user access token
     * @param minId min media id to load later medias
     * @param maxId max media id to load earlier medias
     * @return user's feed model object (parsed from json)
     */
    public Envelope getFeed(String token, String minId, String maxId) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        return api.getFeed(token, ServerApi.DEFAULT_COUNT,
                            minId == null ? "" : minId,
                            maxId == null ? "" : maxId);
    }

    /**
     * Makes instagram server api call to get data about media
     * with id using user with token
     *
     * @param token instagram user access token
     * @param mediaId id of media to get
     * @return single media model object (parsed from json)
     */
    public Envelope.SingleMedia getMedia(String token, String mediaId) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        return api.getMedia(token, mediaId);
    }

    /**
     * Makes instagram server api call to get list of comments
     * for media with id by user with token
     *
     * @param token instagram user access token
     * @param mediaId id of media to get comments
     * @return comments model object (parsed from json)
     */
    public Envelope.Media.Comments getComments(String token, String mediaId) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        return api.getComments(token, mediaId);
    }

    /**
     * Makes instagram server api call to get list of likes
     * for media with id by user with token
     *
     * @param token instagram user access token
     * @param mediaId id of media to get likes
     * @return likes model object (parsed from json)
     */
    public Envelope.Media.Likes getLikes(String token, String mediaId) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        return api.getLikes(token, mediaId);
    }

    /**
     * Makes instagram server api call to like media with id
     * by user with token
     *
     * @param token instagram user access token
     * @param mediaId id of media to like
     */
    public void like(String token, String mediaId) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        api.like(token, mediaId);
    }

    /**
     * Makes instagram server api call to unlike media with id
     * by user with token
     *
     * @param token instagram user access token
     * @param mediaId id of media to unlike
     */
    public void unlike(String token, String mediaId) {
        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        if(mediaId == null)
            throw new IllegalArgumentException("Media id can't be null");

        api.unlike(token, mediaId);
    }

    /**
     * Loads image with url from cache/sd/server and shows it in view
     *
     * @param url image url to load from
     * @param view image view to load into
     */
    public void loadImage(String url, ImageView view) {

        Picasso.with(context)
               .load(url)
               .placeholder(DEFAULT_PICTURE)
               .error(DEFAULT_PICTURE)
               .into(view);
    }

    /**
     * Httt helper callback to return data after async loads
     *
     * @param <T> Object to receive as a result
     */
    public interface HttpCallback<T> {
        /**
         * Fires when load is completed and data/exception is ready to be returned
         *
         * @param result received result
         * @param exception possible exception
         */
        public void onResult(T result, Exception exception);
    }

}

