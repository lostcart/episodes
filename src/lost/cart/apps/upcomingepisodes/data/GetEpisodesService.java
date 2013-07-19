
package lost.cart.apps.upcomingepisodes.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

/**
 * Intent service for calling the web service to get the episodes and putting
 * them into the database.
 * 
 * @author luketedman
 * 
 */
public class GetEpisodesService extends IntentService {

    public GetEpisodesService() {
        super("IntentService");
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        getEpisodes();
    }

    /**
     * Method for retrieving parsing and saving the web service data
     */
    private void getEpisodes() {
        String url = GetUpcomingEpisodesApi.getUrl(null);
        /**
         * Try and retrieve the data from the webservice
         */
        String response = WebUtils.getData(this, url);
        /**
         * try and parse the data from the web service
         */
        Episodes episodes = GetUpcomingEpisodesApi.parseResponse(response);
        /**
         * try and save the data
         */
        if (episodes != null && episodes.mEpisodes != null && episodes.mEpisodes.size() > 0) {
            ContentValues[] entries = new ContentValues[episodes.mEpisodes.size()];
            for (int i = 0; i < episodes.mEpisodes.size(); i++) {
                ContentValues entry = new ContentValues();
                entry.put(ContentDescriptor.Episode.Cols.TITLE, episodes.mEpisodes.get(i).mTitle);
                entry.put(ContentDescriptor.Episode.Cols.DESCRIPTION,
                        episodes.mEpisodes.get(i).mDescription);
                entry.put(ContentDescriptor.Episode.Cols.AIR_DATE,
                        episodes.mEpisodes.get(i).mAirDate);
                entry.put(ContentDescriptor.Episode.Cols.IMAGE, episodes.mEpisodes.get(i).mImageUrl);
                entries[i] = entry;
            }

            /**
             * Clear out the old results.
             */
            getContentResolver().delete(ContentDescriptor.Episode.CONTENT_URI, null, null);
            /**
             * add the episode entries to the db
             */
            getContentResolver().bulkInsert(ContentDescriptor.Episode.CONTENT_URI, entries);
        }

    }
}
