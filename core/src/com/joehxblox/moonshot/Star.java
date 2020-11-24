package com.joehxblox.moonshot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Star {
    private static final Texture STAR_IMAGE = new Texture(Gdx.files.internal("star.png"));

    private final Rectangle rectangle = new Rectangle();

    public Star() {
        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        this.rectangle.width = STAR_IMAGE.getWidth();
        this.rectangle.height = STAR_IMAGE.getHeight();

        this.rectangle.x = MathUtils.random(0, w - this.rectangle.width);
        this.rectangle.y = h;
    }

    public void translateY(final float motion) {
        this.rectangle.y += motion;
    }

    public void translateX(float motion) {
        this.rectangle.x += motion;
    }

    public boolean isOffScreen() {
        return this.rectangle.y + this.rectangle.height < 0;
    }

    public void draw(final SpriteBatch sb) {
        sb.draw(this.STAR_IMAGE, this.rectangle.x, this.rectangle.y);
    }
}
