package com.example.javamailapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    String senderEmailAddress = "SENDER_EMAIL_ADDRESS";
    String senderEmailPassword = "SENDER_EMAIL_PASSWORD";
    String receiverEmailAddress = "RECEIVER_EMAIL_ADDRESS";
    String emailSubject = "EMAIL_SUBJECT";
    String emailBody = "EMAIL_BODY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void send(View view){

        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmailAddress,senderEmailPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(senderEmailAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmailAddress));
            message.setSubject(emailSubject);
            message.setText(emailBody);
            new SendMail().execute(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private class SendMail extends AsyncTask<Message, String, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = progressDialog.show(MainActivity.this, "please wait",
                    "Sending mail", true, false);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            if(s.equals("success")){
                Context context;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#509324'>success</font>"));
                builder.setMessage("Mail sent successfully");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.show();

            }
            else{
                Toast.makeText(getApplicationContext(),"Something went Wrong", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return  "success";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "error";
            }
        }
    }
}