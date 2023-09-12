package planet;

import core.Main;
import processing.core.PImage;

import java.awt.*;

import static planet.Planet.IMG_SIZE;

class Life extends Component {

    enum Type {
        GreenAlgae(new Color(149, 162, 41)),
        BrownAlgae(new Color(190, 139, 35)),
        RedAlgae(new Color(206, 41, 21)),
        CyanBacteria(new Color(120, 180, 120)),
        PurpleBacteria(new Color(201, 40, 112)),
        ConiferousPlants(new Color(80, 125, 70)),
        DeciduousPlants(new Color(145, 211, 33));

        final Color color;

        Type(Color color) {
            this.color = color;
        }
    }

    enum Shape {
        None,
        Sparse,
        Global,
        Equatorial,
        Polar
    }

    Shape shape;
    Type type;

    // Should be disabled if the planet has weird liquids on it
    Life(Planet.Star star, boolean isHabitable) {
        if (isHabitable) shape = Shape.values()[(int) Main.app.random(Shape.values().length)];
        else shape = Shape.None;
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
        } else {
            type = typeFromStar(star);
            low = type.color;
        }
        high = low;

        sprite = createImage(Main.sprites.get("planet_life_" + shape.name()));
        if (shape == Shape.None) description = "No life";
        else {
            if (type != null) description = shape.name() + " " + type.name();
            else description = shape.name() + " Unknown Life";
        }
    }

    private Type typeFromStar(Planet.Star star) {
        if (star.equals(Planet.Star.O) || star.equals(Planet.Star.B) || star.equals(Planet.Star.A)) {
            return pickType(Type.RedAlgae, Type.BrownAlgae, Type.GreenAlgae);
        } else if (star.equals(Planet.Star.F) || star.equals(Planet.Star.G)) {
            return pickType(Type.ConiferousPlants, Type.DeciduousPlants);
        }
        return pickType(Type.CyanBacteria, Type.PurpleBacteria);
    }

    private Type pickType(Type... types) {
        return types[(int) Main.app.random(types.length)];
    }
}
