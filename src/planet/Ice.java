package planet;

import core.Main;

import java.awt.*;
import java.util.function.Supplier;

public class Ice extends Component {

    enum Type implements Pickable {
        Ice(() -> new Color(0xCCFFFF), 8),
        Obsidian(() -> new Color(0x261C31), 2),
        Waste(() -> new Color(107, 93, 28), 1),
        Organic(() -> Component.pickColor(
                new Color(0x8CA00A),
                new Color(0xA10909),
                new Color(0x6609A4)
        ), 1);

        final Supplier<Color> color;
        final int chance;

        Type(java.util.function.Supplier<Color> color, int chance) {
            this.color = color;
            this.chance = chance;
        }

        @Override
        public int getChance() {return chance;}

        @Override
        public int getOrdinal() {return ordinal();}
    }

    enum Shape implements Pickable {
        None(6),
        SmallCaps(5),
        MediumCaps(3),
        LargeCaps(2),
        EyeballSheet(1),
        Burgs(1),
        GlobalSheet(1);

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

    Shape shape;
    Type type;

    // Should be disabled if the type is Ice and there is hot liquid
    Ice(boolean isCool) {
//        shape = Shape.values()[(int) Main.app.random(Shape.values().length)];
        shape = pick(Shape.class);
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
        } else {
            type = pick(Type.class);
            low = type.color.get();
        }

        if (type != null) if (!isCool && type.equals(Type.Ice)) shape = Shape.None;

        sprite = Main.sprites.get("planet_ice_" + shape.name());
        if (shape == Shape.None) description = "No sheets";
        else {
            if (type != null) description = type.name() + " " + shape.name();
            else description = "Unknown " + shape.name();
        }
    }
}
