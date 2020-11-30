package com.joehxblog.moonshot.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class GameSprite {
    private final Sprite sprite;

    public GameSprite(final Texture texture) {
        this.sprite = new Sprite(texture);
    }

    public GameSprite(final TextureRegion texture) {
        this.sprite = new Sprite(texture);
    }

    public void translateY(final float motion) {
        this.sprite.translateY(motion);
    }

    public void translateX(final float motion) {
        this.sprite.translateX(motion);
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void draw(final Batch batch) {
        this.sprite.draw(batch);
    }

    public boolean isOffScreen() {
        return this.sprite.getY() + this.sprite.getHeight() < 0;
    }

    public Rectangle getRectangle() {
        return this.sprite.getBoundingRectangle();
    }
}
