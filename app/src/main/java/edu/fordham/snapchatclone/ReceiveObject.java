package edu.fordham.snapchatclone;

public class ReceiveObject {
    String email,uid;
    private boolean receive;

    public ReceiveObject(){

    }
    public ReceiveObject(String email, String uid,Boolean receive){
        this.email = email;
        this.uid = uid;
        this.receive = receive;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getReceive() {
        return receive;
    }
    public String getUid(){return uid;}

    public void setUid(String u){
        this.uid = u;
    }

    public void setEmail(String e){
        this.email = e;
    }
    public void setReceive(Boolean b){this.receive = b;}

}
