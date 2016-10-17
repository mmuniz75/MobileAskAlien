package edu.muniz.universeguide.mobile;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A login screen that offers login via email/password.
 */
public class AskActivity extends AppCompatActivity  {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isNetworkAvailable()){
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

}

