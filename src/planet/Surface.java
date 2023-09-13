package planet;

import core.Main;

import java.awt.*;
import java.util.function.Supplier;

class Surface extends Component {

    private enum Type implements Pickable {
        Igneous(
                () -> new Color(0x3c3c50),
                () -> new Color(0x1b1b1b), 1),
        Sedimentary(
                () -> new Color(0xc45212),
                () -> new Color(0x96331b), 3),
        Metamorphic(
                () -> new Color(0x817B73),
                () -> new Color(0x434357), 3),
        Ice(
                () -> new Color(0xFFFFFF),
                () -> new Color(0xc8f2ff), 1),
        Metal(
                () -> new Color(0xFFFFFF),
                () -> new Color(0x948A8A), 1),
        Sand(
                () -> new Color(0xfad733),
                () -> new Color(0xf3a844), 1),
        Mud(
                () -> new Color(0x833607),
                () -> new Color(0x392B4B), 2),
        Rust(
                () -> new Color(203, 70, 30),
                () -> new Color(0x3D3D41), 2);

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
        public int getOrdinal() {return this.ordinal();}
    }

    private enum Shape {
        Cratered,
        Mountainous,
        Hilly,
        Fissured,
        Speckled,
        Roads
    }

    Shape shape;
    Type type;

    Surface(long seed) {
        for (long secretSeed : SECRET_SEEDS) {
            if (seed == secretSeed) {
                secretSeeds(seed);
                return;
            }
        }

        shape = Shape.values()[(int) Main.app.random(Shape.values().length)];
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
            high = randomColor();
        } else {
            type = pick(Type.class);
            low = type.low.get();
            high = type.high.get();
        }

        sprite = createImage(Main.sprites.get("planet_surface_" + shape.name()));
        if (type != null) description = type.name() + " " + shape.name();
        else description = "Unknown " + shape.name();
        description += " Surface";
    }

    private void secretSeeds(long seed) {
        switch ("" + seed) {
            case "666" -> {
                shape = Shape.Fissured;
                type = Type.Igneous;
                low = type.low.get();
                high = type.high.get();
                description = type.name() + " " + shape.name();
            }
            // "ASCII"
            case "6583677373" -> {
                shape = Shape.Roads;
                low = Color.BLACK;
                high = new Color(0x00FF33);
                description = "8-Bit Surface";
            }
            // "EARTH"
            case "6965828472" -> {
                shape = Shape.Hilly;
                type = Type.Mud;
                low = type.low.get();
                high = type.high.get();
                description = type.name() + " " + shape.name();
            }
        }
        sprite = createImage(Main.sprites.get("planet_surface_" + shape.name()));
    }
}
