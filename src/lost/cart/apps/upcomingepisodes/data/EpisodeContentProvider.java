
package lost.cart.apps.upcomingepisodes.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class EpisodeContentProvider extends ContentProvider {
    private EpisodeDatabase mEpisodeDb;

    @Override
    public boolean onCreate() {
        Context ctx = getContext();
        mEpisodeDb = new EpisodeDatabase(ctx);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = ContentDescriptor.URI_MATCHER.match(uri);
        switch (match) {
            case ContentDescriptor.Episode.PATH_TOKEN:
                return ContentDescriptor.Episode.CONTENT_TYPE_DIR;
            case ContentDescriptor.Episode.PATH_FOR_ID_TOKEN:
                return ContentDescriptor.Episode.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mEpisodeDb.getWritableDatabase();
        int token = ContentDescriptor.URI_MATCHER.match(uri);
        switch (token) {
            case ContentDescriptor.Episode.PATH_TOKEN: {
                long id = db.insert(ContentDescriptor.Episode.NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentDescriptor.Episode.CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(id)).build();
            }
            default: {
                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
            }
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues values[]) {
        int numInserted = 0;
        SQLiteDatabase db = mEpisodeDb.getWritableDatabase();
        int token = ContentDescriptor.URI_MATCHER.match(uri);
        switch (token) {
            case ContentDescriptor.Episode.PATH_TOKEN: {
                db.beginTransaction();
                for (ContentValues cv : values) {
                    insert(uri, cv);
                    numInserted++;
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return numInserted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteDatabase db = mEpisodeDb.getReadableDatabase();
        final int match = ContentDescriptor.URI_MATCHER.match(uri);
        switch (match) {
        // retrieve episode list
            case ContentDescriptor.Episode.PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ContentDescriptor.Episode.NAME);

                Cursor c = builder.query(db, projection, selection, selectionArgs, null, null,
                        sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            }
            default:
                return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int count = 0;
        SQLiteDatabase db = mEpisodeDb.getWritableDatabase();

        final int match = ContentDescriptor.URI_MATCHER.match(uri);
        switch (match) {
            case ContentDescriptor.Episode.PATH_TOKEN:
                count = db.delete(ContentDescriptor.Episode.NAME, where, whereArgs);
                break;
        }
        return count;
    }
}
