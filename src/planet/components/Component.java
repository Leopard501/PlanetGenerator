package planet.components;

import core.Main;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.awt.*;
import java.util.ArrayList;

import static planet.Planet.IMG_SIZE;

public abstract class Component {

    static final int RANDOM_COLOR_CHANCE = 7;

    public Color low;
    public Color high;
    public PImage sprite;
    public String description;

    public void displayText(float height) {
        Main.app.fill(255);
        Main.app.textSize(20);
        Main.app.text(description, Main.WIDTH / 2f, height);
    }

    public void displayTextToGraphics(float height, PGraphics graphics) {
        graphics.fill(255);
        graphics.textSize(20);
        graphics.text(description, Main.WIDTH / 2f, height);
    }

    PImage createImage(PImage base) {
        PImage img = base.copy();
        img.loadPixels();

        for (int x = 0; x < IMG_SIZE; x++) {
            for (int y = 0; y < IMG_SIZE; y++) {
                int i = x + y * IMG_SIZE;

                img.pixels[i] = mapColor(
                        low, high,
                        new Color(img.pixels[i]).getRed(),
                        img.pixels[i] >> 24 & 255
                ).getRGB();
            }
        }

        img.updatePixels();
        return img;
    }

    public static Color mapColor(Color a, Color b, float map, float alpha) {
        float r = PApplet.map(map, 0, 255, a.getRed(), b.getRed());
        float g = PApplet.map(map, 0, 255, a.getGreen(), b.getGreen());
        float bl = PApplet.map(map, 0, 255, a.getBlue(), b.getBlue());

        r = PApplet.map(r, 0, 255, 0, 1);
        g = PApplet.map(g, 0, 255, 0, 1);
        bl = PApplet.map(bl, 0, 255, 0, 1);
        alpha = PApplet.map(alpha, 0, 255, 0, 1);

        return new Color(r, g, bl, alpha);
    }

    static Color randomColor() {
        return new Color(Main.app.random(1), Main.app.random(1), Main.app.random(1));
    }

    static Color pickColor(Color... colors) {
        return colors[(int) Main.app.random(colors.length)];
    }

    static <E extends Enum<E> & Pickable> E pick(Class<E> picked) {
        ArrayList<Integer> picks = new ArrayList<>();

        E[] values = picked.getEnumConstants();
        for (Pickable value : values) {
            for (int i = 0; i < value.getChance(); i++) {
                picks.add(value.getOrdinal());
            }
        }

        int pickedOrdinal = picks.get((int) Main.app.random(picks.size()));

        return picked.getEnumConstants()[pickedOrdinal];
    }
}
