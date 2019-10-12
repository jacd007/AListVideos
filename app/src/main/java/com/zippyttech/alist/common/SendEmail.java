package com.zippyttech.alist.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by zippyttech on 18/01/19.
 */

public class SendEmail {

    private static final String TAG = "SendEmail";
    Session session = null;
    //    ProgressDialog pdialog = null;
    Context context = null;
    private String rec, subject, textMessage;

    public SendEmail(Context context) {
        this.context = context;
    }


    public void Email(String Email, String subject, String textMessage) {

        this.rec = Email;
        this.subject = subject;
        this.textMessage = textMessage;

        Log.w(TAG+"-Email","Enviando al correo...");

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("app.zippyttech@gmail.com", "123Zippy!");
            }
        });

//        pdialog = ProgressDialog.show(context, "", "Sending Mail...", true);

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("app.zippyttech@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rec));
                message.setSubject(subject);
                message.setContent(textMessage, "text/html; charset=utf-8");
                Transport.send(message);
            } catch(MessagingException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            String msg = (result!=null)  ? "No se Pudo enviar el correo\"" : "Correo enviado\"";
            Log.w(TAG,"RESPUESTA \""+msg);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}