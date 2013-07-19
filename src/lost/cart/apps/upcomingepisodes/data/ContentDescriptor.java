
package lost.cart.apps.upcomingepisodes.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public class ContentDescriptor {
    public static final String AUTHORITY = "lost.cart.apps.upcomingepisodes.episodes";

    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    private ContentDescriptor() {
    };

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AUTHORITY;

        matcher.addURI(authority, Episode.PATH, Episode.PATH_TOKEN);
        matcher.addURI(authority, Episode.PATH_FOR_ID, Episode.PATH_FOR_ID_TOKEN);

        return matcher;
    }

    public static class Episode {
        public static final String NAME = "episode";

        public static final String PATH = "episodes";

        public static final int PATH_TOKEN = 100;

        public static final String PATH_FOR_ID = "episodes/*";

        public static final int PATH_FOR_ID_TOKEN = 200;

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.upcoming_episodes.episode";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.upcoming_episodes.episode";

        public static class Cols {
            public static final String ID = BaseColumns._ID;

            public static final String TITLE = "episode_title";

            public static final String DESCRIPTION = "episode_description";

            public static final String AIR_DATE = "episode_airdate";

            public static final String IMAGE = "episode_image";
        }

    }
}
