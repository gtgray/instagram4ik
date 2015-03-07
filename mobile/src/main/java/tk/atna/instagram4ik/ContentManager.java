package tk.atna.instagram4ik;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Looper;

public class ContentManager {

    private static ContentManager INSTANCE;

//    private Context context;

    private String token;

    private HttpHelper httpHelper;

    private ContentResolver contentResolver;


    private ContentManager(Context context) {
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

    public boolean cancelRequest(int id) {
//        return httpHelper.cancelRequest(id);
        return false;
    }

    public void cancelAllImages() {
//        httpHelper.cancelAllImages();
        //
    }

//    public void logout(final ContentCallback<String> callback) {
//        httpHelper.logout(token, new HttpHelper.HttpCallback<String>() {
//            @Override
//            public void onResult(String result, Exception exception) {
//                callback.onResult(result, exception);
//            }
//        });
//    }


    public interface ContentCallback<T> {

        public void onResult(T result, Exception exception);
    }

}
