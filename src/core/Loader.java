package core;

import processing.core.PImage;
//import processing.sound.SoundFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.function.Function;

public class Loader {

    /**
     * Loads all sprites from resources/sprites. Sprites are loaded as PImages, and their names are based on
     * their filepaths. For example, a sprite with path resources/sprites/player/jump.png will be named
     * player_jump.
     * @return a hashmap of sprites
     */
    public static HashMap<String, PImage> loadSprites() {
        Walker<PImage> walker = new Walker<>(
                "sprites",
                ".png",
                (path) -> {
                    PImage image = Main.app.loadImage(path);
                    if (image == null) System.out.println("Could not find " + path);
                    return image;
                });
        return walker.walk();
    }

//    /**
//     * Loads all sprites from resources/sounds. Sounds are loaded as SoundFiles, and their names are based on
//     * their filepaths. For example, a sound with path resources/sounds/player/jump.wav will be named
//     * player_jump.
//     * @return a hashmap of sounds
//     */
//    public static HashMap<String, SoundFile> loadSounds() {
//        Walker<SoundFile> walker = new Walker<>(
//                "sounds",
//                ".wav",
//                (path) -> new SoundFile(core.Main.app, path));
//        return walker.walk();
//    }

    private static class Walker<T> extends SimpleFileVisitor<Path> {
        private static final Path ROOT_PATH = Paths.get("resources").toAbsolutePath();

        private final String folder;
        private final String extension;
        private final Function<String, T> itemLoader;

        private HashMap<String, T> items;

        public Walker(String folder, String extension, Function<String, T> itemLoader) {
            this.folder = folder;
            this.extension = extension;
            this.itemLoader = itemLoader;

            this.items = new HashMap<>();
        }

        public HashMap<String, T> walk() {
            items = new HashMap<>();

            try {
                Files.walkFileTree(
                        ROOT_PATH.resolve(folder),
                        this
                );
            } catch (IOException e) {
                throw new RuntimeException(e.toString());
            }

            return items;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (!file.toString().endsWith(extension)) return FileVisitResult.CONTINUE;

            String path = ROOT_PATH.relativize(file).toString();
            String name = path
                    .substring(folder.length() + 1, path.length() - 4)
                    .replace('\\', '_')
                    .replace('/', '_');

            System.out.println(path + " -> " + name);

            items.put(name, itemLoader.apply(file.toString()));

            return FileVisitResult.CONTINUE;
        }
    }

}
