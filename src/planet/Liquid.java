package planet;

import core.Main;

import java.awt.*;
import java.util.function.Supplier;

class Liquid extends Component {

    enum Type implements Pickable {
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
                () -> Component.pickColor(
                    new Color(0xB2AF11),
                    new Color(0xB20707),
                    new Color(0x9C0FC4)
                ),
                () -> Component.pickColor(
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

    enum Shape implements Pickable {
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

    Shape shape;
    Type type;

    boolean glows;

    Liquid(long seed) {
        for (long secretSeed : SECRET_SEEDS) {
            if (seed == secretSeed) {
                secretSeeds(seed);
                return;
            }
        }

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

        sprite = createImage(Main.sprites.get("planet_liquid_" + shape.name()));
        if (shape == Shape.None) description = "No Liquid";
        else {
            if (type != null) description = type.name() + " " + shape.name();
            else description = "Unknown " + shape.name();
        }
    }

    private void secretSeeds(long seed) {
        switch ("" + seed) {
            case "666" -> {
                shape = Shape.Oceans;
                high = Color.RED;
                low = new Color(0x490000);
                description = "Blood Seas";
            }
            // "ASCII", "MARS", "MERCURY", "VENUS", MOON
            case "6583677373", "77658283", "77698267858289", "8669788583", "77797978" -> {
                shape = Shape.None;
                low = Color.BLACK;
                high = Color.BLACK;
                description = "No liquid";
            }
            // "EARTH"
            case "6965828472" -> {
                shape = Shape.Oceans;
                type = Type.SaltWater;
                low = type.low.get();
                high = type.high.get();
                description = type.name() + " " + shape.name();
            }
            // JUPITER, SATURN
            case "74858073846982", "836584858278" -> {
                shape = Shape.GlobalOcean;
                type = Type.LiquidNitrogen;
                low = type.low.get();
                high = type.high.get();
                description = "MetallicHydrogen " + shape.name();
            }
            // TITAN
            case "8473846578" -> {
                shape = Shape.Seas;
                type = Type.LiquidMethane;
                low = type.low.get();
                high = type.high.get();
                description = type.name() + " " + shape.name();
            }
            // KRAKEN
            case "758265756978" -> {
                shape = Shape.GlobalOcean;
                high = new Color(0x092952);
                low = new Color(0x030A2D);
                description = "Dark Ocean";
            }
            // FUTURE
            case "708584858269" -> {
                shape = Shape.Oceans;
                type = Type.FreshWater;
                low = type.low.get();
                high = type.high.get();
                description = "Warm Oceans";
            }
        }
        sprite = createImage(Main.sprites.get("planet_liquid_" + shape.name()));
    }
}