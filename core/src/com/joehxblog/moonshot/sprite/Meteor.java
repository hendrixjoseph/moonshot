package com.joehxblog.moonshot.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Meteor extends GameSprite {

    private static final Animation<TextureRegion> walkAnimation = create();

    private float stateTime = 0f;

    private static Animation<TextureRegion> create() {
        Texture walkSheet = new Texture(Gdx.files.internal("meteor.png"));

        TextureRegion[][] tmp = TextureRegion.split(walkSheet,16, 32);

        return new Animation<>(0.025f, tmp[0]);
    }

    public Meteor(float rotationMultiplier) {
        super(new Sprite(walkAnimation.getKeyFrame(0f, true)));

        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        float rotation = rotationMultiplier * 45f;

        this.getSprite().setRotation(MathUtils.random(-rotation,rotation));
        this.getSprite().setPosition(MathUtils.random(w - this.getSprite().getWidth()), h);
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

    @Override
    public void draw(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

        // Get current frame of animation for the current stateTime
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        this.getSprite().setRegion(currentFrame);

        super.draw(batch);
    }
}
