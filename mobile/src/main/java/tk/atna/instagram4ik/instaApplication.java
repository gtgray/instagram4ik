package tk.atna.instagram4ik;

import android.app.Application;

public class InstaApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        // init content manager
        ContentManager.init(this);
    }

}
