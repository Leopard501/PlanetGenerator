package planet.components;

import core.Main;
import processing.core.PImage;

import java.awt.*;
import java.util.function.Supplier;

public class GasGiantClouds extends Component {

    public static final Color GLOW_COLOR = new Color(255, 30, 0);

    private enum Type implements Pickable {
        Ammonia(
                () -> pickColor(
                        new Color(225, 165, 125),
                        new Color(235, 220, 210),
                        new Color(185, 165, 120)
                ),
                () -> pickColor(
                        new Color(155, 120, 100),
                        new Color(170, 150, 130),
                        new Color(75, 80, 85),
                        new Color(130, 120, 100)
                ), 0, 5),
        Water(
                () -> new Color(200, 205, 210),
                () -> new Color(125, 135, 145), 0, 3),
        Methane(
                () -> new Color(110, 125, 145),
                () -> new Color(100, 120, 140), 0, 3),
        Alkali(
                () -> pickColor(
                        new Color(115, 130, 140),
                        new Color(180, 200, 230)
                ),
                () -> pickColor(
                        new Color(95, 120, 150),
                        new Color(23, 79, 194)
                ), 0.3f, 3),
        Silicate(
                () -> new Color(185, 175, 165),
                () -> new Color(145, 120, 120), 1, 3);

        final Supplier<Color> low;
        final Supplier<Color> high;
        final float glowLevel;
        final int chance;

        Type(Supplier<Color> high, Supplier<Color> low, float glowLevel, int chance) {
            this.high = high;
            this.low = low;
            this.glowLevel = glowLevel;
            this.chance = chance;
        }

        @Override
        public int getChance() {return chance;}

        @Override
        public int getOrdinal() {return ordinal();}
    }

    public enum Shape {
        Banded,
        Uniform,
        Chaotic
    }

    public Shape shape;
    public Type type;

    public float glowLevel;
    public PImage baseSprite;

    public GasGiantClouds() {
        shape = Shape.values()[(int) Main.app.random(Shape.values().length)];
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
            high = randomColor();

            if (Main.app.random(3) < 1) glowLevel = Main.app.random(1);
        } else {
            type = pick(Type.class);
            low = type.low.get();
            high = type.high.get();
            glowLevel = type.glowLevel;
        }

        createAssets();
    }

    public void createAssets() {
        baseSprite = Main.sprites.get("planet_gasGiantClouds_" + shape.name());
        sprite = createImage(Main.sprites.get("planet_gasGiantClouds_" + shape.name()));
        if (type != null) description = shape.name() + " " + type.name();
        else description = shape.name() + " Unknown";
        description += " Clouds";
    }
}
