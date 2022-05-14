package com.gabrieltorelo.whatsappclone.model.user;

public class Users {
    private String userID;
    private String userName;
    private String userPhone;
    private String imageProfile;
    private String imageCover;
    private String bio;
    private String bioDate;

    public Users() {
    }

    public Users(String userID, String userName, String userPhone, String imageProfile,
                 String imageCover, String bio, String bioDate) {
        this.userID = userID;
        this.userName = userName;
        this.userPhone = userPhone;
        this.imageProfile = imageProfile;
        this.imageCover = imageCover;
        this.bio = bio;
        this.bioDate = bioDate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getImageCover() {
        return imageCover;
    }

    public void setImageCover(String imageCover) {
        this.imageCover = imageCover;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBioDate() {
        return bioDate;
    }

    public void setBioDate(String bioDate) {
        this.bioDate = bioDate;
    }
}
