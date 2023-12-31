package planet.components;

import core.Main;
import planet.Planet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;

public class Lighting {

    public enum Star {
        O(new Color(187, 203, 248), new Color(0, 10, 38)),
        B(new Color(201, 213, 250), new Color(0, 6, 23)),
        A(new Color(228, 233, 246), new Color(0, 3, 23)),
        F(Color.WHITE, Color.BLACK),
        G(new Color(253, 248, 243), new Color(16, 8, 0)),
        K(new Color(248, 230, 209), new Color(21, 11, 0)),
        M(new Color(241, 206, 167), new Color(24, 12, 0));

        final Color highlight;
        public final Color shadow;

        Star(Color highlight, Color shadow) {
            this.highlight = highlight;
            this.shadow = shadow;
        }
    }

    private static final float PLANET_RAD = 6.5f;
    private static final int MAX_TIME = 4;

    private final float rate;
    private final PVector planetPos;

    public final Star star;

    private float time;
    private PVector shadowPos;
    private float shadowRad;
    private boolean useOutside;

    public Lighting(float rate) {
        this.rate = rate;

        star = Star.values()[(int) Main.app.random(Star.values().length)];

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

    public PImage getImage(PImage surface, Planet.DayNightStatus dayNightStatus) {
        PImage shadow = Main.app.createImage(16, 16, PConstants.ALPHA);
        shadow.loadPixels();
        surface.loadPixels();

        for (int x = 0; x < shadow.width; x++) {
            for (int y = 0; y < shadow.height; y++) {
                int loc = x + y * shadow.width;

                int planetAlph = surface.pixels[loc] >> 24 & 255;
                boolean visible = planetAlph == 255 && (useOutside ? isOutside(x, y) : isInside(x, y));

                if (dayNightStatus.equals(Planet.DayNightStatus.Night)) visible = planetAlph == 255;
                else if (dayNightStatus.equals(Planet.DayNightStatus.Day)) visible = false;

                if (visible) shadow.pixels[loc] = star.shadow.getRGB();
                else shadow.pixels[loc] = 0x00000000;
            }
        }

        shadow.updatePixels();
        return shadow;
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
