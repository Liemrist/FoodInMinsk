package minskfood.by.foodapp;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class CustomSoap extends AppCompatActivity {
    /*
    private static String SOAP_ACTION = "http://ad.corteli.com/soap/smsservice.php";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "getMessage";
    private static String URL = "http://ad.corteli.com/soap/smsservice.wsdl.php";
    */

    private static final String SOAP_ACTION = "http://env-2955146.mycloud.by/wsdl";
    private static final String NAMESPACE = "http://env-2955146.mycloud.by/";
    private static final String URL = SOAP_ACTION;

    public String GetInteger() {
        SoapObject request = new SoapObject(NAMESPACE, "Request");
        request.addProperty("x", "200");
        request.addProperty("y", "150");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        Log.d("MYLOG12", "Start");

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
            //Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();
        }
        //Log.d("MYLOG", "END");
        //return 1;
    }
}