package minskfood.by.foodapp;

import android.content.Context;
import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Sends soap request on server in separate thread.
 */
public class SoapRequestAsync extends AsyncTask<String, Void, String> {
    private static final String NAMESPACE = "http://env-2955146.mycloud.by/";
    private static final String SOAP_ACTION = "http://env-2955146.mycloud.by/wsdl";
    private static final String URL = SOAP_ACTION;

    private OnPostExecuteListener callback;


    public SoapRequestAsync(Context context) {
        if (context instanceof OnPostExecuteListener) {
            callback = (OnPostExecuteListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostExecuteListener");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length < 3) return null;
        return addReview(params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            callback.onSoapPostExecute(result);
        } else {
            callback.onSoapPostExecute("null");
        }
    }

    /**
     * Adds review to the place in the remote database.
     *
     * @param id     Id of edited place
     * @param author Review author
     * @param text   Review text
     * @return String response of soap service on server (json object) or null
     */
    private String addReview(String id, String author, String text) {
        SoapObject request = new SoapObject(NAMESPACE, "Request");
        request.addProperty("id", id);
        request.addProperty("author", author);
        request.addProperty("review", text);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.call(SOAP_ACTION, envelope);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        SoapObject result = (SoapObject) envelope.bodyIn;

        if (result != null) {
            return result.toString();
        } else {
            return null;
        }
    }

    public interface OnPostExecuteListener {
        void onSoapPostExecute(String response);
    }
}