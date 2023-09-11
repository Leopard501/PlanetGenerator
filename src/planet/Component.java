package planet;

import core.Main;
import processing.core.PImage;

import java.awt.*;
import java.util.ArrayList;

abstract class Component {

    static final int RANDOM_COLOR_CHANCE = 7;

    Color color;
    PImage sprite;
    String description;

    void display() {
        Main.app.tint(color.getRGB());
        Main.app.image(sprite, Main.WIDTH / 2f, Planet.HEIGHT, 160, 160);
    }

    void displayText(float height) {
        Main.app.fill(255);
        Main.app.textSize(20);
        Main.app.text(description, Main.WIDTH / 2f, height);
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
