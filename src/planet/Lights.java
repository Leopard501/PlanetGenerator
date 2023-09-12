package planet;

import core.Main;
import processing.core.PImage;

import java.awt.*;
import java.util.function.Supplier;

public class Lights extends Component {

    enum Type implements Pickable {
        Firelit(() -> new Color(255, 128, 0), 5),
        Incandescent(() -> new Color(255, 180, 0), 3),
        LED(() -> Color.WHITE, 1),
        Neon(() -> Color.CYAN, 1),
        Red(() -> Color.RED, 1);

        final Supplier<Color> color;
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

    enum Shape implements Pickable {
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

    Shape shape;
    Type type;

    // Should be disabled if there are no complex plants
    Lights(boolean hasComplexLife) {
        if (!hasComplexLife) shape = Shape.None;
        else shape = pick(Shape.class);

        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
        } else {
            type = pick(Type.class);
            low = type.color.get();
        }
        high = low;

        sprite = createImage(Main.sprites.get("planet_lights_" + shape.name()));
        if (shape == Shape.None) description = "No complex life";
        else {
            if (type != null) description = type.name() + " " + shape.name();
            else description = "Unknown " + shape.name();
        }
    }
}
