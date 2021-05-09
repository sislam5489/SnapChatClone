package edu.fordham.snapchatclone;

public class UserObject {
    String email,uid;
    public UserObject(){

    }
    public UserObject(String email, String uid){
        this.email = email;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUid(){return uid;}

    public void setUid(String u){
        this.uid = u;
    }

    public void setEmail(String e){
        this.email = e;
    }


}
