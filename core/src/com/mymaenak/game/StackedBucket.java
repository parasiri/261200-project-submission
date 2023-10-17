package com.mymaenak.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class StackedBucket {
    public Rectangle rectangle;
    public Texture texture;

    public StackedBucket(Rectangle rectangle, Texture texture) {
        this.rectangle = rectangle;
        this.texture = texture;
    }
}
