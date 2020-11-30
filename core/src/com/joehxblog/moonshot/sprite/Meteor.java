package com.joehxblog.moonshot.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Meteor extends GameSprite {
    public static final String FILENAME = "meteor.png";

    private static Animation<TextureRegion> METEOR_ANIMATION;

    private float stateTime = 0f;

    private static Animation<TextureRegion> getMeteorAnimation(AssetManager manager) {
        if (METEOR_ANIMATION == null || !manager.contains(FILENAME)) {
            final Texture walkSheet = manager.get(FILENAME, Texture.class);

            final TextureRegion[][] tmp = TextureRegion.split(walkSheet, 48, 96);

            METEOR_ANIMATION = new Animation<>(0.125f, tmp[0]);
        }

        return METEOR_ANIMATION;
    }

    public Meteor(AssetManager manager) {
        this(0, manager);
    }

    public Meteor(final float rotationMultiplier, AssetManager manager) {
        super(getMeteorAnimation(manager).getKeyFrame(0f, true));

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
