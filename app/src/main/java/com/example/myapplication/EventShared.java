package com.example.myapplication;

public class EventShared {
    private String eventName;
    private String eventVenue;
    private String eventDate;
    private String eventTime;
    private String eventGenre;
    private String eventId;
    private String eventImage;

    public EventShared(String eventName, String eventVenue,  String eventDate,  String eventTime,  String eventGenre, String eventId, String eventImage) {
        this.eventName = eventName;
        this.eventVenue = eventVenue;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventGenre = eventGenre;
        this.eventId = eventId;
        this.eventImage = eventImage;
    }

    public String getName() {
        return eventName;
    }

    public String getVenue() {
        return eventVenue;
    }
    public String getDate() {
        return eventDate;
    }
    public String getTime() {
        return eventTime;
    }
    public String getGenre() {
        return eventGenre;
    }

    public String getId() {
        return eventId;
    }
    public String getImage() {
        return eventImage;
    }
}
