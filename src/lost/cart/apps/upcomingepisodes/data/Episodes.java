
package lost.cart.apps.upcomingepisodes.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class Episodes {

    /**
     * List for storing the parsed episode objects.
     */
    public List<Episode> mEpisodes = new ArrayList<Episode>();

    public Episodes(String input) {
        parseResponse(input);
    }

    /**
     * Takes in a string and attempts to parse it into a list of episodes.
     * 
     * @param input
     */
    private List<Episode> parseResponse(String input) {
        JSONArray main_object;
        try {
            main_object = new JSONArray(input);
            // data is returned in an array of days
            for (int i = 0; i < main_object.length(); i++) {
                JSONObject day = main_object.getJSONObject(i);
                if (day.has("episodes")) {
                    JSONArray episodes = day.getJSONArray("episodes");
                    for (int j = 0; j < episodes.length(); j++) {
                        mEpisodes.add(new Episode(episodes.getJSONObject(j)));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mEpisodes;
    }

    /**
     * Episode class, handles the parsing of a json episode object
     * 
     */
    public final static class Episode {

        public String mTitle;

        public String mDescription;

        public String mAirDate;

        public String mImageUrl;

        public Episode(JSONObject object) {
            parseData(object);
        }

        /**
         * Setup up an episode object from a row from the database assumes the
         * cursor coming in is in the right position
         * 
         * @param cursor
         */
        public Episode(Cursor cursor) {
            if (cursor != null && cursor.moveToFirst()) {
                mTitle = cursor.getString(cursor
                        .getColumnIndex(ContentDescriptor.Episode.Cols.TITLE));
                mDescription = cursor.getString(cursor
                        .getColumnIndex(ContentDescriptor.Episode.Cols.DESCRIPTION));
                mAirDate = cursor.getString(cursor
                        .getColumnIndex(ContentDescriptor.Episode.Cols.AIR_DATE));
                mImageUrl = cursor.getString(cursor
                        .getColumnIndex(ContentDescriptor.Episode.Cols.IMAGE));
            }
        }

        /**
         * Parse the json data for an episode
         * 
         * @param object
         */
        public void parseData(JSONObject object) {
            if (object != null) {
                try {
                    if (object.has("show")) {
                        JSONObject show = object.getJSONObject("show");
                        if (show.has("title")) {
                            mTitle = show.getString("title");
                        }
                        if (show.has("air_day")) {
                            mAirDate = show.getString("air_day");
                        }
                        if (show.has("overview") && !show.getString("overview").isEmpty()) {
                            mDescription = show.getString("overview");
                        }

                    }
                    if (object.has("episode")) {
                        JSONObject episode = object.getJSONObject("episode");
                        // replace old description if there are details about
                        // this particular
                        // episode
                        if (episode.has("overview") && !episode.getString("overview").isEmpty()) {
                            mDescription = episode.getString("overview");
                        }
                        if (episode.has("images")) {
                            JSONObject images = episode.getJSONObject("images");
                            if (images.has("screen")) {
                                mImageUrl = images.getString("screen");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
