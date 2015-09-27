package com.example.diego.rebootroot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.DataOutputStream;

public class MyReceiver extends BroadcastReceiver {
    private static String DIEGO="02235776581";
    public MyReceiver() {
    }

    Context contexto;
    EnviarSMS sms;


    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences mispreferencias = context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String IP = mispreferencias.getString("edit_IP", "idirect.dlinkddns.com");
        String Id = mispreferencias.getString("IdRadio", "1");
        int Puerto = Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        ConexionIP ClienteTCP = new ConexionIP(IP, Puerto, " " + Id + " 9");
        ClienteTCP.start();
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    if(phoneNumber.toString().equals(DIEGO)){
                        switch (message) {

                            case "Reboot":
                                sms = new EnviarSMS(context, senderNum, "Reboot Full");
                                sms.sendSMS();
                                Thread.sleep(5000);
                                rebootDevice();
                                break;
                              default:
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }

    private static void rebootDevice() {
        try {
            Process proceso = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(proceso.getOutputStream());
            os.writeBytes("reboot\n");
        } catch (Throwable t) {
            t.printStackTrace();
        }


    }
}



