
package lost.cart.apps.upcomingepisodes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EpisodeDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "episode_list.db";

    private static final int DATABASE_VERSION = 1;

    public EpisodeDatabase(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ContentDescriptor.Episode.NAME + " ( "
                + ContentDescriptor.Episode.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ContentDescriptor.Episode.Cols.TITLE + " TEXT, "
                + ContentDescriptor.Episode.Cols.DESCRIPTION + " TEXT, "
                + ContentDescriptor.Episode.Cols.AIR_DATE + " TEXT, "
                + ContentDescriptor.Episode.Cols.IMAGE + " TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ContentDescriptor.Episode.NAME);
            onCreate(db);
        }
    }

}
