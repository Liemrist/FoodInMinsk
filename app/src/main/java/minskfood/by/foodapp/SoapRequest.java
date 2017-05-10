package minskfood.by.foodapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class SoapRequest extends AsyncTask<String, Void, String> {
//    private static final String SOAP_ACTION = "http://env-2955146.mycloud.by/wsdl";
//    private static final String NAMESPACE = "http://env-2955146.mycloud.by/";

    private static final String SOAP_ACTION = "http://192.168.0.108:3000/wsdl";
    private static final String NAMESPACE = "http://192.168.0.108:3000/";
    private static final String URL = SOAP_ACTION;
    private OnSoapExecuteListener callback;
    private String author = "";
    private String review = "";


    public SoapRequest(Context context) {
        if (context instanceof OnSoapExecuteListener) {
            callback = (OnSoapExecuteListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSoapExecuteListener");
        }
    }

    public String GetInteger(String author, String review) {
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
        if (params.length < 2) {
            return null;
        }
        author = params[0];
        review = params[1];
        return GetInteger(author, review);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null && callback != null) {
            callback.soapResponse(result);
        }
        // TODO: 5/10/2017 else if (result == null) handling
    }

    public interface OnSoapExecuteListener {
        void soapResponse(String response);
    }
}