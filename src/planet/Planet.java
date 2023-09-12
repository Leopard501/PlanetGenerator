package planet;

import core.Main;
import processing.core.PApplet;
import processing.core.PImage;

import java.awt.*;

import static processing.core.PConstants.ARGB;

public class Planet {

    enum Star {
        O(new Color(187, 203, 248), new Color(0, 10, 38)),
        B(new Color(201, 213, 250), new Color(0, 6, 23)),
        A(new Color(228, 233, 246), new Color(0, 3, 23)),
        F(Color.WHITE, Color.BLACK),
        G(new Color(253, 248, 243), new Color(16, 8, 0)),
        K(new Color(248, 230, 209), new Color(21, 11, 0)),
        M(new Color(241, 206, 167), new Color(24, 12, 0));


        final Color highlight;
        final Color shadow;

        Star(Color highlight, Color shadow) {
            this.highlight = highlight;
            this.shadow = shadow;
        }
    }

    static final float HEIGHT = Main.HEIGHT * 0.35f;
    static final int IMG_SIZE = 16;

    public boolean showClouds = true;

    public final int seed;

    private final Star star;
    private final Surface surface;
    private final Liquid liquid;
    private final Gas gas;
    private final Life life;
    private final Ice ice;
    private final Lights lights;
    private final Lighting lighting;

    private PImage shadow;
    private final PImage highlight;

    public Planet(int seed) {
        this.seed = seed;
        Main.app.randomSeed(seed);

        star = Star.values()[(int) Main.app.random(Star.values().length)];
        surface = new Surface();
        liquid = new Liquid();
        gas = new Gas();
        life = new Life(star, isHabitable(liquid));
        ice = new Ice(isCool(liquid));
        lights = new Lights(hasComplexLife(life));
        lighting = new Lighting(
                ice.shape.equals(Ice.Shape.EyeballSheet) ?
                        0 :
                        Main.app.random(-0.01f, 0.01f));

        highlight = Main.sprites.get("planet_lighting_highlight");
    }

    public void update() {
        lighting.update();
    }

    public void display() {
//        surface.display();
//        life.display();
//        liquid.display();
//        ice.display();
//        displayHighlight();
//        if (showClouds) gas.display();
//        displayShadow();
//        lights.display(liquid.sprite, ice.sprite, shadow);
        Main.app.image(createImage(), Main.WIDTH / 2f, HEIGHT, 160, 160);

        displaySeed();
        surface.displayText(HEIGHT + 100);
        liquid.displayText(HEIGHT + 100 + 30);
        gas.displayText(HEIGHT + 100 + 30 * 2);
        ice.displayText(HEIGHT + 100 + 30 * 3);
        life.displayText(HEIGHT + 100 + 30 * 4);
        lights.displayText(HEIGHT + 100 + 30 * 5);
        displayStarType(HEIGHT + 100 + 30 * 6);
    }

    private PImage createImage() {
        PImage img = Main.app.createImage(16, 16, ARGB);
        img.loadPixels();

        shadow = lighting.getImage(surface.sprite, liquid, gas);
        shadow.loadPixels();
        ice.sprite.loadPixels();
        liquid.sprite.loadPixels();
        surface.sprite.loadPixels();
        life.sprite.loadPixels();

        for (int x = 0; x < IMG_SIZE; x++) {
            for (int y = 0; y < IMG_SIZE; y++) {
                int i = x + y * IMG_SIZE;

                /*// In shadow
                if ((shadow.pixels[i] >> 24 & 255) > 0) {
                    // todo: shadow stuff
                    img.pixels[i] = shadow.pixels[i];
                // Not in shadow
                } else {*/
                    // Ice
                    if ((ice.sprite.pixels[i] >> 24 & 255) > 0) {
                        img.pixels[i] = ice.sprite.pixels[i];
                    // Liquid
                    } else if ((liquid.sprite.pixels[i] >> 24 & 255) > 0) {
                        img.pixels[i] = liquid.sprite.pixels[i];
                    // Surface
                    } else {
                        img.pixels[i] = surface.sprite.pixels[i];
                        // Life
                        if ((life.sprite.pixels[i] >> 24 & 255) > 0) {
                            Color lifeColor = new Color(life.sprite.pixels[i], true);
                            // extract alpha
                            int lifeAlpha = lifeColor.getAlpha();
                            lifeColor = new Color(lifeColor.getRGB());
                            // merge with surface
                            img.pixels[i] = Component.mapColor(
                                    new Color(img.pixels[i]), lifeColor,
                                    lifeAlpha, 255).getRGB();
                        }
                    }
//                }
            }
        }

        return img;
    }

    private void displaySeed() {
        Main.app.textSize(32);
        Main.app.fill(Color.YELLOW.getRGB());
        Main.app.text("Planet #" + seed, Main.WIDTH / 2f, HEIGHT - 100);
    }

//    private void displayShadow() {
//        shadow = lighting.getImage(surface.sprite, liquid, gas);
//        Main.app.tint(star.shadow.getRGB());
//        Main.app.image(shadow, Main.WIDTH / 2f, Planet.HEIGHT, 160, 160);
//    }
//
//    private void displayHighlight() {
//        Main.app.tint(star.highlight.getRGB());
//        Main.app.image(highlight, Main.WIDTH / 2f, Planet.HEIGHT, 160, 160);
//    }

    private void displayStarType(float height) {
        Main.app.fill(255);
        Main.app.textSize(20);
        Main.app.text(star.name() + " Type Star", Main.WIDTH / 2f, height);
    }

    private boolean isHabitable(Liquid liquid) {
        if (Main.app.random(5) < 1 || liquid.type == null) return true;

        return !liquid.type.equals(Liquid.Type.MoltenMetal) &&
                !liquid.type.equals(Liquid.Type.MoltenRock) &&
                !liquid.type.equals(Liquid.Type.LiquidMethane) &&
                !liquid.type.equals(Liquid.Type.LiquidNitrogen) &&
                !liquid.type.equals(Liquid.Type.Mercury);
    }

    private boolean isCool(Liquid liquid) {
        if (liquid.type == null) return false;

        return !liquid.type.equals(Liquid.Type.MoltenMetal) &&
                !liquid.type.equals(Liquid.Type.MoltenRock);
    }

    private boolean hasComplexLife(Life life) {
        if (life == null || life.type == null || life.shape.equals(Life.Shape.None)) return false;
        if (Main.app.random(5) < 1) return true;

        return life.type.equals(Life.Type.ConiferousPlants) ||
                life.type.equals(Life.Type.DeciduousPlants);
    }
}
