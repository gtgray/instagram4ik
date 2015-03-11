package tk.atna.instagram4ik;

public class DetailedMedia {

    private String mediaId;
    private String imageUrl;
    private String createdTime;
    private int commentsCount;
    private int likesCount;
    private String caption;
    private boolean iLiked;


    public DetailedMedia(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public DetailedMedia setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public DetailedMedia setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public DetailedMedia setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
        return this;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public DetailedMedia setLikesCount(int likesCount) {
        this.likesCount = likesCount;
        return this;
    }

    public String getCaption() {
        return caption;
    }

    public DetailedMedia setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public boolean isiLiked() {
        return iLiked;
    }

    public DetailedMedia setiLiked(boolean iLiked) {
        this.iLiked = iLiked;
        return this;
    }
}
