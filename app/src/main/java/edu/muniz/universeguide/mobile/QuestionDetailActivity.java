package edu.muniz.universeguide.mobile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import android.content.DialogInterface.OnClickListener;

/**
 * An activity representing a single Question detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link QuestionListActivity}.
 */
public class QuestionDetailActivity extends AppCompatActivity {

    private AlertDialog dialog;

    private QuestionDetailActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_question_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        instance = this;

        FloatingActionButton fabFeedBack = (FloatingActionButton) findViewById(R.id.fabFeedBack);
        fabFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer questionId = getIntent().getIntExtra(Constants.QUESTION_ID,0);;
                Intent intent = new Intent(instance, FeedBackActivity.class);
                intent.putExtra(Constants.QUESTION_ID, questionId);
                startActivity(intent);

            }
        });

        FloatingActionButton fabQuestion = (FloatingActionButton) findViewById(R.id.fabQuestion);
        fabQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String questionLabel = getIntent().getStringExtra(Constants.QUESTION);

                AlertDialog.Builder builder = new AlertDialog.Builder(instance);
                builder.setMessage(questionLabel);
                builder.setPositiveButton(getString(R.string.ok),  new OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    });
                dialog = builder.create();
                dialog.show();

            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setLogo(R.drawable.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Constants.QUESTION, getIntent().getStringExtra(Constants.QUESTION));
            arguments.putString(Constants.ANSWER_ID, getIntent().getStringExtra(Constants.ANSWER_ID));

            QuestionDetailFragment fragment = new QuestionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.question_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
