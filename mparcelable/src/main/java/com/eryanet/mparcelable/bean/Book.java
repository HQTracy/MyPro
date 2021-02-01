package com.eryanet.mparcelable.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    private int id;
    private String name;
    private String classify;

    public Book(int id, String name, String classify) {
        this.id = id;
        this.name = name;
        this.classify = classify;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.classify);
    }

    protected Book(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.classify = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
