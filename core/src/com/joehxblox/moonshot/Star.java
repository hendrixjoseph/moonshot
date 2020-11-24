package com.joehxblox.moonshot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Star {
    private static final Texture STAR_IMAGE = new Texture(Gdx.files.internal("star.png"));

    private final Sprite sprite = new Sprite(STAR_IMAGE);

    public Star() {
        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        this.sprite.setRotation(MathUtils.random(360));
        this.sprite.setPosition(MathUtils.random(w - this.sprite.getWidth()), h);
    }

    public void translateY(final float motion) {
        this.sprite.translateY(motion);
    }

    public void translateX(float motion) {
        this.sprite.translateX(motion);
    }

    public boolean isOffScreen() {
        return this.sprite.getY() + this.sprite.getHeight() < 0;
    }

    public void draw(final SpriteBatch sb) {
        this.sprite.draw(sb);
    }

    public Rectangle getRectangle() {
        return this.sprite.getBoundingRectangle();
    }
}
