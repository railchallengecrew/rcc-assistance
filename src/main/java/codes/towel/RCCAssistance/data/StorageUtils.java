package codes.towel.RCCAssistance.data;

import com.mongodb.MongoClient;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class StorageUtils {

    private static StorageMode mode;

    public static void setupDB(MongoClient dbClient) {
        DBUtils.setDbClient(dbClient);
        mode = StorageMode.DB;
    }

    public static void setupLocal(File file) throws IOException {
        LocalStorageUtils.loadData(file);
        mode = StorageMode.LOCAL;
    }

    public static void setObject(String collection, String id, String key, Object object) throws IllegalStateException {
        if (mode==StorageMode.DB) {
            DBUtils.setObject(collection, id, key, object);
        } else {
            try {
                LocalStorageUtils.setObject(collection + "." + id + "." + key, object);
            } catch(IOException ex) {
                Logger.getLogger("StorageUtils").warning("IOException setting object: "+ex.getMessage());
                Logger.getLogger("StorageUtils").warning("Saving to JVM memory instead.");
                LocalStorageUtils.setObjectWithoutSave(collection + "." + id + key, object);
            }
        }
    }

    @Nullable
    public static Object getObject(String collection, String id, String key) throws IllegalStateException {
        if (mode==StorageMode.DB) {
            return DBUtils.getObject(collection, id, key);
        } else {
            return LocalStorageUtils.getObject(collection+"."+id+"."+key);
        }
    }

}
