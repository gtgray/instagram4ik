package tk.atna.instagram4ik;

import android.os.Handler;
import android.os.Looper;

public class Worker {


    // TODO add BroadcastReceiver

    abstract static class WorkTask<T> implements Runnable {

        T result = null;
        Exception exception = null;

        void execute(TaskCallback<T> callback) {
            Worker.execute(this, callback);
        }
    }

    public static <T> void execute(final WorkTask<T> task, final TaskCallback<T> callback) {

        Thread executor = new Thread(new Runnable() {
            @Override
            public void run() {
                if(task != null) {
                    task.run();
                    (new Handler(Looper.getMainLooper()))
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null)
                                    callback.onComplete(task.result, task.exception);
                            }
                        });
                }
            }
        });
        executor.start();
    }


    public interface TaskCallback<T> {

        public void onComplete(T result, Exception ex);
    }

}
