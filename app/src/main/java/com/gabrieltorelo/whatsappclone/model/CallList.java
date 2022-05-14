package com.gabrieltorelo.whatsappclone.model;

public class CallList {
    private String userID;
    private String userName;
    private String dateMessage;
    private String urlProfile;
    private String callType;

    public CallList() {

    }

    public CallList(String userID, String userName, String dateMessage, String urlProfile, String callType) {
        this.userID = userID;
        this.userName = userName;
        this.dateMessage = dateMessage;
        this.urlProfile = urlProfile;
        this.callType = callType;
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

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
