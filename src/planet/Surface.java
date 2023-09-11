package planet;

import core.Main;

import java.awt.*;
import java.util.function.Supplier;

class Surface extends Component {

    private enum Type implements Pickable {
        Igneous(() -> new Color(0x2f2f31), 1),
        Sedimentary(() -> new Color(0x9c481b), 3),
        Metamorphic(() -> new Color(0x59544F), 3),
        Ice(() -> new Color(0xCCFFFF), 1),
        Metal(() -> new Color(0xBEBEBE), 1),
        Sand(() -> new Color(0xBE9429), 1),
        Mud(() -> new Color(0x5B2707), 2),
        Rust(() -> new Color(203, 70, 30), 2);

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

    Surface() {
        shape = Shape.values()[(int) Main.app.random(Shape.values().length)];
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            color = randomColor();
        } else {
            type = pick(Type.class);
            color = type.color.get();
        }

        sprite = Main.sprites.get("planet_surface_" + shape.name());
        if (type != null) description = type.name() + " " + shape.name();
        else description = "Unknown " + shape.name();
        description += " Surface";
    }
}
