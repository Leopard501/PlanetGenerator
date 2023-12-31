package planet.components;

import core.Main;

import java.awt.*;
import java.util.function.Supplier;

public class Gas extends Component {

    private enum Type implements Pickable {
        Water(() -> Color.WHITE, 10),
        Hydrogen(() -> new Color(241, 209, 157), 1),
        Helium(() -> new Color(253, 204, 197), 1),
        Acid(() -> new Color(0xC7FFB4), 1),
        Dust(() -> new Color(175, 140, 100), 1),
        Metal(() -> new Color(161, 139, 127), 1),
        Ammonia(() -> new Color(200, 200, 220), 1),
        Methane(() -> new Color(200, 180, 100), 1),
        CO2(() -> new Color(240, 225, 195), 1),
        Spores(() -> Component.pickColor(
                new Color(140, 180, 175),
                new Color(90, 70, 100),
                new Color(150, 140, 90)
        ), 1),
        Smoke(() -> new Color(0x4F4A4A), 1);

        final Supplier<Color> color;
        final int chance;

        Type(Supplier<Color> color, int chance) {
            this.color = color;
            this.chance = chance;
        }

        @Override
        public int getChance() {return chance;}

        @Override
        public int getOrdinal() {return this.ordinal();}
    }

    public enum Shape implements Pickable {
        None(3),
        Trace(2),
        Sparse(1),
        Dense(1),
        Global(1);

        final int chance;

        Shape(int chance) {
            this.chance = chance;
        }

        @Override
        public int getChance() {
            return chance;
        }

        @Override
        public int getOrdinal() {
            return ordinal();
        }
    }

    public Shape shape;
    public Type type;

    public Gas() {
        shape = Shape.values()[(int) Main.app.random(Shape.values().length)];
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
        } else {
            type = pick(Type.class);
            low = type.color.get();
        }
        high = low;

        createAssets();
    }

    public void createAssets() {
        sprite = createImage(Main.sprites.get("planet_gas_" + shape.name()));
        if (shape == Shape.None) {
            description = null;
            return;
        }
        if (type != null) description = shape.name() + " " + type.name();
        else description = shape.name() + " Unknown Gas";
        description += " Clouds";
    }
}