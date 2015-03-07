package tk.atna.instagram4ik;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.SparseArray;

import java.util.Set;
import java.util.concurrent.Future;

import retrofit.RestAdapter;

public class HttpHelper {


    private final static int DEFAULT_PICTURE = R.drawable.ic_picture_default;

    public final static String NETWORK_DOWN = "Network is unavailable at the moment";
    public final static int KEY = R.string.client_id;

    private ServerApi api;

    private final Context context;

    private SparseArray<Future> currentRequests;
//    private SparseArray<ImageViewFuture> currentIconRequests;

    private String key;


    public HttpHelper(Context context) {
        this.context = context;
        this.currentRequests = new SparseArray<>();
//        this.currentIconRequests = new SparseArray<>();

        this.key = context.getString(KEY);

        RestAdapter ra = new RestAdapter.Builder()
                .setEndpoint(ServerApi.SERVER_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        api = ra.create(ServerApi.class);

    }

    public static String getAuthUrl(String clientId) {
        return ServerApi.FULL_AUTH_URL + clientId;
    }

    public static String getBlank() {
        return ServerApi.BLANK_URL;
    }

    public static Uri parseRedirect(String url) {
        Uri uri = Uri.parse(url);
        // if needed host
        if((ServerApi.DEFAULT_REDIRECT_URI.equals(uri.getHost())))
            return uri;

        return null;
    }

    public static String parseToken(Uri uri) {
        String raw = uri.getFragment();
        if (raw != null) {
            int index = (ServerApi.ACCESS_TOKEN + "=").length();
            if (index > 0)
                return raw.substring(index);
        }

        return null;
    }

    public static Boolean isUserDenied(Uri uri) {
        Set<String> names = uri.getQueryParameterNames();
        for(String name : names) {
            if(ServerApi.ERROR_REASON.equals(name)) {
                return ServerApi.USER_DENIED.equals(uri.getQueryParameter(name));
            }
        }
        return null;
    }

    private boolean isNetworkUp() {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // network is not available at all
        if(cm == null)
            return false;

        NetworkInfo ni = cm.getActiveNetworkInfo();
        // default network is presented and it is able to connect through and it is connected now
        return (ni != null) && ni.isAvailable() && ni.isConnected();
    }



    public interface HttpCallback<T> {

        public void onResult(T result, Exception exception);
    }

}

