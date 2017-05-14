package minskfood.by.foodapp;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import minskfood.by.foodapp.models.place.Review;

/**
 * Sends soap request on server in separate thread.
 */
public class SoapRequestAsync extends AsyncTask<String, Void, Review> {
    private static final String NAMESPACE = "http://krabsburger.mycloud.by/";
    private static final String SOAP_ACTION = "http://krabsburger.mycloud.by/wsdl";
    private static final String URL = SOAP_ACTION;

    private OnPostExecuteListener listener;


    public SoapRequestAsync(OnPostExecuteListener listener) {
        this.listener = listener;
    }

    @Override
    protected Review doInBackground(String... params) {
        if (params.length < 3) {
            return null;
        }
        return addReview(params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(Review result) {
        if (listener != null) listener.onSoapPostExecute(result);
    }

    /**
     * Adds review to the place in the remote database.
     *
     * @param placeId Id of edited place
     * @param author  Review author
     * @param text    Review text
     * @return String response of soap service on server (json object) or null
     */
    private Review addReview(String placeId, String author, String text) {
        SoapObject request = new SoapObject(NAMESPACE, "Request");
        request.addProperty("id", placeId);
        request.addProperty("author", author);
        request.addProperty("text", text);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.call(SOAP_ACTION, envelope);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        SoapObject result = (SoapObject) envelope.bodyIn;

        if (result != null && result.getPropertyCount() == 2) {
            return new Review(result.getPropertyAsString("author"),
                    result.getPropertyAsString("text"));
        } else {
            return null;
        }
    }

    public interface OnPostExecuteListener {
        void onSoapPostExecute(Review response);
    }
}
