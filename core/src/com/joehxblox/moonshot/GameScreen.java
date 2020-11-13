package com.joehxblox.moonshot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameScreen extends ScreenAdapter {
    private static final float WIDTH = 800f;
    private static final float HEIGHT = 480f;

    private final Moonshot game;

    private Texture img;
    private final TiledMap tiledMap;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final TiledMapRenderer tiledMapRenderer;
    private final SpriteBatch sb;
    private final Texture texture;
    private final Sprite sprite;

    private final Rectangle floor = new Rectangle(0, 0, WIDTH, 100);
    private final Circle moon = new Circle(WIDTH / 2, 120, 20);

    private final ShapeRenderer shape = new ShapeRenderer();

    public GameScreen(final Moonshot game) {
        this.game = game;

        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        this.camera.setToOrtho(false, w, h);
        this.camera.update();
        this.tiledMap = new TmxMapLoader().load("moonshot.tmx");
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(this.tiledMap);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(final int keycode) {
                if (keycode == Input.Keys.LEFT) {
                    //GameScreen.this.camera.translate(-32, 0);
                    GameScreen.this.sprite.translateX(-1);
                }
                if (keycode == Input.Keys.RIGHT) {
                    //GameScreen.this.camera.translate(32, 0);
                    GameScreen.this.sprite.translateX(1);
                }
                if (keycode == Input.Keys.UP) {
                    GameScreen.this.camera.translate(0, -32);
                }
                if (keycode == Input.Keys.DOWN) {
                    GameScreen.this.camera.translate(0, 32);
                }

                return false;
            }

            @Override
            public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
                final Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
                final Vector3 position = GameScreen.this.camera.unproject(clickCoordinates);
                GameScreen.this.sprite.setPosition(position.x, position.y);
                return true;
            }
        });

        this.sb = new SpriteBatch();
        this.texture = new Texture(Gdx.files.internal("moon.png"));
        this.sprite = new Sprite(this.texture);
        this.sprite.translateY(64);
    }

    @Override
    public void render(final float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.camera.update();
        this.tiledMapRenderer.setView(this.camera);
        this.tiledMapRenderer.render();
        sb.setProjectionMatrix(camera.combined);
        this.sb.begin();

        this.sprite.draw(this.sb);
        this.sb.end();
        //        shape.begin(ShapeRenderer.ShapeType.Filled);
        //        shape.circle(moon.x, moon.y, moon.radius);
        //        shape.rect(floor.x, floor.y, floor.width, floor.height, Color.BROWN, Color.BROWN, Color.BROWN, Color.BROWN);
        //        shape.end();
    }

    @Override
    public void dispose() {
        this.shape.dispose();
        this.sb.dispose();
        this.tiledMap.dispose();
    }
}
