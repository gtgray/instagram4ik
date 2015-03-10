package tk.atna.instagram4ik;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

interface ServerApi {

    static final String SERVER_URL = "https://api.instagram.com";

    static final String AUTH_ENDPOINT = "/oauth/authorize";

    static final String CLIENT_ID = "client_id";
    static final String REDIRECT_URI = "redirect_uri";
    static final String RESPONSE_TYPE = "response_type";
    static final String SCOPE = "scope";

    static final String DEFAULT_SCHEME = "https://";
    static final String DEFAULT_REDIRECT_URI = "instgrm4ik.com";
    static final String DEFAULT_FULL_REDIRECT_URI = DEFAULT_SCHEME + DEFAULT_REDIRECT_URI;
    static final String DEFAULT_RESPONSE_TYPE = "token";
    static final String DEFAULT_SCOPE = "basic+comments+likes";

    static final String FULL_AUTH_URL = SERVER_URL + AUTH_ENDPOINT
                                + "?" + REDIRECT_URI + "=" + DEFAULT_FULL_REDIRECT_URI
                                + "&" + RESPONSE_TYPE + "=" + DEFAULT_RESPONSE_TYPE
                                + "&" + SCOPE + "=" + DEFAULT_SCOPE
                                + "&" + CLIENT_ID + "=";

    static final String BLANK_URL = "about:blank";

    static final String LOGOUT_URL = "/accounts/logout";

    static final String FULL_LOGOUT_URL = "https://instagram.com" + LOGOUT_URL;

    static final String ERROR_REASON = "error_reason";
//    static final String ERROR = "error";
//    static final String ERROR_DESC = "error_description";

    static final String USER_DENIED = "user_denied";
//    static final String ACCESS_DENIED = "access_denied";

    static final String CONTENT_FEED_ENDPOINT = "/v1/users/self/feed";
    static final String CONTENT_COMMENTS_ENDPOINT = "/v1/media/{media-id}/comments";
    static final String CONTENT_LIKES_ENDPOINT = "/v1/media/{media-id}/likes";


    static final String ACCESS_TOKEN = "access_token";
    static final String COUNT = "count";
    static final String MIN_ID = "min_id";
    static final String MAX_ID = "max_id";
    static final String MEDIA_ID = "media-id";

    static final String DEFAULT_COUNT = "10";


    @GET(CONTENT_FEED_ENDPOINT)
    Envelope getFeed(
            @Query(ACCESS_TOKEN) String token,
            @Query(COUNT) String count,
            @Query(MIN_ID) String minId,
            @Query(MAX_ID) String maxId
    );

    @GET(CONTENT_FEED_ENDPOINT)
    void getFeedAsync(
            @Query(ACCESS_TOKEN) String token,
            @Query(COUNT) String count,
            @Query(MIN_ID) String minId,
            @Query(MAX_ID) String maxId,
            Callback<Envelope> callback
    );

    @GET(CONTENT_COMMENTS_ENDPOINT)
    Envelope.Media.Comments getComments(
            @Query(ACCESS_TOKEN) String token,
            @Path(MEDIA_ID) String mediaId
    );

    @GET(CONTENT_COMMENTS_ENDPOINT)
    void getCommentsAsync(
            @Query(ACCESS_TOKEN) String token,
            @Path(MEDIA_ID) String mediaId,
            Callback<Envelope.Media.Comments> callback
    );

    @GET(CONTENT_LIKES_ENDPOINT)
    Envelope.Media.Likes getLikes(
            @Query(ACCESS_TOKEN) String token,
            @Path(MEDIA_ID) String mediaId
    );

    @GET(CONTENT_LIKES_ENDPOINT)
    void getLikesAsync(
            @Query(ACCESS_TOKEN) String token,
            @Path(MEDIA_ID) String mediaId,
            Callback<Envelope.Media.Likes> callback
    );

    @POST(CONTENT_LIKES_ENDPOINT)
    void likeAsync(
            @Query(ACCESS_TOKEN) String token,
            @Path(MEDIA_ID) String mediaId,
            Callback<Envelope.Media.Likes> callback
    );

    @DELETE(CONTENT_LIKES_ENDPOINT)
    void unlikeAsync(
            @Query(ACCESS_TOKEN) String token,
            @Path(MEDIA_ID) String mediaId,
            Callback<Envelope.Media.Likes> callback
    );



}
