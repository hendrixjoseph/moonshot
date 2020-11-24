package com.joehxblox.moonshot.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class Moon extends GameSprite {
    private final Circle circle;

    public Moon() {
        super(new Sprite(new Texture(Gdx.files.internal("moon.png"))));

        this.getSprite().translateY(64.01f);
        this.getSprite().translateX(10);

        final float radius = this.getSprite().getHeight() / 2;

        this.circle = new Circle(this.getSprite().getX() + radius, this.getSprite().getY() + radius, radius);

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
}
