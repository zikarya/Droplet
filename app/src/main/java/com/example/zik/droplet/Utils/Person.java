package com.example.zik.droplet.Utils;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

@Keep public class Person implements Parcelable {
    Bitmap profilePic;
    String imageurl;
    String name;
    String email;
    String password;
    String location;
    String bio;

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }
    public void setimageurl(String imageurl){
        this.imageurl = imageurl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public  void setEmail(String email){this.email = email;}

    public void setPassword(String password){this.password = password;}

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Bitmap getProfilePic() {
        return this.profilePic;
    }

    public String getimageurl(){
        return this.imageurl;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail(){ return this.email;}

    public String getPassword(){ return this.password;}

    public String getLocation() {
        return this.location;
    }

    public String getBio() {
        return this.bio;
    }
    // IMPLEMENT PARCELABLE METHODS
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
  //      dest.writeBitmap(profilePic);
        dest.writeString(imageurl);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(location);
        dest.writeString(bio);

    }
    public Person(){}
    public Person(Parcel in) {
//        this.profilePic = in.readBitMap();
        this.imageurl = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.location= in.readString();
        this.bio= in.readString();
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

}
