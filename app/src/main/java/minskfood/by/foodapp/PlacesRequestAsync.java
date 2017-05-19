package minskfood.by.foodapp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class PlacesRequestAsync extends AsyncTask<String, Void, String> {
    private OnPostExecuteListener listener;


    public PlacesRequestAsync(OnPostExecuteListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (listener != null) {
            NetworkInfo networkInfo = listener.getActiveNetworkInfo();
            boolean noConnection = networkInfo == null || !networkInfo.isConnected()
                    || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                    && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE);
            // If no connectivity, cancel network operation and update Callback with null data.
            if (noConnection) {
                listener.onRestPostExecute(null);
                cancel(true);
            }
        }
    }

    @Override
    protected String doInBackground(String... urls) {
        String result = null;
        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                String resultString = downloadFromUrl(url);
                if (resultString != null) {
                    result = resultString;
                } else {
                    throw new IOException("No response received.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) listener.onRestPostExecute(result);
    }

    /**
     * Gets a URL, sets up a connection and gets the HTTP onRestPostExecute body from the server.
     *
     * @param url The url of the server resource
     * @return the onRestPostExecute body in String form if the network request is successful.
     * @throws IOException if cannot close the connection.
     */
    private static String downloadFromUrl(URL url) throws IOException {
        InputStream responseBody = null;
        HttpURLConnection connection = null;
        String result = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000); // Timeout for reading InputStream
            connection.setConnectTimeout(10000); // Timeout for connection.connect()
            connection.setRequestMethod("GET");
            connection.connect(); // Network traffic occurs here
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            responseBody = connection.getInputStream();
            if (responseBody != null) {
                result = convertStreamToString(responseBody);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        } finally {
            // Close Stream and disconnect HTTP connection.
            if (responseBody != null) responseBody.close();
            if (connection != null) connection.disconnect();
        }
        return result;
    }

    private static String convertStreamToString(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }

    public interface OnPostExecuteListener {
        void onRestPostExecute(String response);

        /**
         * Gets the device's active network status as a NetworkInfo object.
         */
        NetworkInfo getActiveNetworkInfo();
    }
}
