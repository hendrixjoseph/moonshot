package com.joehxblox.moonshot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import com.badlogic.gdx.utils.TimeUtils;
import com.joehxblox.moonshot.sprite.GameSprite;
import com.joehxblox.moonshot.sprite.Meteor;
import com.joehxblox.moonshot.sprite.Moon;
import com.joehxblox.moonshot.sprite.Star;

import static com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class GameScreen extends ScreenAdapter {
    private final Moonshot game;

    private final OrthographicCamera camera = new OrthographicCamera();
    private final BitmapFont font = new BitmapFont();
    private final SpriteBatch sb = new SpriteBatch();

    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final Moon moon;

    private final Rectangle leftButton;
    private final Rectangle centerButton;
    private final Rectangle rightButton;

    private final Array<GameSprite> npcs = new Array<>();

    private final float scale;

    private boolean gameOver = false;
    private long lastStar = TimeUtils.nanoTime();
    private long lastMeteor = TimeUtils.nanoTime();
    private final int dropsGathered = 0;

    private final Predicate<Cell> cellFloorPredicate = new Predicate<Cell>() {
        @Override
        public boolean evaluate(final Cell arg0) {
            return isCellFloor(arg0);
        }
    };

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

        this.moon = new Moon();

        final float buttonWidth = w * 0.25f;
        this.leftButton = new Rectangle(0, 0, buttonWidth, h);
        this.centerButton = new Rectangle(buttonWidth, 0, w - 2 * buttonWidth, h);
        this.rightButton = new Rectangle(w - buttonWidth, 0, buttonWidth, h);
    }

    @Override
    public void render(final float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!this.gameOver) {
            playGame(delta);
        }

        this.camera.update();

        this.tiledMapRenderer.setView(this.camera);
        this.tiledMapRenderer.render();

        this.sb.begin();

        if (this.gameOver) {
            this.font.draw(this.sb, "Game Over", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        }

        this.moon.draw(this.sb);

        for (final GameSprite npc : this.npcs) {
            npc.draw(this.sb);
        }

        this.sb.end();
    }

    private void playGame(final float delta) {
        final float motion = 200 * delta;

        final boolean cannotPanRight = this.tiledMapRenderer.getViewBounds().x + motion > getMapWidth() - this.tiledMapRenderer.getViewBounds().width;
        final boolean cannotPanLeft = this.tiledMapRenderer.getViewBounds().x - motion < 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || rectanglePressed(this.leftButton)) {
            final Array<Cell> cell = getTilesToTheLeft(this.moon.getSprite(), motion);

            if (!cell.select(this.cellFloorPredicate).iterator().hasNext()) {
                if (cannotPanLeft || cannotPanRight && this.moon.getX() > 250) {
                    if (this.moon.getX() - motion > 0) {
                        this.moon.translateX(-motion);
                    }
                } else {
                    panCameraRight(-motion);
                }
            }

            this.moon.rotate(motion);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || rectanglePressed(this.rightButton)) {
            final Array<Cell> cell = getTilesToTheRight(this.moon.getSprite(), motion);

            if (!cell.select(this.cellFloorPredicate).iterator().hasNext()) {
                if (cannotPanRight || cannotPanLeft && this.moon.getX() < 250) {
                    if (this.moon.getX() + motion < this.tiledMapRenderer.getViewBounds().width - this.moon.getWidth()) {
                        this.moon.translateX(motion);
                    }
                } else {
                    panCameraRight(motion);
                }
            }

            this.moon.rotate(-motion);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || rectanglePressed(this.centerButton)) {
            if (this.moon.getY() + this.moon.getHeight() + motion < Gdx.graphics.getHeight()) {
                this.moon.translateY(motion);
            }
        } else {
            final Array<Cell> cell = getTilesBeneath(this.moon.getSprite(), motion);

            if (this.moon.getY() + this.moon.getHeight() < 0) {
                this.gameOver = true;
            }
            if (!cell.select(this.cellFloorPredicate).iterator().hasNext()) {
                this.moon.translateY(-motion);
            }
        }

        if (TimeUtils.nanoTime() - this.lastStar > 1000000000) {
            this.npcs.add(new Star());
            this.lastStar = TimeUtils.nanoTime();
        }

        if (TimeUtils.nanoTime() - this.lastMeteor > 1000000000) {
            this.npcs.add(new Meteor());
            this.lastMeteor = TimeUtils.nanoTime();
        }

        for (final GameSprite npc : this.npcs) {
            npc.translateY(-motion);

            if (npc.isOffScreen()) {
                this.npcs.removeValue(npc, true);
            }

            if (this.moon.overlaps(npc.getRectangle())) {
                this.npcs.removeValue(npc, true);
            }
        }
    }

    private void panCameraRight(final float motion) {
        this.camera.translate(motion, 0);
        final MapLayer background = this.tiledMapRenderer.getMap().getLayers().get("background");
        background.setOffsetX(background.getOffsetX() + motion);

        for (final GameSprite npc : this.npcs) {
            npc.translateX(-motion);
        }
    }

    private boolean rectanglePressed(final Rectangle rectangle) {
        return Gdx.input.isTouched() && rectangle.contains(Gdx.input.getX(), Gdx.input.getY());
    }

    private Cell getTileCellAt(final float screenX, final float screenY) {
        final Vector3 coords = this.camera.unproject(new Vector3(screenX, screenY, 0));

        final int tilewidth = this.tiledMapRenderer.getMap().getProperties().get("tilewidth", Integer.class);
        final int tileHeight = this.tiledMapRenderer.getMap().getProperties().get("tileheight", Integer.class);

        final int x = (int) Math.floor(coords.x / (tilewidth * this.scale));
        final int y = (int) Math.floor(coords.y / (tileHeight * this.scale));

        final TiledMapTileLayer ml = (TiledMapTileLayer) this.tiledMapRenderer.getMap().getLayers().get("ground");
        return ml.getCell(x, y);
    }

    private Array<Cell> getTilesToTheRight(final Sprite sprite, final float motion) {

        final float x = sprite.getX() + sprite.getWidth() + motion;

        return getTilesToTheSide(sprite, x);
    }

    private Array<Cell> getTilesToTheLeft(final Sprite sprite, final float motion) {

        final float x = sprite.getX() - motion;

        return getTilesToTheSide(sprite, x);
    }

    private Array<Cell> getTilesToTheSide(final Sprite sprite, final float x) {
        final float y = Gdx.graphics.getHeight() - sprite.getY();

        final Array<Cell> cells = new Array<>();

        cells.add(getTileCellAt(x, y), getTileCellAt(x, y - sprite.getHeight() / 2), getTileCellAt(x, y - sprite.getHeight()));

        return cells;
    }

    private Array<Cell> getTilesBeneath(final Sprite sprite, final float motion) {
        final float y = Gdx.graphics.getHeight() - sprite.getY() + motion;

        return getTilesAboveOrBeneath(sprite, y);
    }

    private Array<Cell> getTilesAbove(final Sprite sprite, final float motion) {
        final float y = Gdx.graphics.getHeight() - sprite.getY() - sprite.getHeight() + motion;

        return getTilesAboveOrBeneath(sprite, y);
    }

    private Array<Cell> getTilesAboveOrBeneath(final Sprite sprite, final float y) {
        final float x = sprite.getX();

        final Array<Cell> cells = new Array<>();

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
        this.font.dispose();
        this.game.dispose();
    }
}
