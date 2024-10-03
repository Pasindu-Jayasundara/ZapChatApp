package model;

import java.net.HttpURLConnection;
import java.net.URL;

public class SendSMS {

    public static void send(String sendTo,String textmessage) {
        try {
            String userId="28058";
            String apikey="fMJvr1ev4XFSPXjPEPth";
            String senderId="NotifyDEMO";
            String to= sendTo.replaceFirst("^0", "+94");
            String message=textmessage;
            
            String stringurl = "https://app.notify.lk/api/v1/send?user_id="+userId+"&api_key="+apikey+"&sender_id="+senderId+"&to="+to+"&message="+message;
            
            URL url = new URL(stringurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Success logic
                System.out.println("ok");
            } else {
                System.out.println("SMS Sending Faild");
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

}
