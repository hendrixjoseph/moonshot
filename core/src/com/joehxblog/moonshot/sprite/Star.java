package com.joehxblog.moonshot.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Star extends GameSprite {
    public static final String FILENAME = "star.png";
    private static final Texture STAR_IMAGE = new Texture(Gdx.files.internal("star.png"));

    private final static Color[] COLORS = {Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED};
    public static final int NUMBER_OF_COLORS = COLORS.length;

    private final int color;

    public Star(AssetManager manager) {
        this(MathUtils.floor(MathUtils.randomTriangular(0, COLORS.length, 0)),
                MathUtils.random(360),
                MathUtils.random(Gdx.graphics.getWidth()),
                Gdx.graphics.getHeight(),
                manager);
    }

    public Star(int color, float rotation, float x, float y, AssetManager manager) {
        super(manager.get(FILENAME, Texture.class));
        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        this.color = color;

        this.getSprite().setColor(COLORS[this.color]);
        this.getSprite().setRotation(rotation);
        this.getSprite().setPosition(x, y);
    }

    public int getPoints() {
        return this.color * 2 + 1;
    }
}
