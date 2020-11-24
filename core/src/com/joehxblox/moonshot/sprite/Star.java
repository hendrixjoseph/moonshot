package com.joehxblox.moonshot.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Star extends GameSprite {
    private static final Texture STAR_IMAGE = new Texture(Gdx.files.internal("star.png"));

    public Star() {
        super(new Sprite(STAR_IMAGE));
        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        this.getSprite().setRotation(MathUtils.random(360));
        this.getSprite().setPosition(MathUtils.random(w - this.getSprite().getWidth()), h);
    }
}
