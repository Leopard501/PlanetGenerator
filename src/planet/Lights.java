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

        sprite = Main.sprites.get("planet_lights_" + shape.name());
        if (shape == Shape.None) description = "No complex life";
        else {
            if (type != null) description = type.name() + " " + shape.name();
            else description = "Unknown " + shape.name();
        }
    }

    public void display(PImage liquid, PImage ice, PImage shadow) {
        PImage img = sprite.copy();
        img.loadPixels();
        liquid.loadPixels();
        ice.loadPixels();
        for (int x = 0; x < img.width; x++) {
            for (int y = 0; y < img.height; y++) {
                int loc = x + y * img.width;

                int liqAlph = liquid.pixels[loc] >> 24 & 255;
                boolean isLiq = liqAlph > 0;
                int iceAlph = ice.pixels[loc] >> 24 & 255;
                boolean isIce = iceAlph > 0;
                int imgAlph = img.pixels[loc] >> 24 & 255;
                float imgProp = imgAlph / 255f;
                int shadAlph = shadow.pixels[loc] >> 24 & 255;
                float shadProp = shadAlph / 255f;

                if (isLiq || isIce) liqAlph = 0;
                else liqAlph = (int) (imgProp * shadProp * 255);

                img.pixels[loc] = (liqAlph << 24) + 0xFFFFFF;
            }
        }
        img.updatePixels();

        Main.app.tint(low.getRGB());
        Main.app.image(img, Main.WIDTH / 2f, Planet.HEIGHT, 160, 160);
    }
}
