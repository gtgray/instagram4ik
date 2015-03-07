package tk.atna.instagram4ik;

import com.google.gson.annotations.SerializedName;

public class Envelope {

    Meta meta;
    MediaData data;
    Pagination pagination;


    static class Meta {

        int code;

        @SerializedName("error_type")
        String errorType;

        @SerializedName("error_message")
        String errorMessage;
    }

    static class Pagination {

        @SerializedName("next_url")
        String nextUrl;

        @SerializedName("next_max_id")
        String nextMaxId;
    }

    private class MediaData {
        // nothing here
    }
}
