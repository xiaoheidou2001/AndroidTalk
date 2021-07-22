package com.example.version0;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

//public class Message implements Serializable {
//
//    @SerializedName("_id")
//    private String Id;
//    @SerializedName("student_id")
//    private String studentId;
//    @SerializedName("from")
//    private String from;
//    @SerializedName("to")
//    private String to;
//    @SerializedName("content")
//    private String content;
//    @SerializedName("image_url")
//    private String imageUrl;
//
//    public Message(String id, String studentId, String from, String to, String content, String imageUrl, int imageW, int imageH, Date createdAt, Date updatedAt) {
//        Id = id;
//        this.studentId = studentId;
//        this.from = from;
//        this.to = to;
//        this.content = content;
//        this.imageUrl = imageUrl;
//        this.imageW = imageW;
//        this.imageH = imageH;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }
//
//    @SerializedName("image_w")
//    private int imageW;
//    @SerializedName("image_h")
//    private int imageH;
//    @SerializedName("createdAt")
//    private Date createdAt;
//    @SerializedName("updatedAt")
//    private Date updatedAt;
//
//
//    public void setId(String Id) {
//        this.Id = Id;
//    }
//    public String getId() {
//        return Id;
//    }
//
//    public void setStudentId(String studentId) {
//        this.studentId = studentId;
//    }
//    public String getStudentId() {
//        return studentId;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }
//    public String getFrom() {
//        return from;
//    }
//
//    public void setTo(String to) {
//        this.to = to;
//    }
//    public String getTo() {
//        return to;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//    public String getContent() {
//        return content;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageW(int imageW) {
//        this.imageW = imageW;
//    }
//    public int getImageW() {
//        return imageW;
//    }
//
//    public void setImageH(int imageH) {
//        this.imageH = imageH;
//    }
//    public int getImageH() {
//        return imageH;
//    }
//
//    public void setCreatedAt(Date createdat) {
//        this.createdAt = createdat;
//    }
//    public Date getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setUpdatedAt(Date updatedat) {
//        this.updatedAt = updatedat;
//    }
//    public Date getUpdatedAt() {
//        return updatedAt;
//    }
//
//}

public class Message implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("student_id")
    private String student_id;
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("video_url")
    private String video_url;
    @SerializedName("image_url")
    private String image_url;
    @SerializedName("image_w")
    private int image_w;
    @SerializedName("image_h")
    private int image_h;
    @SerializedName("createdAt")
    private Date created_at;
    @SerializedName("updatedAt")
    private Date updated_at;

    public Message(String id, String student_id, String user_name, String video_url, String image_url, int image_w, int image_h, Date created_at, Date updated_at) {
        this.id = id;
        this.student_id = student_id;
        this.user_name = user_name;
        this.video_url = video_url;
        this.image_url = image_url;
        this.image_w = image_w;
        this.image_h = image_h;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }
    public String getStudent_id() {
        return student_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getUser_name() {
        return user_name;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
    public String getVideo_url() {
        return video_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public String getImage_url() {
        return image_url;
    }

    public void setImage_w(int image_w) {
        this.image_w = image_w;
    }
    public int getImage_w() {
        return image_w;
    }

    public void setImage_h(int image_h) {
        this.image_h = image_h;
    }
    public int getImage_h() {
        return image_h;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
    public Date getCreated_at() {
        return created_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
    public Date getUpdated_at() {
        return updated_at;
    }

}
