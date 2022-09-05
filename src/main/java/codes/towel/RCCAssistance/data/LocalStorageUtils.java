package codes.towel.RCCAssistance.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class LocalStorageUtils {
    private static Yaml yaml;
    private static File sfile;
    private static Map<String, Object> data;

    /**
     * Attempts to load data from the given File. If this File does not exist, it will be created.
     * @param file The File to load
     * @throws NullPointerException If the file parameter is null
     * @throws IOException If creating the file throws an IOException
     * @throws SecurityException If creating or reading the file throws a SecurityException
     */
    protected static void loadData(@NotNull File file) throws NullPointerException, IOException, SecurityException {
        Objects.requireNonNull(file, "file cannot be null");

        LocalStorageUtils.sfile = file;
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        try (InputStream in = new FileInputStream(file)) {
            yaml = new Yaml();
            LocalStorageUtils.data = yaml.load(in);
        }

        Logger.getLogger("LocalStorageUtils").info("Loaded file: "+file.getName()+" successfully.");
    }

    /**
     * Saves the data, then reloads the data, returning the old data. LocalStorageUtils.loadData(File) must be called before calling this method.
     * @return The old data
     * @throws IOException If loading from the file throws an IOException
     * @throws SecurityException If loading from the file throws a SecurityException
     * @throws IllegalStateException If data is not loaded (LocalStorageUtils.loadData(File) must be called before calling this method)
     */
    public static Map<String, Object> reloadData() throws IOException, SecurityException, IllegalStateException {
        if (sfile == null || data == null) throw new IllegalStateException("You must call LocalStorageUtils.loadData(File) first");

        saveData();
        return forceReloadData();
    }

    /**
     * Forcibly reloads the data from the data file, overwriting the existing data in memory, returning the old data. LocalStorageUtils.loadData(File) must be called before calling this method.
     * @return The old data
     * @throws IOException If loading from the file throws an IOException
     * @throws SecurityException If loading from the file throws a SecurityException
     * @throws IllegalStateException If data is not loaded (LocalStorageUtils.loadData(File) must be called before calling this method)
     */
    public static Map<String, Object> forceReloadData() throws IOException, SecurityException, IllegalStateException {
        if (sfile == null || data == null) throw new IllegalStateException("You must call LocalStorageUtils.loadData(File) first");

        Map<String, Object> oldData = data;
        loadData(sfile);
        return oldData;
    }

    /**
     * Saves the data to the File provided when LocalStorageUtils.loadData(File) is called. LocalStorageUtils.loadData(File) must be called before calling this method.
     * @throws IOException If saving to the file throws an IOException
     * @throws SecurityException If saving to the file throws an SecurityException
     * @throws IllegalStateException If data is not loaded (LocalStorageUtils.loadData(File) must be called before calling this method)
     */
    public static void saveData() throws IOException, SecurityException, IllegalStateException {
        if (sfile == null || data == null) throw new IllegalStateException("You must call LocalStorageUtils.loadData(File) first");

        try (FileWriter writer = new FileWriter(sfile)) {
            yaml.dump(data, writer);
        }
    }

    /**
     * Saves the data to the File provided. LocalStorageUtils.loadData(File) must be called before calling this method.
     * @param file The File to save to
     * @throws IOException If saving to the file throws an IOException
     * @throws SecurityException If saving to the file throws a SecurityException
     * @throws IllegalStateException If data is not loaded (LocalStorageUtils.loadData(File) must be called before calling this method)
     */
    public static void saveData(File file) throws IOException, SecurityException, IllegalStateException {
        if (data == null) throw new IllegalStateException("You must call LocalStorageUtils.loadData(File) first");

        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(data, writer);
        }
    }

    /**
     * Load an Object from the database. LocalStorageUtils.loadData(File) must be called before calling this method.
     * @param key The key of the Object
     * @return The Object, or null if it is not found
     * @throws IllegalStateException If data is not loaded (LocalStorageUtils.loadData(File) must be called before calling this method)
     */
    @Nullable
    public static Object getObject(String key) throws IllegalStateException {
        if (data == null) throw new IllegalStateException("You must call LocalStorageUtils.loadData(File) first");
        return data.get(key);
    }

    /**
     * Set an Object in the database. LocalStorageUtils.loadData(File) must be called before calling this method.
     * @param key The key of the Object
     * @param value The Object to be stored
     * @throws IllegalStateException If data is not loaded (LocalStorageUtils.loadData(File) must be called before calling this method)
     */
    public static void setObject(String key, Object value) throws IllegalStateException {
        if (data == null) throw new IllegalStateException("You must call LocalStorageUtils.loadData(File) first");
        data.put(key, value);
    }

    
}
