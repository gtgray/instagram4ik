package tk.atna.instagram4ik;

import android.os.*;
import android.os.Process;

public class Worker {

    /**
     * Task that can be performed asynchronously
     *
     * @param <T> type of object to return
     */
    abstract static class Task<T> implements Runnable {

        T result = null;
        Exception exception = null;

        /**
         * Method to run this task
         *
         * @param callback where to return the result on complete
         */
        void execute(Callback<T> callback) {
            Worker.execute(this, callback);
        }


        /**
         * On complete task listener
         */
        public interface Callback<T> {
            /**
             * Returns task complete results or exception
             *
             * @param result result of task running
             * @param ex exception
             */
            public void onComplete(T result, Exception ex);
        }
    }

    /**
     * Task that can be performed asynchronously. Returns no result.
     *
     */
    abstract static class SimpleTask implements Runnable {

        /**
         * Method to run this task
         *
         * @param callback flag to fire on complete
         */
        void execute(Callback callback) {
            Worker.execute(this, callback);
        }

        /**
         * On complete task listener
         */
        public interface Callback {
            /**
             * Flag that fires on runnning completed
             */
            public void onComplete();
        }
    }

    /**
     * Performs async task execution in separate thread
     *
     * @param task task to run async
     * @param callback listener to return result through
     * @param <T> type of result to return
     */
    public static <T> void execute(final Task<T> task,
                                   final Task.Callback<T> callback) {

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
        executor.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        executor.start();
    }

    /**
     * Performs async task execution in separate thread. Returns no result.
     *
     * @param task task to run async
     * @param callback listener to fire when task is completed
     */
    public static void execute(final SimpleTask task, final SimpleTask.Callback callback) {

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
                                    callback.onComplete();
                            }
                        });
                }
            }
        });
        executor.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        executor.start();
    }


}
