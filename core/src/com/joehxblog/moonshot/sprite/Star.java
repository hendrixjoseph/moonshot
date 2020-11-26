package com.joehxblog.moonshot.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Star extends GameSprite {
    private static final Texture STAR_IMAGE = new Texture(Gdx.files.internal("star.png"));

    private final static Color[] COLORS = {Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED};

    private final int color;

    public Star() {
        super(new Sprite(STAR_IMAGE));
        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();


        this.color = MathUtils.floor(MathUtils.randomTriangular(0, COLORS.length, 0));

        this.getSprite().setColor(COLORS[this.color]);
        this.getSprite().setRotation(MathUtils.random(360));
        this.getSprite().setPosition(MathUtils.random(w - this.getSprite().getWidth()), h);
    }

    public int getPoints() {
        return this.color * 2 + 1;
    }
}
