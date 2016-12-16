package com.android.watercolor.utils;

/**
 * Created by Evgeniy on 15.12.2016.
 */

public class ImageResponseEvent {

    private String imageString;

    public ImageResponseEvent(String imageString) {
        this.imageString = imageString;
    }

    public String getImageString() {
        return imageString;
    }
}
