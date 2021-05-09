package edu.fordham.snapchatclone;

import java.util.Objects;

public class StoryObject{
    String email,uid,chatOrStory;
    public StoryObject(){

    }
    public StoryObject(String email, String uid, String chatOrStory){
        this.email = email;
        this.uid = uid;
        this.chatOrStory = chatOrStory;
    }

    public String getEmail() {
        return email;
    }

    public String getUid(){return uid;}

    public void setChatOrStory(String u){
        this.uid = u;
    }

    public String getChatOrStory(){return uid;}

    public void setUid(String u){
        this.uid = u;
    }


    public void setEmail(String e){
        this.email = e;
    }

    //b/c java assumes two objects are diff even if have same uid and email
    @Override
    public boolean equals(Object o) {
        boolean same = false;
        if(o != null && o instanceof StoryObject){
            same = this.uid == ((StoryObject) o).uid;
        }
        return same;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.uid == null ? 0: this.uid.hashCode());
        return super.hashCode();
    }
}
