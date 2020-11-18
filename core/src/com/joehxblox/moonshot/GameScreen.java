package com.joehxblox.moonshot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;

public class GameScreen extends ScreenAdapter {
    private static final float WIDTH = 800f;
    private static final float HEIGHT = 480f;

    private final Moonshot game;

    private final TiledMap tiledMap;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final SpriteBatch sb;
    private final Texture texture;
    private final Sprite sprite;

    private final BitmapFont font = new BitmapFont();

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

        this.sb = new SpriteBatch();
        this.texture = new Texture(Gdx.files.internal("moon.png"));
        this.sprite = new Sprite(this.texture);
        this.sprite.translateY(64);
        this.sprite.translateX(10);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
                TiledMapTileLayer.Cell cell = getTileCellAt(screenX, screenY);
                cell.setRotation((cell.getRotation() + 1) % 4);

                return false;
            }
        });
    }

    private TiledMapTileLayer.Cell getTileCellAt(float screenX, float screenY) {
        final Vector3 coords = this.camera.unproject(new Vector3(screenX, screenY, 0));

        int x = (int) Math.floor(coords.x / 32.0);
        int y = (int) Math.floor(coords.y / 32.0);

        TiledMapTileLayer ml = (TiledMapTileLayer) this.tiledMapRenderer.getMap().getLayers().get(0);
        return ml.getCell(x, y);
    }

    @Override
    public void render(final float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        final float motion = 200 * delta;

        final boolean cannotPanRight = this.tiledMapRenderer.getViewBounds().x + motion > getMapWidth() - this.tiledMapRenderer.getViewBounds().width;
        final boolean cannotPanLeft = this.tiledMapRenderer.getViewBounds().x - motion < 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (cannotPanLeft || cannotPanRight && this.sprite.getX() > 250) {
                if (this.sprite.getX() - motion > 0) {
                    this.sprite.translateX(-motion);
                }
            } else {
                this.camera.translate(-motion, 0);
            }

            this.sprite.rotate(motion);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (cannotPanRight || cannotPanLeft && this.sprite.getX() < 250) {
                if (this.sprite.getX() + motion < this.tiledMapRenderer.getViewBounds().width - this.sprite.getWidth()) {
                    this.sprite.translateX(motion);
                }
            } else {
                this.camera.translate(motion, 0);
            }

            this.sprite.rotate(-motion);
        }

        TiledMapTileLayer.Cell cell = getTileCellAt(this.sprite.getX(), Gdx.graphics.getHeight() - this.sprite.getY() + 1);

        if (!cell.getTile().getProperties().get("floor", false, Boolean.class)) {
            this.sprite.translateY(-1);
        }

        this.camera.update();
        this.tiledMapRenderer.setView(this.camera);
        this.tiledMapRenderer.render();
        //this.sb.setProjectionMatrix(this.camera.combined);

        this.sb.begin();

        printDebug();

        this.sprite.draw(this.sb);
        this.sb.end();
        //        shape.begin(ShapeRenderer.ShapeType.Filled);
        //        shape.circle(moon.x, moon.y, moon.radius);
        //        shape.rect(floor.x, floor.y, floor.width, floor.height, Color.BROWN, Color.BROWN, Color.BROWN, Color.BROWN);
        //        shape.end();
    }

    private void printDebug() {
        final String spriteInfo = String.format("Sprite: %f, %f", this.sprite.getX(), this.sprite.getY());
        final String mapInfo = String.format("Map: total width: %d; w,h: %f, %f; x,y: %f, %f", getMapWidth(), this.tiledMapRenderer.getViewBounds().width, this.tiledMapRenderer.getViewBounds().height, this.tiledMapRenderer.getViewBounds().x, this.tiledMapRenderer.getViewBounds().y);

        TiledMapTileLayer.Cell cell = getTileCellAt(this.sprite.getX(), Gdx.graphics.getHeight() - this.sprite.getY() + 1);
        MapProperties props = cell.getTile().getProperties();

        String cellInfo = "Tile Map Props: ";

        Iterator<String> keys = props.getKeys();

        while (keys.hasNext()) {
            String key = keys.next();
            cellInfo += key + ": " + props.get(key) + "; ";
        }

        this.font.draw(this.sb, String.format("%s%n%s%n%s", spriteInfo, mapInfo, cellInfo), 10, 470);
    }

    private int getMapWidth() {
        final int width = (int) this.tiledMap.getProperties().get("width");
        final int tilewidth = (int) this.tiledMap.getProperties().get("tilewidth");

        return width * tilewidth;
    }

    @Override
    public void dispose() {
        this.shape.dispose();
        this.sb.dispose();
        this.tiledMap.dispose();
    }
}
