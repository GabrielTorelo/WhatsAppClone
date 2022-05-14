package com.gabrieltorelo.whatsappclone.model.chat;

public class Chat {
    private String id;
    private String date;
    private String time;
    private String textMessage;
    private String url;
    private String type;
    private String rotation;
    private String visualize;
    private String favorite;
    private String duration;
    private String sender;
    private String receiver;
    private String receiverRemoved;

    public Chat() {

    }

    public Chat(String id, String date, String time, String textMessage,
                String url, String type, String rotation, String visualize,
                String favorite, String duration, String sender,
                String receiver, String receiverRemoved) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.textMessage = textMessage;
        this.url = url;
        this.type = type;
        this.rotation = rotation;
        this.visualize = visualize;
        this.favorite = favorite;
        this.duration = duration;
        this.sender = sender;
        this.receiver = receiver;
        this.receiverRemoved = receiverRemoved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRotation() {
        return rotation;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getReceiverRemoved() {
        return receiverRemoved;
    }

    public void setReceiverRemoved(String receiverRemoved) {
        this.receiverRemoved = receiverRemoved;
    }

    public String getVisualize() {
        return visualize;
    }

    public void setVisualize(String visualize) {
        this.visualize = visualize;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String dateTime) {
        this.date = dateTime;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

}
