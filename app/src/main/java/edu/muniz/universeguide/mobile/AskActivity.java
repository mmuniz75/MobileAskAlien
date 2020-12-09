package edu.muniz.universeguide.mobile;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONObject;


/**
 * A login screen that offers login via email/password.
 */
public class AskActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isNetworkAvailable()){
            new SetServerTask().execute();
			getSupportActionBar().setDisplayShowHomeEnabled(true);
			getSupportActionBar().setLogo(R.drawable.ic_launcher);
			getSupportActionBar().setDisplayUseLogoEnabled(true);
            setContentView(R.layout.activity_ask);
        }else{
            String mensagem = getString(R.string.msg_no_connection);
            Toast toast = Toast.makeText(this, mensagem, Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

    }

    public void search(View view) {
        EditText questionText = (EditText)findViewById(R.id.askText);
        String question = questionText.getText().toString();

        if(question.length()==0){
            String mensagem = getString(R.string.msg_no_question);
            Toast toast = Toast.makeText(this, mensagem, Toast.LENGTH_SHORT);
            toast.show();
        }else{
            Intent intent = new Intent(this, QuestionListActivity.class);
            intent.putExtra(Constants.ASK, question);
            startActivity(intent);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ask_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SetServerTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            try {
                HttpRequest request = HttpRequest.get(Constants.SERVER_NAME, true);
                String conteudo = request.body();
                JSONObject questionObject = new JSONObject(conteudo);
                String[] server = new String[1];
                Constants.SERVER = questionObject.getString("server");
                //Constants.SERVER = "http://192.168.43.222:9090";
            } catch (Exception e) {
                Log.e(getPackageName(), e.getMessage(), e);
            }
            return null;
        }
    }

}

