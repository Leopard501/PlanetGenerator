package planet;

import core.Main;

import java.awt.*;

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
    Life(Lighting.Star star, boolean isHabitable, long seed) {
        for (long secretSeed : SECRET_SEEDS) {
            if (seed == secretSeed) {
                secretSeeds(seed);
                return;
            }
        }

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
        if (shape == Shape.None) description = "No Life";
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

    private void secretSeeds(long seed) {
        switch ("" + seed) {
            case "666" -> {
                shape = Shape.Polar;
                type = Type.RedAlgae;
                low = type.color;
                high = low;
                description = "Polar Strange Life";
            }
            // "ASCII"
            case "6583677373" -> {
                shape = Shape.None;
                low = Color.BLACK;
                high = low;
                description = "No Physical Life";
            }
            // "EARTH"
            case "6965828472" -> {
                shape = Shape.Global;
                type = Type.DeciduousPlants;
                low = type.color;
                high = low;
                description = "Global Diverse Life";
            }
            // "MARS", "MERCURY", "VENUS", JUPITER, SATURN, MOON, TITAN
            case "77658283", "77698267858289", "8669788583", "74858073846982", "836584858278", "77797978", "8473846578" -> {
                shape = Shape.None;
                low = Color.BLACK;
                high = low;
                description = "No Life";
            }
        }
        sprite = createImage(Main.sprites.get("planet_life_" + shape.name()));
    }
}
