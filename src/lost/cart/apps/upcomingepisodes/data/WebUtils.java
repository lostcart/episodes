
package lost.cart.apps.upcomingepisodes.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.net.http.AndroidHttpClient;

public class WebUtils {

    public static String getData(Context context, String url) {
        return doRequest(context, getURIfromString(url));
    }

    private static URI getURIfromString(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

    /**
     * method for attempting to get data from a webservice null is returned if
     * the service fails
     * 
     * @param context
     * @param uri
     * @return
     */
    private static String doRequest(Context context, URI uri) {

        String result = null;
        AndroidHttpClient client = null;
        try {
            BufferedReader in = null;
            client = AndroidHttpClient.newInstance(null, context);
            HttpGet request = new HttpGet();

            request.setURI(uri);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }
                in.close();
                String page = sb.toString();
                result = page;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (client != null) {
                client.close();
                client = null;
            }
        }
        return result;
    }
}
