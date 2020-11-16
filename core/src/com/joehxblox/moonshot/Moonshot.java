package com.joehxblox.moonshot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Moonshot extends Game {
    @Override
    public void create() {

        this.setScreen(new GameScreen(this));
    }


}
