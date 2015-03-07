package tk.atna.instagram4ik;

import android.app.Application;

public class instaApplication extends Application {

//    private static instaApplication INSTANCE;


//    public instaApplication() {
//        INSTANCE = this;
//    }

    @Override
    public void onCreate() {
        super.onCreate();

        // init content helper object
        ContentManager.init(this);
    }

//    public static Context getContext() {
//        return INSTANCE;
//    }

}
