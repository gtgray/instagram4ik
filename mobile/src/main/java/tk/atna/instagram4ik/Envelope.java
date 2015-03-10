package tk.atna.instagram4ik;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Envelope {

    Meta meta;
    List<Media> data;
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

    static class Media {

        String id;
        String type;
        Images images;
        Caption caption;
        Comments comments;
        Likes likes;

        @SerializedName("created_time")
        String createdTime;

        @SerializedName("user_has_liked")
        boolean iLiked;


        static class Images {

            @SerializedName("low_resolution")
            Image low;

            @SerializedName("thumbnail")
            Image thumb;

            @SerializedName("standard_resolution")
            Image standard;


            static class Image {

                String url;
                int width;
                int height;
            }
        }


        static class Caption {

            String text;
        }


        static class Comments {

            Meta meta; // appears on comments list request
            List<Comment> data;
            int count;


            static class Comment {

                String id;
                String text;
                User from;

                @SerializedName("created_time")
                String createdTime;
            }
        }


        static class Likes {

            Meta meta; // appears on likes list request
            List<User> data;
            int count;
        }


        static class User {

            String id;
            String username;

            @SerializedName("full_name")
            String fullName;

            @SerializedName("profile_picture")
            String picture;
        }
    }

}
