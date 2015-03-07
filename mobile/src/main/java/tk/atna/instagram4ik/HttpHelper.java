package tk.atna.instagram4ik;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.Future;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import retrofit.mime.TypedString;

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
                .setConverter(new MockConverter())
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

    public static String getLogoutUrl() {
        return ServerApi.FULL_LOGOUT_URL;
    }

    public static Uri hitTargetRedirect(String url) {
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

//    public void logout(String token, final HttpCallback<String> callback) {
//        api.logout(token, new Callback<String>() {
//            @Override
//            public void success(String s, Response response) {
//                callback.onResult(s, null);
//                Log.d("myLogs", "logout completed with status " + response.getStatus());
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
////                error.printStackTrace();
//                callback.onResult(null, error);
//            }
//        });
//    }

    public int getUserStream(String token, String minId, String maxId,
                             final HttpCallback<String> callback) {

        if(token == null)
            throw new IllegalArgumentException("Token can't be null");

        api.getUserStream(token, ServerApi.DEFAULT_COUNT,
                          minId == null ? "" : minId,
                          maxId == null ? "" : maxId,
                          new Callback<String>() {
            @Override
            public void success(String envelope, Response response) {
//                callback.onResult(envelope, null);

                Log.d("myLogs", "--------- envelop: " + envelope);

            }

            @Override
            public void failure(RetrofitError error) {
//                callback.onResult(null, error);

                Log.d("myLogs", "--------- error: " + error.getMessage());

            }
        });

        return 0;
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


    static class MockConverter implements Converter {

        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {
            BufferedInputStream bis = null;
            ByteArrayOutputStream baos = null;
            byte[] buf = new byte[1024];

            try {
                bis = new BufferedInputStream(body.in(), buf.length);
                baos = new ByteArrayOutputStream(buf.length);

                while(bis.read(buf) > -1)
                    baos.write(buf);

                byte[] result = baos.toByteArray();

                bis.close();
                bis = null;

                baos.close();
                baos = null;

                return new String(result);

            } catch (IOException e) {
                e.printStackTrace();
                closeItQuiet(bis);
                closeItQuiet(baos);
            }
            return null;
        }

        @Override
        public TypedOutput toBody(Object object) {
            return new TypedString(object.toString());
        }

        public static void closeItQuiet(Closeable c) {
            if(c != null)
                try {
                    c.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    c = null;
                }
        }
    }

}

