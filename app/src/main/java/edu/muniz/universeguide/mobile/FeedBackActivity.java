package edu.muniz.universeguide.mobile;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FeedBackActivity extends AppCompatActivity {

    private Integer questionId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionId = getIntent().getIntExtra(Constants.QUESTION_ID,0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_feed_back);


    }

    public void sendFeedback(View view) {
        boolean cancel = false;
        View focusView = null;

        EditText commmentText = (EditText)findViewById(R.id.commentsText);
        EditText emailText = (EditText)findViewById(R.id.emailText);

        emailText.setError(null);
        commmentText.setError(null);

        String email = emailText.getText().toString();

        if (!TextUtils.isEmpty(email) && !isEmailValid(email)) {
            emailText.setError(getString(R.string.error_invalid_email));
            focusView = emailText;
            cancel = true;
        }

        String comments = commmentText.getText().toString();

        if (TextUtils.isEmpty(comments)) {
            commmentText.setError(getString(R.string.msg_no_feedback));
            focusView = commmentText;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        } else {
            new FeedBackTask().execute();
            String mensagem = getString(R.string.feedback_message);
            Toast toast = Toast.makeText(this, mensagem, Toast.LENGTH_SHORT);
            toast.show();
            onBackPressed();
        }
    }

    public void cancel(View view) {
        onBackPressed();
    }

    private class FeedBackTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            try {

                String urlPath = Constants.SERVER +"/feedback";

                EditText editText = (EditText)findViewById(R.id.nameText);
                String name = editText.getText().toString();

                editText = (EditText)findViewById(R.id.emailText);
                String email = editText.getText().toString();

                editText = (EditText)findViewById(R.id.commentsText);
                String comments = editText.getText().toString();

                URL url = new URL(urlPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("id", Integer.toString(questionId));
                jsonParam.put("creator",name);
                jsonParam.put("email",email);
                jsonParam.put("feedback",comments);

                Log.i("JSON", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());

                conn.disconnect();
                return null;
            } catch (Exception e) {
                Log.e(getPackageName(), e.getMessage(), e);
                return null;
            }
        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}

