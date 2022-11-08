package com.example.art_blooddonation.Email;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;


import com.example.art_blooddonation.R;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailApi extends AsyncTask<Void, Void ,Void> {

    private Context contexte;
    private Session session;
    private String email;
    private String subject;

    public JavaMailApi(Context contexte, String email, String subject, String message) {
        this.contexte = contexte;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    public JavaMailApi(Context contexte, Session session, String email, String subject, String message) {
        this.contexte = contexte;
        this.session = session;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    private String message;

    ProgressDialog progressDialog;


    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(contexte);
        progressDialog.setMessage("Please wait to send your email ...");
        progressDialog.setTitle("Sending the email to donor ...");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Properties properties = new Properties();
        properties.put("mail.stmp.host","stmp.gmail.com");
        properties.put("mail.stmp.socketFactory.port","465");
        properties.put("mail.stmp.socketFactory.class", "java.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.stmp.port","465");

        session = Session.getDefaultInstance(properties, new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return  new PasswordAuthentication(Util.EMAIL, Util.PASSWORD);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom( new InternetAddress(Util.EMAIL));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email)));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            Transport.send(mimeMessage);
        }catch (MessagingException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
        StartAlertDialog();
        super.onPostExecute(aVoid);
    }

    private void StartAlertDialog() {
        AlertDialog.Builder myDialog =  new AlertDialog.Builder(contexte);
        LayoutInflater inflater = LayoutInflater.from(contexte);
        View myView = inflater.inflate(R.layout.output_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);


        Button closeButton = myView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
