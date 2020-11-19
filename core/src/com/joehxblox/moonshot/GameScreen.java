package com.joehxblox.moonshot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
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
    private final Sprite moon;

    private final BitmapFont font = new BitmapFont();

    public GameScreen(final Moonshot game) {
        this.game = game;

        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        this.camera.setToOrtho(false, w, h);
        this.camera.update();
        this.tiledMap = new TmxMapLoader().load("moonshot.tmx");
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(this.tiledMap);

        this.sb = new SpriteBatch();
        this.moon = new Sprite(new Texture(Gdx.files.internal("moon.png")));
        this.moon.translateY(64.01f);
        this.moon.translateX(10);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
                final TiledMapTileLayer.Cell cell = getTileCellAt(screenX, screenY);
                cell.setRotation((cell.getRotation() + 1) % 4);

                return false;
            }
        });
    }

    private TiledMapTileLayer.Cell getTileCellAt(final float screenX, final float screenY) {
        final Vector3 coords = this.camera.unproject(new Vector3(screenX, screenY, 0));

        final int x = (int) Math.floor(coords.x / 32.0);
        final int y = (int) Math.floor(coords.y / 32.0);

        final TiledMapTileLayer ml = (TiledMapTileLayer) this.tiledMapRenderer.getMap().getLayers().get(0);
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
            if (cannotPanLeft || cannotPanRight && this.moon.getX() > 250) {
                if (this.moon.getX() - motion > 0) {
                    this.moon.translateX(-motion);
                }
            } else {
                this.camera.translate(-motion, 0);
            }

            this.moon.rotate(motion);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (cannotPanRight || cannotPanLeft && this.moon.getX() < 250) {
                //final TiledMapTileLayer.Cell cell = getTileToTheRight(this.moon, 1);

                if (this.moon.getX() + motion < this.tiledMapRenderer.getViewBounds().width - this.moon.getWidth()) {
                    this.moon.translateX(motion);
                }
            } else {
                this.camera.translate(motion, 0);
            }

            this.moon.rotate(-motion);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            this.moon.translateY(motion);
        } else {
            final TiledMapTileLayer.Cell cell = getTileBeneath(this.moon, motion);

            if (cell == null) {
                this.moon.setPosition(10, 64);
            } else if (!isCellFloor(cell)) {
                this.moon.translateY(-motion);
            }
        }

        this.camera.update();
        this.tiledMapRenderer.setView(this.camera);
        this.tiledMapRenderer.render();

        this.sb.begin();

        printDebug();

        this.moon.draw(this.sb);
        this.sb.end();
    }

    private TiledMapTileLayer.Cell getTileToTheRight(final Sprite sprite, final float motion) {

        float x = sprite.getX() + sprite.getWidth() + motion;
        float y = sprite.getY() + sprite.getHeight() / 2;

        //this.font.draw(sb, x + "," + y, x, y);

        return getTileCellAt(x, Gdx.graphics.getHeight() - y);
    }

    private TiledMapTileLayer.Cell getTileToTheLeft(final Sprite sprite, final float motion) {
        return getTileCellAt(sprite.getX() - motion, Gdx.graphics.getHeight() - sprite.getY() - sprite.getHeight());
    }

    private TiledMapTileLayer.Cell getTileBeneath(final Sprite sprite, final float motion) {
        return getTileCellAt(sprite.getX() + sprite.getWidth() / 2, Gdx.graphics.getHeight() - sprite.getY() + motion);
    }

    private boolean isCellFloor(final TiledMapTileLayer.Cell cell) {
        return cell != null && cell.getTile().getProperties().get("floor", false, Boolean.class);
    }

    private void printDebug() {
        final String spriteInfo = String.format("Sprite: %f, %f", this.moon.getX(), this.moon.getY());
        final String mapInfo = String.format("Map: total width: %d; w,h: %f, %f; x,y: %f, %f", getMapWidth(), this.tiledMapRenderer.getViewBounds().width, this.tiledMapRenderer.getViewBounds().height, this.tiledMapRenderer.getViewBounds().x, this.tiledMapRenderer.getViewBounds().y);
        String cellInfo = "Tile Map Props: ";

        final TiledMapTileLayer.Cell cell = getTileToTheRight(moon, 1);

        if (cell != null) {
            final MapProperties props = cell.getTile().getProperties();

            final Iterator<String> keys = props.getKeys();

            while (keys.hasNext()) {
                final String key = keys.next();
                cellInfo += key + ": " + props.get(key) + "; ";
            }
        }

        cellInfo += " -> " + isCellFloor(cell);

        this.font.draw(this.sb, String.format("%s%n%s%n%s", spriteInfo, mapInfo, cellInfo), 10, 470);
    }

    private int getMapWidth() {
        final int width = (int) this.tiledMap.getProperties().get("width");
        final int tilewidth = (int) this.tiledMap.getProperties().get("tilewidth");

        return width * tilewidth;
    }

    @Override
    public void dispose() {
        this.sb.dispose();
        this.tiledMap.dispose();
    }
}
