package planet;

import core.Main;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;

class Lighting {

    private static final float PLANET_RAD = 6.5f;
    private static final int MAX_TIME = 4;

    private final float rate;
    private final PVector planetPos;

    private float time;
    private PVector shadowPos;
    private float shadowRad;
    private boolean useOutside;

    Lighting(float rate) {
        this.rate = rate;

        planetPos = new PVector(8, 8);
        time = 3.2f;
    }

    public void update() {
        time += rate;
        if (time >= MAX_TIME) time = 0;
        if (time < 0) time = MAX_TIME;

        float trueTime = (time % 2) - 1;
        shadowRad = PLANET_RAD / trueTime;
        shadowPos = new PVector(-shadowRad + PLANET_RAD * trueTime + planetPos.x, planetPos.y);
        useOutside = time < 1 || time > 3;
    }

    public PImage getImage(PImage surface, Liquid liquid, Gas gas) {
        PImage shadow = Main.app.createImage(16, 16, PConstants.ALPHA);
        shadow.loadPixels();
        surface.loadPixels();

        boolean glowyLiquid = (liquid.type != null && (liquid.type.equals(Liquid.Type.MoltenMetal) || liquid.type.equals(Liquid.Type.MoltenRock))) &&
                !liquid.shape.equals(Liquid.Shape.None);
        if (glowyLiquid) {
            liquid.sprite.loadPixels();
            gas.sprite.loadPixels();
        }

        // todo: turn off clouds in shadow
        for (int x = 0; x < shadow.width; x++) {
            for (int y = 0; y < shadow.height; y++) {
                int loc = x + y * shadow.width;

                int alph = 255;
                int planetAlph = surface.pixels[loc] >> 24 & 255;
                boolean visible = planetAlph == 255 && (useOutside ? isOutside(x, y) : isInside(x, y));
                if (glowyLiquid && (liquid.sprite.pixels[loc] >> 24 & 255) == 255) alph = gas.sprite.pixels[loc] >> 24;
                if (visible) shadow.pixels[loc] = (alph) + 0xFFFFFF00;
                else shadow.pixels[loc] = Main.app.color(0, 0);
            }
        }

        shadow.updatePixels();
        return shadow;
    }

    private boolean isOnPlanet(int x, int y) {
        PVector pos = new PVector(x, y);
        return Math.pow(pos.x - planetPos.x, 2) + Math.pow(pos.y - planetPos.y, 2) <= Math.floor(Math.pow(PLANET_RAD, 2));
    }

    private boolean isOutside(int x, int y) {
        PVector pos = new PVector(x, y);
        return Math.pow(pos.x - shadowPos.x, 2) + Math.pow(pos.y - shadowPos.y, 2) >= Math.pow(shadowRad, 2);
    }

    private boolean isInside(int x, int y) {
        PVector pos = new PVector(x, y);
        return Math.pow(pos.x - shadowPos.x, 2) + Math.pow(pos.y - shadowPos.y, 2) <= Math.pow(shadowRad, 2);
    }
}
