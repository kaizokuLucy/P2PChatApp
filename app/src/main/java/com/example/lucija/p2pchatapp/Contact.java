package com.example.lucija.p2pchatapp;


public class Contact {

    private int _id;
    private String _contactName;
    private String _contactNumber;
    private String _myKey;
    private String _contactKey;

    public Contact() {
    }

    public Contact(String _id, String _contactName, String contactNumber, String myKey, String contactKey) {
        this._id = Integer.valueOf(_id);
        this._contactName = _contactName;
        this._contactNumber = contactNumber;
        this._myKey = myKey;
        this._contactKey = contactKey;
    }

    public Contact(String _contactName, String contactNumber, String myKey, String contactKey){
        this._contactName = _contactName;
        this._contactNumber = contactNumber;
        this._myKey = myKey;
        this._contactKey = contactKey;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_contactName() {
        return _contactName;
    }

    public void set_contactName(String _contactName) {
        this._contactName = _contactName;
    }

    public String getContactNumber() {
        return _contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this._contactNumber = contactNumber;
    }

    public String getMyKey() {
        return _myKey;
    }

    public void setMyKey(String myKey) {
        this._myKey = myKey;
    }

    public String getContactKey() {
        return _contactKey;
    }

    public void setContactKey(String contactKey) {
        this._contactKey = contactKey;
    }
}
