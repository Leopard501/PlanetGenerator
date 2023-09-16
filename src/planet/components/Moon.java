package planet.components;

import core.Main;
import processing.core.PApplet;
import processing.core.PConstants;

import java.awt.*;
import java.util.function.Supplier;

public class Moon extends Component {

    public enum Type implements Pickable {
        Icy(
                () -> pickColor(
                        new Color(200, 200, 200),
                        new Color(230, 230, 230)
                ),
                () -> pickColor(
                        new Color(165, 185, 200),
                        new Color(175, 120, 90)
                ), 3),
        Rocky(
                () -> pickColor(
                        new Color(245, 245, 245),
                        new Color(182, 182, 161),
                        new Color(220, 215, 190),
                        new Color(185, 185, 185),
                        new Color(180, 160, 150)
                ),
                () -> pickColor(
                        new Color(95, 75, 70),
                        new Color(40, 35, 40),
                        new Color(85, 85, 90),
                        new Color(110, 105, 90),
                        new Color(140, 125, 120)
                ), 5),
        Sulphurous(
                () -> new Color(245, 195, 115),
                () -> new Color(20, 10, 0), 1);

        final Supplier<Color> high;
        final Supplier<Color> low;
        final int chance;

        Type(Supplier<Color> high, Supplier<Color> low, int chance) {
            this.high = high;
            this.low = low;
            this.chance = chance;
        }

        @Override
        public int getChance() {return chance;}

        @Override
        public int getOrdinal() {return ordinal();}
    }

    private enum Size {
        Tiny,
        Large,
        Small
    }

    private static final int MIN_DIST = 150;
    private static final int MAX_DIST = 350;

    private Type type;
    private Size size;
    private int shape;
    private int orbitDirection;
    private float orbitDistance;
    private float orbitPosition;

    public float screenPosition;
    public boolean isBehind;

    public Moon(boolean extraIcy, float rotationSpeed) {
        if (rotationSpeed < 0) orbitDirection = -1;
        else orbitDirection = 1;
        Main.distribute(
                () -> {
                    size = Size.Tiny;
                    orbitDistance = Main.app.random(50, 350);
                },
                () -> {
                    size = Size.Small;
                    Main.distribute(
                            () -> orbitDistance = Main.app.random(0.4f),
                            () -> orbitDistance = Main.app.random(0.4f, 0.8f),
                            () -> orbitDistance = Main.app.random(0.8f, 1)
                    );
                    orbitDistance *= Main.app.random(2) < 1 ? -1 : 1;
                    float mid = MAX_DIST - (MAX_DIST - MIN_DIST) / 2f;
                    float diff = mid - MIN_DIST;
                    orbitDistance = mid + diff * orbitDistance;
                    },
                () -> {
                    size = Size.Large;
                    Main.distribute(
                            () -> orbitDistance = Main.app.random(0.2f),
                            () -> orbitDistance = Main.app.random(0.2f, 0.5f),
                            () -> orbitDistance = Main.app.random(0.5f, 1)
                    );
                    orbitDistance *= Main.app.random(2) < 1 ? -1 : 1;
                    float mid = MAX_DIST - (MAX_DIST - MIN_DIST) / 2f;
                    float diff = mid - MIN_DIST;
                    orbitDistance = mid + diff * orbitDistance;
                }
        );

        shape = (int) Main.app.random(5);
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
            high = randomColor();
        } else {
            if (extraIcy && Main.app.random(2) < 1) type = Type.Icy;
            else type = pick(Type.class);
            low = type.low.get();
            high = type.high.get();
        }

        createAssets();
    }

    public void createAssets() {
        sprite = createImage(Main.sprites.get(
                "planet_moon_" + size.name() + "_" + PApplet.nf(shape, 3)));
        if (type != null) description = size.name() + " " + type.name() + " Moon";
        else description = size.name() + " Unknown Moon";
    }

    public void update() {
        orbitPosition += 0.01f * (isBehind ? -1 : 1);
        if (orbitPosition >= PConstants.HALF_PI || orbitPosition <= -PConstants.HALF_PI) isBehind = !isBehind;
        screenPosition = (float) Math.sin(orbitPosition) * orbitDistance;
    }
}
