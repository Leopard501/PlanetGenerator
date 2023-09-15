package planet.components;

import core.Main;

import java.awt.*;
import java.util.function.Supplier;

public class Ice extends planet.components.Component {

    public enum Type implements Pickable {
        Ice(
                () -> new Color(0xe5e3df),
                () -> new Color(0xc1d5d6), 8),
        Obsidian(
                () -> new Color(0x351F4F),
                () -> new Color(0x160A23), 2),
        Waste(
                () -> new Color(107, 93, 28),
                () -> new Color(75, 65, 17), 1),
        /*Organic(() -> Component.pickColor(
                new Color(0x8CA00A),
                new Color(0xA10909),
                new Color(0x6609A4)
        ), 1)*/;

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

    public enum Shape implements Pickable {
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

    public Shape shape;
    public Type type;

    // Should be disabled if the type is Ice and there is hot liquid
    public Ice(boolean isCool) {
        shape = pick(Shape.class);
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
            high = randomColor();
        } else {
            type = pick(Type.class);
            low = type.low.get();
            high = type.high.get();
        }
        if (type != null) if (!isCool && type.equals(Type.Ice)) shape = Shape.None;

        createAssets();
    }

    public void createAssets() {
        sprite = createImage(Main.sprites.get("planet_ice_" + shape.name()));
        if (shape == Shape.None) description = null;
        else {
            if (type != null) description = type.name() + " " + shape.name();
            else description = "Unknown " + shape.name();
        }
    }
}
