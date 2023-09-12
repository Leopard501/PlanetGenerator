package planet;

import core.Main;

import java.awt.*;
import java.util.function.Supplier;

class Liquid extends Component {

    enum Type implements Pickable {
        MoltenRock(() -> new Color(255, 98, 0), 4),
        SaltWater(() -> new Color(0x254ADE), 14),
        FreshWater(() -> new Color(0x2d4747), 8),
        MoltenMetal(() -> new Color(0xFF3C00), 2),
        LiquidMethane(() -> new Color(0x000513), 2),
        LiquidNitrogen(() -> new Color(0xBAC1D2), 2),
        Mercury(() -> new Color(0x949494), 2),
        Organic(() -> Component.pickColor(
                new Color(0x8CA00A),
                new Color(0xA10909),
                new Color(0x6609A4)
        ), 2),
        Acid(() -> new Color(0x84FF00), 2),
        Sand(() -> new Color(0xf4d444), 2),
        Ash(() -> new Color(0x22201d), 2),
        Mud(() -> new Color(0x5B2707), 2),
        Life(Component::randomColor, 1),
        Runoff(() -> new Color(145, 120, 60), 1);

        final Supplier<Color> color;
        final int chance;

        Type(Supplier<Color> color, int chance) {
            this.color = color;
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

    Liquid() {
        shape = pick(Shape.class);
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
        } else {
            type = pick(Type.class);
            low = type.color.get();
        }

        sprite = Main.sprites.get("planet_liquid_" + shape.name());
        if (shape == Shape.None) description = "No liquid";
        else {
            if (type != null) description = type.name() + " " + shape.name();
            else description = "Unknown " + shape.name();
        }
    }
}