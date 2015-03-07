package tk.atna.instagram4ik;

import retrofit.Callback;
import retrofit.http.GET;
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
    static final String DEFAULT_SCOPE = "basic+likes";

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

    static final String CONTENT_STREAM_ENDPOINT = "/v1/users/self/feed";


    static final String ACCESS_TOKEN = "access_token";
    static final String COUNT = "count";
    static final String MIN_ID = "min_id";
    static final String MAX_ID = "max_id";

    static final String DEFAULT_COUNT = "10";


    @GET(CONTENT_STREAM_ENDPOINT)
    void getUserStream(
            @Query(ACCESS_TOKEN) String token,
            @Query(COUNT) String count,
            @Query(MIN_ID) String minId,
            @Query(MAX_ID) String maxId,
            Callback<String> callback
    );

//    @GET(LOGOUT_URL)
//    void logout(@Query(ACCESS_TOKEN) String token,
//                Callback<String> callback
//    );

}
