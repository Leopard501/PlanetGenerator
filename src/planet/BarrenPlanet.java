package planet;

import core.Main;

public class BarrenPlanet extends Planet {

    public BarrenPlanet(int seed) {
        super(seed);

        liquid.shape = Liquid.Shape.None;
        Gas.Shape[] possibleGasses = {Gas.Shape.None, Gas.Shape.Trace};
        gas.shape = possibleGasses[(int) Main.app.random(possibleGasses.length)];
        if (Main.app.random(3) < 2) ice.shape = Ice.Shape.None;
        life.shape = Life.Shape.None;
        boolean hasProbes = Main.app.random(3) < 1;
        if (hasProbes) {
            lights.shape = Lights.Shape.Campfires;
            lights.type = Lights.Type.LED;
            lights.low = Lights.Type.LED.color.get();
            lights.high = lights.low;
        } else lights.shape = Lights.Shape.None;

        liquid.createAssets();
        gas.createAssets();
        ice.createAssets();
        life.createAssets();
        lights.createAssets();
        if (hasProbes) lights.description = "Sparse Probes";
    }
}
