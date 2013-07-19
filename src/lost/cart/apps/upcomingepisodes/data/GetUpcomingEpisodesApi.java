
package lost.cart.apps.upcomingepisodes.data;

import android.os.Bundle;

public class GetUpcomingEpisodesApi {
    private final static String baseUrl = "http://api.trakt.tv/calendar/premieres.json/ed6a7616baa5efbb5179490bfb3599b5";

    /**
     * Method for constructing the request url
     * 
     * @param searchBundle
     * @return
     */
    public static String getUrl(Bundle searchBundle) {
        String url = baseUrl;
        return url;
    }

    public static Episodes parseResponse(String response) {
        Episodes object = null;
        if (response != null && !response.isEmpty()) {
            object = new Episodes(response);
        }
        return object;
    }

}
