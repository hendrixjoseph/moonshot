package com.joehxblox.moonshot.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Meteor extends GameSprite {
    private static final Texture METEOR_IMAGE = new Texture(Gdx.files.internal("meteor1.png"));

    public Meteor(float rotationMultiplier) {
        super(new Sprite(METEOR_IMAGE));

        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        float rotation = rotationMultiplier * 45f;

        this.getSprite().setRotation(MathUtils.random(-rotation,rotation));
        this.getSprite().setPosition(MathUtils.random(w - this.getSprite().getWidth()), h);



    }

    public Meteor() {
        this(0);
    }

    @Override
    public void translateY(float motion) {
        /*
                        |\
                        | \   motion
       cos() = y/motion |  \
                        |___\
                          sin() = x/motion
         */

        float rotation = this.getSprite().getRotation();
        super.translateY(MathUtils.cosDeg(rotation) * motion);
        super.translateX(-MathUtils.sinDeg(rotation) * motion);
    }
}
