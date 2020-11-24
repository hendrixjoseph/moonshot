package com.joehxblox.moonshot.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Meteor extends GameSprite {
    private static final Texture METEOR_IMAGE = new Texture(Gdx.files.internal("meteor1.png"));

    public Meteor() {
        super(new Sprite(METEOR_IMAGE));

        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        this.getSprite().setPosition(MathUtils.random(w - this.getSprite().getWidth()), h);
    }
}
