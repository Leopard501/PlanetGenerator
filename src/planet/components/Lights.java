package planet.components;

import core.Main;

import java.awt.*;
import java.util.function.Supplier;

public class Lights extends planet.components.Component {

    public enum Type implements Pickable {
        Firelit(() -> new Color(255, 128, 0), 5),
        Incandescent(() -> new Color(255, 180, 0), 3),
        LED(() -> Color.WHITE, 1),
        Neon(() -> Color.CYAN, 1),
        Red(() -> Color.RED, 1);

        public final Supplier<Color> color;
        final int chance;

        Type(Supplier<Color> color, int chance) {
            this.color = color;
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

    public enum Shape implements Pickable {
        None(5),
        Campfires(4),
        Towns(3),
        Cities(2),
        Roads(1),
        Metropolis(1);

        final int chance;

        Shape(int chance){
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

    // Should be disabled if there are no complex plants
    public Lights(boolean hasComplexLife) {
        if (!hasComplexLife) shape = Shape.None;
        else shape = pick(Shape.class);

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
        sprite = createImage(Main.sprites.get("planet_lights_" + shape.name()));
        if (shape == Shape.None) description = null;
        else {
            if (type != null) description = type.name() + " " + shape.name();
            else description = "Unknown " + shape.name();
        }
    }
}
