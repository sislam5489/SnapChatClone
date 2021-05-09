package edu.fordham.snapchatclone;

public class User_Class {
    String email,name,profileurl,uid;
    public User_Class(){

    }
    public User_Class(String e,String n){
        this.email = e;
        this.name = n;
        this.profileurl = "default";
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfileurl(){
        return profileurl;
    }

    public void setEmail(String e){
        this.email = e;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }
}
