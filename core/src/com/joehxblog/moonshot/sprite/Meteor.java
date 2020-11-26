package com.joehxblog.moonshot.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Meteor extends GameSprite {

    private static final Animation<TextureRegion> METEOR_ANIMATION = create();

    private float stateTime = 0f;

    private static Animation<TextureRegion> create() {
        final Texture walkSheet = new Texture(Gdx.files.internal("meteor.png"));

        final TextureRegion[][] tmp = TextureRegion.split(walkSheet, 16, 32);

        return new Animation<>(0.125f, tmp[0]);
    }

    public Meteor(final float rotationMultiplier) {
        super(new Sprite(METEOR_ANIMATION.getKeyFrame(0f, true)));

        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        final float rotation = rotationMultiplier * 45f;

        this.getSprite().setRotation(MathUtils.random(-rotation, rotation));
        this.getSprite().setPosition(MathUtils.random(w - this.getSprite().getWidth()), h);
    }

    @Override
    public void translateY(final float motion) {
        /*
                        |\
                        | \   motion
       cos() = y/motion |  \
                        |___\
                          sin() = x/motion
         */

        final float rotation = this.getSprite().getRotation();
        super.translateY(MathUtils.cosDeg(rotation) * motion);
        super.translateX(-MathUtils.sinDeg(rotation) * motion);
    }

    @Override
    public void draw(final Batch batch) {
        this.stateTime += Gdx.graphics.getDeltaTime();

        final TextureRegion currentFrame = METEOR_ANIMATION.getKeyFrame(this.stateTime, true);

        this.getSprite().setRegion(currentFrame);

        super.draw(batch);
    }
}
