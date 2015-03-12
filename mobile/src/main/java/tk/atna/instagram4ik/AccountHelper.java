package tk.atna.instagram4ik;


import android.net.Uri;

import java.util.Set;

public class AccountHelper {

    public static final String NETWORK_DOWN = "Network is unavailable at the moment";

    /**
     * Returns auth url
     *
     * @param clientId client id to use
     */
    public static String getAuthUrl(String clientId) {
        return ServerApi.FULL_AUTH_URL + clientId;
    }

    /**
     * Returns about:blank
     */
    public static String getBlank() {
        return ServerApi.BLANK_URL;
    }

    /**
     * Returns log out url
     */
    public static String getLogoutUrl() {
        return ServerApi.FULL_LOGOUT_URL;
    }

    /**
     * Returns needed redirect uri when caught
     *
     * @param url url to check
     * @return needed uri or null
     */
    public static Uri hitTargetRedirect(String url) {
        Uri uri = Uri.parse(url);
        // if needed host
        if((ServerApi.DEFAULT_REDIRECT_URI.equals(uri.getHost())))
            return uri;

        return null;
    }

    /**
     * Parses incoming uri
     *
     * @param uri uri to parse
     * @return access token or null
     */
    public static String parseToken(Uri uri) {
        String raw = uri.getFragment();
        if (raw != null) {
            int index = (ServerApi.ACCESS_TOKEN + "=").length();
            if (index > 0)
                return raw.substring(index);
        }

        return null;
    }

    /**
     * Parses incoming uri and fires on user denied
     *
     * @param uri uri to check
     * @return true if user denied permissions
     */
    public static Boolean isUserDenied(Uri uri) {
        Set<String> names = uri.getQueryParameterNames();
        for(String name : names) {
            if(ServerApi.ERROR_REASON.equals(name)) {
                return ServerApi.USER_DENIED.equals(uri.getQueryParameter(name));
            }
        }
        return null;
    }

}

