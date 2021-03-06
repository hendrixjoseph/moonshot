package com.joehxblog.moonshot.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class Moon extends GameSprite {
    public static final String FILENAME = "moon.png";

    private final Circle circle = new Circle();

    public Moon(float scale, AssetManager manager) {
        super(manager.get(FILENAME, Texture.class));

        this.circle.setRadius(getRadius());

        this.reset(scale);
    }

    @Override
    public void translateX(final float xAmount) {
        super.translateX(xAmount);
        this.circle.x += xAmount;
    }

    @Override
    public void translateY(final float yAmount) {
        super.translateY(yAmount);
        this.circle.y += yAmount;
    }

    public float getX() {
        return this.getSprite().getX();
    }

    public float getY() {
        return this.getSprite().getY();
    }

    public void rotate(final float degrees) {
        this.getSprite().rotate(degrees);
    }

    public float getWidth() {
        return this.getSprite().getWidth();
    }

    public float getHeight() {
        return this.getSprite().getHeight();
    }

    public boolean overlaps(final Rectangle rectangle) {
        return Intersector.overlaps(this.circle, rectangle);
    }

    public void reset(float scale) {
        final float radius = getRadius();

        this.getSprite().setPosition(10 * scale, 64.01f * scale);
        this.circle.setPosition(this.getSprite().getX() + radius, this.getSprite().getY() + radius);
    }

    private float getRadius() {
        return this.getSprite().getHeight() / 2;
    }
}
