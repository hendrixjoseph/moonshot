package com.joehxblog.moonshot;

import com.badlogic.gdx.Game;

public class Moonshot extends Game {
    @Override
    public void create() {
        this.setScreen(new GameScreen(this));
    }
}
