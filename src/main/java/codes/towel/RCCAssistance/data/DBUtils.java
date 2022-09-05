package codes.towel.RCCAssistance.data;

import com.mongodb.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DBUtils {
    private static MongoClient dbClient;

    /**
     * Set the client to connect to the database. Will overwrite the existing client if set.
     * @param dbClient The MongoClient to use
     * @throws NullPointerException If the dbClient parameter is null
     */
    protected static void setDbClient(@NotNull MongoClient dbClient) throws NullPointerException {
        Objects.requireNonNull(dbClient, "dbClient cannot be null");
        DBUtils.dbClient = dbClient;
    }


    /* GETTERS */
    @Nullable
    public static DBObject getDBObject(@NotNull String collection, String id) throws IllegalStateException {
        if (dbClient == null) throw new IllegalStateException("You must call setDBClient(MongoClient) first");

        DBCollection coll = dbClient.getDB(System.getenv("RCC_DB_NAME")).getCollection(collection);
        DBObject query = new BasicDBObject("_id", id);
        DBCursor cursor = coll.find(query);
        return cursor.one();
    }

    @Nullable
    public static Object getObject(@NotNull String collection, String id, String key) throws IllegalStateException {
        if (dbClient == null) throw new IllegalStateException("You must call setDBClient(MongoClient) first");

        return getDBObject(collection, id).get(key);
    }


    /* SETTERS */
    public static void setDBObject(@NotNull String collection, String id, DBObject object) throws IllegalStateException {
        if (dbClient == null) throw new IllegalStateException("You must call setDBClient(MongoClient) first");

        DBCollection coll = dbClient.getDB(System.getenv(("RCC_DB_NAME"))).getCollection(collection);

        if (getDBObject(collection, id) == null) {
            object.put("_id", id);
            coll.insert(object);
        } else {
            coll.findAndModify(new BasicDBObject("_id", id), object);
        }
    }

    public static void setObject(@NotNull String collection, String id, String key, Object object) throws IllegalStateException {
        if (dbClient == null) throw new IllegalStateException("You must call setDBClient(MongoClient) first");

        DBObject obj = new BasicDBObject("_id", id).append(key, object);
        setDBObject(collection, id, obj);
    }



}
