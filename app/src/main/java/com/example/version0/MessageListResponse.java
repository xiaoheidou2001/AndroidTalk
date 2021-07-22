package com.example.version0;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageListResponse {
    @SerializedName("feeds")
    public List<Message> feeds;
    @SerializedName("success")
    public boolean success;
}