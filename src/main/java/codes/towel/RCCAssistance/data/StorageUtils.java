package codes.towel.RCCAssistance.data;

import com.mongodb.MongoClient;

import java.io.File;
import java.io.IOException;

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
            LocalStorageUtils.setObject(collection+"."+id+"."+key, object);
        }
    }

    public static void getObject(String collection, String id, String key) throws IllegalStateException {
        if (mode==StorageMode.DB) {
            DBUtils.getObject(collection, id, key);
        } else {
            LocalStorageUtils.getObject(collection+"."+id+"."+key);
        }
    }

}
