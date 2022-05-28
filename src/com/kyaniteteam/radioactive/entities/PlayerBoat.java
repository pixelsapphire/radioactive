package com.kyaniteteam.radioactive.entities;

import com.kyaniteteam.radioactive.GameScene;
import com.kyaniteteam.radioactive.GameState;
import com.kyaniteteam.radioactive.ui.GameHUD;
import com.rubynaxela.kyanite.game.GameContext;
import com.rubynaxela.kyanite.game.assets.AssetsBundle;
import com.rubynaxela.kyanite.game.assets.Texture;
import com.rubynaxela.kyanite.game.entities.AnimatedEntity;
import com.rubynaxela.kyanite.game.entities.CompoundEntity;
import com.rubynaxela.kyanite.game.entities.MovingEntity;
import com.rubynaxela.kyanite.system.Clock;
import com.rubynaxela.kyanite.util.Colors;
import com.rubynaxela.kyanite.util.MathUtils;
import com.rubynaxela.kyanite.util.Vec2;
import com.rubynaxela.kyanite.window.Window;
import com.rubynaxela.kyanite.window.event.KeyListener;
import org.jetbrains.annotations.NotNull;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerBoat extends CompoundEntity implements AnimatedEntity, MovingEntity, KeyListener {

    private static final AssetsBundle assets = GameContext.getInstance().getAssetsBundle();
    private static final Texture
            hullTexture = assets.get("texture.player_boat"),
            barrelTexture = assets.get("texture.barrel_top");

    private final Window window = GameContext.getInstance().getWindow();
    private final Clock clock = GameContext.getInstance().getClock();
    private final GameScene scene;
    private final GameHUD hud;
    private final GameState gameState;

    private final List<RectangleShape> barrelSlots = new ArrayList<>(6);
    private float baseVelocity = 80;
    private float lastBarrelDroppedTime = -1;

    public PlayerBoat(@NotNull GameScene scene, @NotNull GameState state) {
        super(Vec2.f(600, 600));
        this.scene = scene;
        this.hud = window.getHUD();
        this.gameState = state;
        this.gameState.barrels = 5;

        final RectangleShape hull = hullTexture.createRectangleShape(false);
        hull.setSize(Vec2.f(100, 100));
        hull.setPosition(Vec2.divideFloat(hull.getSize(), -2));
        add(hull);

        final float barrelSlotSize = 32f;
        for (int i = 0; i < state.barrels; i++) {
            final RectangleShape barrelSlot = barrelTexture.createRectangleShape(true);
            barrelSlot.setSize(Vec2.f(barrelSlotSize, barrelSlotSize));
            barrelSlot.setPosition(Vec2.f((i % 2 + 0.5f) * barrelSlotSize / 2, (i % 3 - 0.5f) * barrelSlotSize / 2));
            add(barrelSlot);
            barrelSlots.add(barrelSlot);
        }

        window.addKeyListener(this);
        hud.update(gameState);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.key.equals(Keyboard.Key.H) && gameState.barrels > 0
            && clock.getTime().asSeconds() - lastBarrelDroppedTime > 2) {
            scene.scheduleToAdd(new DroppedBarrel(getPosition()));
            scene.schedule(s -> s.bringToTop(this));
            if (gameState.barrels-- > 0) barrelSlots.get(gameState.barrels).setFillColor(Colors.TRANSPARENT);
            hud.update(gameState);
            lastBarrelDroppedTime = clock.getTime().asSeconds();
        }
    }

    @Override
    public void animate(@NotNull Time deltaTime, @NotNull Time elapsedTime) {
        if (((GameScene) window.getScene()).getBarrels().stream().anyMatch(b -> MathUtils.isInsideCircle(
                getPosition(), b.getPosition(), b.getGlobalBounds().width / 2))) baseVelocity = 40;
        else baseVelocity = 80;
        if (Keyboard.isKeyPressed(Keyboard.Key.A)) rotate(-100 * deltaTime.asSeconds());
        if (Keyboard.isKeyPressed(Keyboard.Key.D)) rotate(100 * deltaTime.asSeconds());
    }

    @Override
    public @NotNull Vector2f getVelocity() {
        if (Keyboard.isKeyPressed(Keyboard.Key.W)) return Vec2.multiply(MathUtils.direction(getRotation()), baseVelocity);
        else return Vector2f.ZERO;
    }

    @Override
    public void setVelocity(@NotNull Vector2f velocity) {
    }
}