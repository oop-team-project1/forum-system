package com.company.web.forum.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user_profile_pic")
public class UserProfilePic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pic_id")
    private int picId;

    @Column(name = "pic")
    private String pic;

    public UserProfilePic(){
    };

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
