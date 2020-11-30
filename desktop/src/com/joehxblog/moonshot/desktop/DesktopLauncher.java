package com.joehxblog.moonshot.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.joehxblog.moonshot.Moonshot;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 361;
		config.width = 600;
		new LwjglApplication(new Moonshot(), config);
	}
}
