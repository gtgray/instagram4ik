package tk.atna.instagram4ik;

import android.app.Application;

public class InstaApplication extends Application {

//    private static InstaApplication INSTANCE;


//    public InstaApplication() {
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
