package com.kyaniteteam.radioactive;

import com.kyaniteteam.radioactive.entities.DroppedBarrel;
import com.kyaniteteam.radioactive.entities.boats.EnemyBoat;
import com.kyaniteteam.radioactive.entities.boats.PlayerBoat;
import com.kyaniteteam.radioactive.terrain.Depth;
import com.rubynaxela.kyanite.game.GameContext;
import com.rubynaxela.kyanite.game.Scene;
import com.rubynaxela.kyanite.game.assets.AudioHandler;
import org.jetbrains.annotations.NotNull;
import org.jsfml.graphics.Color;

import java.util.List;
import java.util.stream.Collectors;

public class GameScene extends Scene {

    private static final AudioHandler audioHandler = GameContext.getInstance().getAudioHandler();
    private final Background backgroundShader = new Background();
    private final PlayerBoat player = new PlayerBoat(this);

    private final Depth depth1 = new Depth(this);
    private final Depth depth2 = new Depth(this);
    private final Depth depth3 = new Depth(this);

    public GameScene(@NotNull SceneLoader.SceneData data) {
        data.enemies.stream().map(e -> e.createEnemyBoat(this)).forEach(EnemyBoat::addToScene);
        setBackgroundColor(new Color(40, 40, 80));
    }

    public PlayerBoat getPlayer() {
        return player;
    }

    public List<DroppedBarrel> getBarrels() {
        return drawables.stream().filter(b -> b instanceof DroppedBarrel)
                        .map(b -> (DroppedBarrel) b).collect(Collectors.toList());
    }

    public List<EnemyBoat> getEnemyBoats() {
        return drawables.stream().filter(b -> b instanceof EnemyBoat)
                        .map(b -> (EnemyBoat) b).collect(Collectors.toList());
    }

    public List<Depth> getDepths() {
        return drawables.stream().filter(d -> d instanceof Depth)
                        .map(d -> (Depth) d).collect(Collectors.toList());
    }

    @Override
    protected void init() {
        add(depth1, depth2, depth3);
        add(backgroundShader);
        add(player);
        audioHandler.playSound("sound.astronomia", "music", 100, 1, true);
    }

    @Override
    protected void loop() {

    }
}
