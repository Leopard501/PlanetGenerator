package planet.components;

import core.Main;

import java.awt.*;
import java.util.function.Supplier;

public class Liquid extends planet.components.Component {

    public enum Type implements Pickable {
        MoltenRock(
                () -> new Color(255, 128, 0),
                () -> new Color(0xFF2F00), 4),
        SaltWater(
                () -> new Color(0x266fff),
                () -> new Color(0x4242CC), 14),
        FreshWater(
                () -> new Color(0x3E8686),
                () -> new Color(0x38657C), 8),
        MoltenMetal(
                () -> new Color(0xFFE285),
                () -> new Color(255, 128, 0), 2),
        LiquidMethane(
                () -> new Color(50, 50, 85),
                () -> new Color(10, 10, 20), 2),
        LiquidNitrogen(
                () -> new Color(0xE4E7EF),
                () -> new Color(0xA2ACC2), 2),
        Mercury(
                () -> new Color(0xFFFFFF),
                () -> new Color(54, 54, 54), 2),
        Organic(
                () -> planet.components.Component.pickColor(
                    new Color(0xB2AF11),
                    new Color(0xB20707),
                    new Color(0x9C0FC4)
                ),
                () -> planet.components.Component.pickColor(
                        new Color(0x406705),
                        new Color(0x490000),
                        new Color(0x3D0580)
                ), 2),
        Acid(
                () -> new Color(0x84FF00),
                () -> new Color(0x9BC939), 2),
        Sand(
                () -> new Color(199, 165, 116),
                () -> new Color(187, 137, 86), 2),
        Ash(
                () -> new Color(0x302d28),
                () -> new Color(0x231E1D), 2),
        Mud(
                () -> new Color(0x5B2707),
                () -> new Color(0x411A04), 2),
        Runoff(
                () -> new Color(145, 120, 60),
                () -> new Color(112, 64, 31), 1);

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
        None(4),
        Lakes(2),
        Rivers(2),
        Seas(2),
        Oceans(2),
        GlobalOcean(1);

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

    public boolean glows;

    public Liquid() {
        shape = pick(Shape.class);
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
            high = randomColor();

            glows = Main.app.random(5) < 1;
        } else {
            type = pick(Type.class);
            low = type.low.get();
            high = type.high.get();

            glows = type.equals(Type.MoltenRock) || type.equals(Type.MoltenMetal);
        }

        createAssets();
    }

    public void createAssets() {
        sprite = createImage(Main.sprites.get("planet_liquid_" + shape.name()));
        if (shape == Shape.None) description = null;
        else {
            if (type != null) description = type.name() + " " + shape.name();
            else description = "Unknown " + shape.name();
        }
    }
}