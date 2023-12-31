package planet.components;

import core.Main;

import java.awt.*;

public class Life extends planet.components.Component {

    public enum Type {
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

    public enum Shape {
        None,
        Sparse,
        Global,
        Equatorial,
        Polar
    }

    public Shape shape;
    public Type type;

    // Should be disabled if the planet has weird liquids on it
    public Life(Lighting.Star star, boolean isHabitable) {
        if (isHabitable) shape = Shape.values()[(int) Main.app.random(Shape.values().length)];
        else shape = Shape.None;
        if (Main.app.random(RANDOM_COLOR_CHANCE) < 1) {
            low = randomColor();
        } else {
            type = typeFromStar(star);
            low = type.color;
        }
        high = low;

        createAssets();
    }

    public void createAssets() {
        sprite = createImage(Main.sprites.get("planet_life_" + shape.name()));
        if (shape == Shape.None) description = null;
        else {
            if (type != null) description = shape.name() + " " + type.name();
            else description = shape.name() + " Unknown Life";
        }
    }

    private Type typeFromStar(Lighting.Star star) {
        if (star.equals(Lighting.Star.O) || star.equals(Lighting.Star.B) || star.equals(Lighting.Star.A)) {
            return pickType(Type.RedAlgae, Type.BrownAlgae, Type.GreenAlgae);
        } else if (star.equals(Lighting.Star.F) || star.equals(Lighting.Star.G)) {
            return pickType(Type.ConiferousPlants, Type.DeciduousPlants);
        }
        return pickType(Type.CyanBacteria, Type.PurpleBacteria);
    }

    private Type pickType(Type... types) {
        return types[(int) Main.app.random(types.length)];
    }
}
