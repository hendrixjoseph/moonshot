package com.joehxblox.moonshot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Moonshot extends Game {
    private SpriteBatch batch;
    private BitmapFont font;

    public BitmapFont getFont() {
        return this.font;
    }

    public SpriteBatch getBatch() {
        return this.batch;
    }

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();

        this.setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        this.font.dispose();
        this.batch.dispose();
    }
}
