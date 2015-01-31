package org.springframework.android.basicauth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Created by Devdeep on 1/30/2015.
 */
public class GameActivity extends Activity {

    protected static final String TAG = PlayActivity.class.getSimpleName();
    private RadioGroup movieGroup;
    private Button answerButton;
    private Button nextButton;
    private TextView answerText;

    private RadioButton rb1, rb2, rb3, rb4;

    private  ArrayList<String> movieArray;
    private String answer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_layout);
        Bundle data = getIntent().getExtras();
        movieArray = data.getStringArrayList("MovieSet");
        Log.d(TAG, "game movie1:" + movieArray.get(0));
        movieGroup = (RadioGroup) findViewById(R.id.moviegroup);
        answerButton = (Button) findViewById(R.id.answerbutton);
        answerText = (TextView) findViewById(R.id.correcttext);
        nextButton = (Button) findViewById(R.id.nextbutton);

        rb1 = (RadioButton) findViewById(R.id.radioButton1);
        rb2 = (RadioButton) findViewById(R.id.radioButton2);
        rb3 = (RadioButton) findViewById(R.id.radioButton3);
        rb4 = (RadioButton) findViewById(R.id.radioButton4);

        rb1.setText(movieArray.get(0));
        rb2.setText(movieArray.get(1));
        rb3.setText(movieArray.get(2));
        rb4.setText(movieArray.get(3));
        answer = movieArray.get(4);

        nextButton.setEnabled(false);

        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chosenAnswer;
                int chosenId = movieGroup.getCheckedRadioButtonId();
                Log.d(TAG,"id:"+chosenId);
                if(chosenId == rb1.getId()) chosenAnswer = rb1.getText().toString();
                else if(chosenId == rb2.getId()) chosenAnswer = rb2.getText().toString();
                else if(chosenId == rb3.getId()) chosenAnswer = rb3.getText().toString();
                else chosenAnswer = rb4.getText().toString();

                if (chosenAnswer.equals(answer))
                    answerText.setText("That's the right answer!");
                else
                    answerText.setText("Wrong answer. The correct answer is "+answer);

                nextButton.setEnabled(true);

            }


        });
    }
}
