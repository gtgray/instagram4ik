package tk.atna.instagram4ik;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

interface ServerApi {

    static final String SERVER_URL = "https://api.instagram.com";

    static final String SERVER_AUTH_URL = "/oauth/authorize";

    static final String CLIENT_ID = "client_id";
    static final String REDIRECT_URI = "redirect_uri";
    static final String RESPONSE_TYPE = "response_type";
    static final String SCOPE = "scope";

    static final String DEFAULT_SCHEME = "https://";
    static final String DEFAULT_REDIRECT_URI = "instgrm4ik.com";
    static final String DEFAULT_FULL_REDIRECT_URI = DEFAULT_SCHEME + DEFAULT_REDIRECT_URI;
    static final String DEFAULT_RESPONSE_TYPE = "token";
    static final String DEFAULT_SCOPE = "basic+likes";

    static final String FULL_AUTH_URL = SERVER_URL + SERVER_AUTH_URL
                                + "?" + REDIRECT_URI + "=" + DEFAULT_FULL_REDIRECT_URI
                                + "&" + RESPONSE_TYPE + "=" + DEFAULT_RESPONSE_TYPE
                                + "&" + SCOPE + "=" + DEFAULT_SCOPE
                                + "&" + CLIENT_ID + "=";

    static final String ERROR_REASON = "error_reason";
//    static final String ERROR = "error";
//    static final String ERROR_DESC = "error_description";

    static final String USER_DENIED = "user_denied";
//    static final String ACCESS_DENIED = "access_denied";

    static final String BLANK_URL = "about:blank";

    static final String SERVER_MEDIA_URL = "/v1";
    static final String ENDPOINT_MEDIA_STREAM = "/users/self/feed";

    static final String ACCESS_TOKEN = "access_token";
    static final String COUNT = "count";



    static final int DEFAULT_COUNT = 10;


    @GET(SERVER_AUTH_URL)
    void authorize(
            @Query(CLIENT_ID) String clientId,
            @Query(REDIRECT_URI) String redirectUri,
            @Query(RESPONSE_TYPE) String responseType,
            Callback<JsonObject> callback
    );

    @GET("")
    void getUserStream(
            @Query(ACCESS_TOKEN) String token,
            @Query(COUNT) int count,
            Callback<Envelope> callback
    );



}
