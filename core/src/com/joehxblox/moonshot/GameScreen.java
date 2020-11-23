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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;

import static com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class GameScreen extends ScreenAdapter {
    private final Moonshot game;

    private final OrthographicCamera camera = new OrthographicCamera();
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final SpriteBatch sb;
    private final Sprite moon;

    private final Rectangle leftButton;
    private final Rectangle centerButton;
    private final Rectangle rightButton;

    private final BitmapFont font = new BitmapFont();

    private final float scale;

    public GameScreen(final Moonshot game) {
        this.game = game;

        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        this.camera.setToOrtho(false, w, h);
        this.camera.update();
        final TiledMap tiledMap = new TmxMapLoader().load("moonshot.tmx");

        final int mapHeight = tiledMap.getProperties().get("height", Integer.class);
        final int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
        this.scale = h / (mapHeight * tileHeight);

        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, this.scale);

        this.sb = new SpriteBatch();
        this.moon = new Sprite(new Texture(Gdx.files.internal("moon.png")));
        this.moon.translateY(64.01f);
        this.moon.translateX(10);

        final float buttonWidth = w * 0.25f;
        this.leftButton = new Rectangle(0, 0, buttonWidth, h);
        this.centerButton = new Rectangle(buttonWidth, 0, w - 2 * buttonWidth, h);
        this.rightButton = new Rectangle(w - buttonWidth, 0, buttonWidth, h);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
                final Cell cell = getTileCellAt(screenX, screenY);
                cell.setRotation((cell.getRotation() + 1) % 4);

                return false;
            }
        });
    }

    @Override
    public void render(final float delta) {
        final Predicate<Cell> predicate = new Predicate<Cell>() {
            @Override
            public boolean evaluate(final Cell arg0) {
                return isCellFloor(arg0);
            }
        };

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        final float motion = 200 * delta;

        final boolean cannotPanRight = this.tiledMapRenderer.getViewBounds().x + motion > getMapWidth() - this.tiledMapRenderer.getViewBounds().width;
        final boolean cannotPanLeft = this.tiledMapRenderer.getViewBounds().x - motion < 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || rectanglePressed(this.leftButton)) {
            final Array<Cell> cell = getTilesToTheLeft(this.moon, motion);

            if (!cell.select(predicate).iterator().hasNext()) {
                if (cannotPanLeft || cannotPanRight && this.moon.getX() > 250) {
                    if (this.moon.getX() - motion > 0) {
                        this.moon.translateX(-motion);
                    }
                } else {
                    this.camera.translate(-motion, 0);
                }
            }

            this.moon.rotate(motion);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || rectanglePressed(this.rightButton)) {
            final Array<Cell> cell = getTilesToTheRight(this.moon, motion);

            if (!cell.select(predicate).iterator().hasNext()) {
                if (cannotPanRight || cannotPanLeft && this.moon.getX() < 250) {
                    if (this.moon.getX() + motion < this.tiledMapRenderer.getViewBounds().width - this.moon.getWidth()) {
                        this.moon.translateX(motion);
                    }
                } else {
                    this.camera.translate(motion, 0);
                }
            }

            this.moon.rotate(-motion);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || rectanglePressed(this.centerButton)) {
            if (this.moon.getY() + this.moon.getHeight() + motion < Gdx.graphics.getHeight()) {
                this.moon.translateY(motion);
            }
        } else {
            final Array<Cell> cell = getTilesBeneath(this.moon, motion);

            if (cell.contains(null, true)) {
                this.moon.setPosition(10, 64); // lose
            } else if (!cell.select(predicate).iterator().hasNext()) {
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

    private boolean rectanglePressed(final Rectangle rectangle) {
        return Gdx.input.isTouched() && rectangle.contains(Gdx.input.getX(), Gdx.input.getY());
    }

    private Cell getTileCellAt(final float screenX, final float screenY) {
        final Vector3 coords = this.camera.unproject(new Vector3(screenX, screenY, 0));

        final int tilewidth = this.tiledMapRenderer.getMap()
                                .getProperties().get("tilewidth", Integer.class);
        final int tileHeight = this.tiledMapRenderer.getMap()
                                .getProperties().get("tileheight", Integer.class);

        final int x = (int) Math.floor(coords.x / (tilewidth * this.scale));
        final int y = (int) Math.floor(coords.y / (tileHeight * this.scale));

        final TiledMapTileLayer ml = (TiledMapTileLayer) this.tiledMapRenderer.getMap()
                                                                        .getLayers().get(0);
        return ml.getCell(x, y);
    }

    private Array<Cell> getTilesToTheRight(final Sprite sprite, final float motion) {

        final Array<Cell> cells = new Array<>();

        final float x = sprite.getX() + sprite.getWidth() + motion;
        final float y = Gdx.graphics.getHeight() - sprite.getY();

        cells.add(getTileCellAt(x, y), getTileCellAt(x, y - sprite.getHeight() / 2), getTileCellAt(x, y - sprite.getHeight()));

        return cells;
    }

    private Array<Cell> getTilesToTheLeft(final Sprite sprite, final float motion) {

        final Array<Cell> cells = new Array<>();

        final float x = sprite.getX() - motion;
        final float y = Gdx.graphics.getHeight() - sprite.getY();

        cells.add(getTileCellAt(x, y), getTileCellAt(x, y - sprite.getHeight() / 2), getTileCellAt(x, y - sprite.getHeight()));

        return cells;
    }

    private Array<Cell> getTilesBeneath(final Sprite sprite, final float motion) {

        final Array<Cell> cells = new Array<>();

        final float x = sprite.getX();
        final float y = Gdx.graphics.getHeight() - sprite.getY() + motion;

        cells.add(getTileCellAt(x, y), getTileCellAt(x + sprite.getWidth() / 2, y), getTileCellAt(x + sprite.getWidth(), y));

        return cells;
    }

    private boolean isCellFloor(final Cell cell) {
        return cell != null && cell.getTile().getProperties().get("floor", false, Boolean.class);
    }

    private void printDebug() {
        final String spriteInfo = String.format("Sprite: %f, %f", this.moon.getX(), this.moon.getY());
        final String mapInfo = String.format("Map: total width: %f; w,h: %f, %f; x,y: %f, %f", getMapWidth(), this.tiledMapRenderer.getViewBounds().width, this.tiledMapRenderer.getViewBounds().height, this.tiledMapRenderer.getViewBounds().x, this.tiledMapRenderer.getViewBounds().y);
        final String mouseInfo = String.format("Mouse: %d, %d", Gdx.input.getX(), Gdx.input.getY());


        this.font.draw(this.sb, String.format("%s%n%s%n%s", spriteInfo, mapInfo, mouseInfo), 10, 470);
    }

    private float getMapWidth() {
        final int width = this.tiledMapRenderer.getMap().getProperties().get("width", Integer.class);
        final int tilewidth = this.tiledMapRenderer.getMap().getProperties().get("tilewidth", Integer.class);

        return width * tilewidth * this.scale;
    }

    @Override
    public void dispose() {
        this.sb.dispose();
        this.tiledMapRenderer.dispose();
    }
}
