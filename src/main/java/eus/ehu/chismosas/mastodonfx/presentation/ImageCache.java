package eus.ehu.chismosas.mastodonfx.presentation;

import javafx.scene.image.Image;

import java.util.HashMap;

public class ImageCache {
    private static final HashMap<String, Image> cache = new HashMap<>();


    private ImageCache() {}

    public static Image get(String url) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        } else {
            Image image = new Image(url, true); // true = load in background
            cache.put(url, image);
            return image;
        }
    }

}
