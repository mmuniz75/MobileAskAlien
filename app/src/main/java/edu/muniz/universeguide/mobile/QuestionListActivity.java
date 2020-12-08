package edu.muniz.universeguide.mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * An activity representing a list of Questions. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link QuestionDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class QuestionListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private QuestionListActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_question_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.question_detail_container) != null) {
            mTwoPane = true;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setLogo(R.drawable.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
        }

        new SearchTask().execute();

    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Map<String, String>> mValues;

        public SimpleItemRecyclerViewAdapter(List<Map<String, String>> questions) {
            mValues = questions;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.question_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
			
			if (position % 2 == 1) {
				holder.mView.setBackgroundColor(Color.WHITE);
			} else {
				holder.mView.setBackgroundColor(Color.parseColor("#cce4ff"));
			}
			
            Map question = mValues.get(position);
            holder.mItem = question;
            holder.mIdView.setText(question.get("number").toString());
            holder.mContentView.setText(question.get("question").toString());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ask = getIntent().getStringExtra(Constants.ASK)  + " (*)";
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(Constants.ASK, ask);
                        arguments.putString(Constants.QUESTION, holder.mItem.get("question").toString());
                        arguments.putString(Constants.ANSWER_ID, holder.mItem.get("number").toString());

                        QuestionDetailFragment fragment = new QuestionDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.question_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, QuestionDetailActivity.class);
                        intent.putExtra(Constants.ASK, ask);
                        intent.putExtra(Constants.QUESTION, holder.mItem.get("question").toString());
                        intent.putExtra(Constants.ANSWER_ID, holder.mItem.get("number").toString());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Map mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    private class SearchTask extends AsyncTask<String, Void, String[]> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(QuestionListActivity.this);
            dialog.setMessage(getString(R.string.searching));
            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            try {

                String ask = getIntent().getStringExtra(Constants.ASK);
                String url = "http://" + Constants.SERVER +"/ask";

                HttpRequest request = HttpRequest.get(url, true, "question", ask);

                String conteudo = request.body();

                JSONArray resultados =  new JSONArray(conteudo);

                String[] questions = new String[resultados.length()];

                for (int i = 0; i < resultados.length(); i++) {
                    JSONObject questionObject = resultados.getJSONObject(i);
                    String number = questionObject.getString("number");
                    String question = questionObject.getString("question");
                    questions[i] = number + ";" + question;
                }

                return questions;

            } catch (Exception e) {
                Log.e(getPackageName(), e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            try{
                if(result != null && result.length > 0){
                    List<Map<String, String>> listQuestions = listQuestions(result);
                    RecyclerView recyclerView = (RecyclerView)findViewById(R.id.question_list);
                    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(listQuestions));
                }else{
                    String mensagem = getString(R.string.msg_no_results);
                    Toast toast = Toast.makeText(instance, mensagem, Toast.LENGTH_SHORT);
                    toast.show();
                    onBackPressed();
                }
            }finally{
                dialog.dismiss();
            }

        }

        private List<Map<String, String>> listQuestions(String[] results) {
            List<Map<String, String>> questions = new ArrayList<Map<String, String>>();
            Map<String, String> question = null;

            for(String resultValues : results){
                StringTokenizer token = new StringTokenizer(resultValues,";");
                question = new HashMap<String, String>();
                question.put("number", token.nextToken());
                question.put("question", token.nextToken());
                questions.add(question);
            }

            return questions;
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

}
