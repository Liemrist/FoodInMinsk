package minskfood.by.foodapp;

import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class SoapRequest extends AsyncTask<String, Void, String> {
//    private static final String SOAP_ACTION = "http://env-2955146.mycloud.by/wsdl";
//    private static final String NAMESPACE = "http://env-2955146.mycloud.by/";

    private static final String SOAP_ACTION = "http://192.168.0.17:3000/wsdl";
    private static final String NAMESPACE = "http://192.168.0.17:3000/";
    private static final String URL = SOAP_ACTION;

    private String author = "";
    private String review = "";

    public SoapRequest(String author, String review) {
        this.author = author;
        this.review = review;
    }

    public String GetInteger() {
        SoapObject request = new SoapObject(NAMESPACE, "Request");
        request.addProperty("x", author);
        request.addProperty("y", review);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SoapObject result = (SoapObject) envelope.bodyIn;

        if (result == null) {
            Log.d("MYLOG", "null");
            return "null";
        } else {
            Log.d("MYLOG777==", result.toString());
            Log.d("MYLOG777==", result.getProperty("number").toString());
            return result.toString();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        return GetInteger();
    }

    //// TODO: 5/10/2017
//    @Override
//    protected void onPostExecute(String result) {
//        if (result != null && callback != null) {
//            if (result.mException != null) {
//                callback.response(result.mException.getMessage());
//            } else if (result.mResultValue != null) {
//                callback.response(result.mResultValue);
//            }
//        }
//    }

    public interface OnSoapExecuteListener {
        void soapResponse(String response);
    }
}