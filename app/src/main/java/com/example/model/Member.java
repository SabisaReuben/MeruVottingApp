package com.example.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.mustapp.BR;

public class Member extends BaseObservable {
    private String firstName;
    private String secondName;
    private String phoneNumber;
    private String email;
    private String gender;
    private String password;
    private  String confirmPassword;
    public Member() {
    }

    @Bindable
    public String getFirstName() {
        return firstName;
    }

    @Bindable
    public String getSecondName() {
        return secondName;
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    @Bindable
    public String getGender() {
        return gender;
    }
@Bindable
    public String getPassword() {
        return password;
    }
@Bindable
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setFirstName(String firstName) {
        if (!this.firstName.equals(firstName)) {
            this.firstName = firstName;
            notifyPropertyChanged(BR.firstName);
        }
    }

    public void setSecondName(String secondName) {
        if (!this.secondName.equals(secondName)) {
            this.secondName = secondName;
            notifyPropertyChanged(BR.secondName);
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        if (!this.phoneNumber.equals(phoneNumber)) {
            this.phoneNumber = phoneNumber;
            notifyPropertyChanged(BR.phoneNumber);
        }
    }

    public void setEmail(String email) {
        if (!this.email.equals(email)) {
            this.email = email;
            notifyPropertyChanged(BR.email);
        }
    }

    public void setGender(String gender) {
        if (!this.gender.equals(gender)) {
            this.gender = gender;
            notifyPropertyChanged(BR.gender);
        }
    }

    public void setPassword(String password) {
        if (!this.password.equals(password)) {
            this.password = password;
            notifyPropertyChanged(BR.password);
        }
    }

    public void setConfirmPassword(String confirmPassword) {
        if (!this.confirmPassword.equals(confirmPassword)) {
            this.confirmPassword = confirmPassword;
            notifyPropertyChanged(BR.confirmPassword);
        }
    }
}
