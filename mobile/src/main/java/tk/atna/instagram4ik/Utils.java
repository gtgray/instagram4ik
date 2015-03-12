package tk.atna.instagram4ik;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class that contains various instrumental methods
 */
public class Utils {


    public static final String FULL_TIMESTAMP_FORMAT = "yyyy-MM-dd kk:mm:ss";
    public static final String SIMPLE_TIMESTAMP_FORMAT = "yyyy-MM-dd";

    /**
     * First tries to find fragment by tag in fragment manager.
     * Otherwise, creates it or initializes (with data or not, optional).
     * Adds to backstack (optional). Loads into container with tag.
     *
     * @param fm link to fragment manager
     * @param container resource to park fragment to
     * @param clazz type of fragment to park
     * @param initData data to init fragment with
     * @param backstacked if fragment is needed to be added to backstack
     * @param <T> child of Fragment class
     */
    public static <T extends Fragment> T parkFragment(FragmentManager fm, int container,
                                                         Class<T> clazz, Bundle initData,
                                                         boolean backstacked) {
        Fragment fragment = findFragment(fm, clazz);
        if (fragment == null) {
            fragment = initFragment(clazz, initData);

            String tag = findTag(clazz);

            FragmentTransaction ft = fm.beginTransaction()
                                       .replace(container, fragment, tag);
            if(backstacked)
                ft.addToBackStack(tag);

            ft.commit();
        }
        return clazz.cast(fragment);
    }

    /**
     * Removes fragment of type clazz from fragment manager
     *
     * @param fm link to fragment manager
     * @param clazz type of fragment to unpark
     * @param <T> child of Fragment class
     */
    public static <T extends Fragment> void unparkFragment(FragmentManager fm, Class<T> clazz) {
        Fragment fragment = findFragment(fm, clazz);
        if(fragment == null)
            return;

        fm.beginTransaction()
          .remove(fragment)
          .commit();
    }

    /**
     * Tries to find fragment of type clazz in fragment manager
     *
     * @param fm link to fragment manager
     * @param clazz type of fragment to
     * @param <T> child of Fragment class
     * @return found fragment or null
     */
    public static <T extends Fragment> T findFragment(FragmentManager fm, Class<T> clazz) {
        return clazz.cast(fm.findFragmentByTag(findTag(clazz)));
    }

    /**
     *  Searches for static field TAG in clazz type
     *
     * @param clazz type of fragment to find tag for
     * @param <T> child of Fragment class
     * @return value of TAG field or null
     */
    private static <T extends Fragment> String findTag(Class<T> clazz) {

        final String TAG = "TAG";

        try {
            Field tag = clazz.getDeclaredField(TAG);
            return (String) tag.get(null);

        } catch (IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            Log.d("myLogs", Utils.class.getSimpleName() + "findTag: " + clazz.getSimpleName()
                                    + " class must have static final field 'TAG'");
        }
        return null;
    }

    /**
     * Tries to initialize fragment of type clazz by one of methods:
     * init(Bundle), init().
     *
     * @param clazz type of fragment to initialize
     * @param data data to initialize fragment with
     * @param <T> child of Fragment class
     * @return initialized fragment or null
     */
    private static <T extends Fragment> T initFragment(Class<T> clazz, Bundle data) {

        final String INIT = "newInstance";

        try {
            Method init;
            Object fragment;

            if (data != null) {
                init = clazz.getMethod(INIT, Bundle.class);
                fragment = init.invoke(null, data);

            } else {
                init = clazz.getMethod(INIT);
                fragment = init.invoke(null);
            }
            return clazz.cast(fragment);

        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts timestamp in seconds into human readable representation
     *
     * @param secs timestamp in seconds
     * @return timestamp in FULL_TIMESTAMP_FORMAT format
     */
    public static String formatTime(String secs) {
        return Utils.millisToLocalDate(Long.valueOf(secs) * 1000, Utils.FULL_TIMESTAMP_FORMAT);
    }

    /**
     * Converts timestamp in millis into human readable representation
     *
     * @param millis timestamp in millis
     * @param format format to convert into
     * @return timestamp in format
     */
    public static String millisToLocalDate(long millis, String format) {
        return (DateFormat.format(format, millis)).toString();
    }

}
