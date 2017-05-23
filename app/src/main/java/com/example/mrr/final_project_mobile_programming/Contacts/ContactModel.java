package com.example.mrr.final_project_mobile_programming.Contacts;

public class ContactModel {

    private String name;
    private String contactId;
    private String photo;
    private String phoneNumber;
    private String email;

    public ContactModel() {

    }

    public ContactModel(String name, String contactId, String photo, String phoneNumber, String email) {

        this.name = name;
        this.contactId = contactId;
        this.photo = photo;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

