package edu.muniz.universeguide.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;




/**
 * A fragment representing a single Question detail screen.
 * This fragment is either contained in a {@link QuestionListActivity}
 * in two-pane mode (on tablets) or a {@link QuestionDetailActivity}
 * on handsets.
 */
public class QuestionDetailFragment extends Fragment {


    private Integer questionId;
    private QuestionDetailFragment instance;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QuestionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
   }

    private View rootView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.question_detail, container, false);

        new GetQuestionTask().execute();

        return rootView;
    }

    private class GetQuestionTask extends AsyncTask<String, Void, String[]> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Wait");
            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            try {

                String url = "http://" + Constants.SERVER +"/rest/answer/detail";

                String search = getArguments().getString(Constants.QUESTION);
                String id = getArguments().getString(Constants.ANSWER_ID);

                HttpRequest request = HttpRequest.get(url, true, "search", search,"id",id);

                String conteudo = request.body();

                JSONObject jsonObject = new JSONObject(conteudo);

                String[] answer = new String[1];

                JSONObject questionObject = (JSONObject)jsonObject.get("answer");
                String number = questionObject.getString("number");
                String question = questionObject.getString("question");
                String content = questionObject.getString("content");
                questionId = questionObject.getInt("questionId");
                getActivity().getIntent().putExtra(Constants.QUESTION_ID, questionId);

                answer[0] = number + Constants.FIELDS_SPLITER + question + Constants.FIELDS_SPLITER + content;
                return answer;

            } catch (Exception e) {
                Log.e(getActivity().getPackageName(), e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null){
                Map<String, String> answerMap = getAnswer(result[0]);

                Activity activity = instance.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(answerMap.get("question"));
                }

                String formatedContent = Html.fromHtml(answerMap.get("content")).toString();
                ((TextView) rootView.findViewById(R.id.question_detail)).setText(formatedContent);

            }
            dialog.dismiss();
        }

        private Map<String, String> getAnswer(String result) {

            Map<String, String> question = new HashMap<String, String>();;
            StringTokenizer token = new StringTokenizer(result,Constants.FIELDS_SPLITER);
            question.put("number", token.nextToken());
            question.put("question", token.nextToken());
            question.put("content", token.nextToken());

            return question;
        }

    }
}
