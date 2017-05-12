package minskfood.by.foodapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class PlacesRequestAsync extends AsyncTask<String, Integer, PlacesRequestAsync.Result> {
    private OnPostExecuteListener callback;

    public PlacesRequestAsync(Context context) {
        if (context instanceof OnPostExecuteListener) {
            callback = (OnPostExecuteListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostExecuteListener");
        }
    }

    /**
     * Gets a URL, sets up a connection and gets the HTTP onResponse body from the server.
     * @param url The url of the server resource
     * @return the onResponse body in String form if the network request is successful.
     * @throws IOException if cannot close the connection.
     */
    private static String downloadUrl(URL url) throws IOException {
        InputStream responseBody = null;
        HttpURLConnection connection = null;
        String result = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            // Timeout for reading InputStream
            connection.setReadTimeout(10000);
            // Timeout for connection.connect()
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            // Opens communications link (network traffic occurs here)
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            responseBody = connection.getInputStream();
            if (responseBody != null) {
                result = readStream(responseBody);
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

    /**
     * Converts Stream to String.
     * @param stream Stream to convert
     * @return String representation of stream data.
     * @throws IOException if cannot read data.
     */
    private static String readStream(InputStream stream) throws IOException {
        BufferedReader reader;
        StringBuilder builder = new StringBuilder();

        reader = new BufferedReader(new InputStreamReader(stream));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }

    @Override
    protected void onPreExecute() {
        if (callback != null) {
            NetworkInfo networkInfo = callback.getActiveNetworkInfo();
            boolean noConnection = networkInfo == null || !networkInfo.isConnected()
                    || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                    && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE);
            // If no connectivity, cancel network operation and update Callback with null data.
            if (noConnection) {
                callback.onResponse("Response null");
                cancel(true);
            }
        }
    }

    @Override
    protected Result doInBackground(String... urls) {
        Result result = null;
        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                String resultString = downloadUrl(url);
                if (resultString != null) {
                    result = new Result(resultString);
                } else {
                    throw new IOException("No onResponse received.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        if (result != null) {
            if (result.mException != null) {
                callback.onResponse(result.mException.getMessage());
            } else if (result.mResultValue != null) {
                callback.onResponse(result.mResultValue);
            }
        }
    }

    public interface OnPostExecuteListener {
        void onResponse(String response);

        /**
         * Get the device's active network status in the form of a NetworkInfo object.
         */
        NetworkInfo getActiveNetworkInfo();
    }

    /**
     * Wrapper class that serves as a union of a result value and an exception.
     * When the download task has completed, either the result value or exception
     * can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    @SuppressWarnings("WeakerAccess")
    public static class Result {
        public String mResultValue;
        public Exception mException;

        public Result(String resultValue) {
            mResultValue = resultValue;
        }

        public Result(Exception exception) {
            mException = exception;
        }
    }
}
