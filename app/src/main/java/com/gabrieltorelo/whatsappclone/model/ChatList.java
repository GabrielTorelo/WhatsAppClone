package com.gabrieltorelo.whatsappclone.model;

public class ChatList {
    private String userID;
    private String userName;
    private String userBio;
    private String userBioDate;
    private String descriptionMessage;
    private String dateMessage;
    private String urlProfile;
    private String userPhone;

    public ChatList(){

    }

    public ChatList(String userID, String userName, String userBio,
                    String userBioDate, String descriptionMessage,
                    String dateMessage, String urlProfile, String userPhone) {
        this.userID = userID;
        this.userName = userName;
        this.userBio = userBio;
        this.userBioDate = userBioDate;
        this.descriptionMessage = descriptionMessage;
        this.dateMessage = dateMessage;
        this.urlProfile = urlProfile;
        this.userPhone = userPhone;
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

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserBioDate() {
        return userBioDate;
    }

    public void setUserBioDate(String userBioDate) {
        this.userBioDate = userBioDate;
    }

    public String getDescriptionMessage() {
        return descriptionMessage;
    }

    public void setDescriptionMessage(String descriptionMessage) {
        this.descriptionMessage = descriptionMessage;
    }

    public String getDateMessage() {
        return dateMessage;
    }

    public void setDateMessage(String dateMessage) {
        this.dateMessage = dateMessage;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
