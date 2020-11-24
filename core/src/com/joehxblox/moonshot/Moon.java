package com.joehxblox.moonshot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class Moon {
    private final Sprite sprite;
    private final Circle circle;

    public Moon() {
        this.sprite = new Sprite(new Texture(Gdx.files.internal("moon.png")));
        this.sprite.translateY(64.01f);
        this.sprite.translateX(10);

        float radius = this.sprite.getHeight() / 2;

        this.circle = new Circle(this.sprite.getX() + radius, this.sprite.getY() + radius, radius);

    }

    public void translateX(float xAmount) {
        sprite.translateX(xAmount);
        this.circle.x += xAmount;
    }

    public void translateY(float yAmount) {
        sprite.translateY(yAmount);
        this.circle.y += yAmount;
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public void rotate(float degrees) {
        sprite.rotate(degrees);
    }

    public float getWidth() {
        return sprite.getWidth();
    }

    public float getHeight() {
        return sprite.getHeight();
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean overlaps(Rectangle rectangle) {
        return Intersector.overlaps(circle, rectangle);
    }
}
