package org.springframework.android.basicauth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devdeep on 1/30/2015.
 */
public class GameActivity extends Activity {

    protected static final String TAG = GameActivity.class.getSimpleName();
    private RadioGroup movieGroup;
    private Button answerButton;
    private Button nextButton;
    private TextView answerText;
    private TextView scoreText;

    private RadioButton rb1, rb2, rb3, rb4;

    private  ArrayList<String> movieArray;
    private String answer;
    private long currentScore;

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
        scoreText = (TextView) findViewById(R.id.scoretext);
        nextButton = (Button) findViewById(R.id.nextbutton);

        rb1 = (RadioButton) findViewById(R.id.radioButton1);
        rb2 = (RadioButton) findViewById(R.id.radioButton2);
        rb3 = (RadioButton) findViewById(R.id.radioButton3);
        rb4 = (RadioButton) findViewById(R.id.radioButton4);

        rb1.setText(movieArray.get(0));
        rb2.setText(movieArray.get(1));
        rb3.setText(movieArray.get(2));
        rb4.setText(movieArray.get(3));
        rb1.setChecked(true);
        answer = movieArray.get(4);

        nextButton.setEnabled(false);

        currentScore = 0;

        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chosenAnswer="";
                int chosenId = movieGroup.getCheckedRadioButtonId();
                Log.d(TAG, "id:" + chosenId);
                if (chosenId == rb1.getId()) chosenAnswer = rb1.getText().toString();
                else if (chosenId == rb2.getId()) chosenAnswer = rb2.getText().toString();
                else if (chosenId == rb3.getId()) chosenAnswer = rb3.getText().toString();
                else if (chosenId == rb4.getId()) chosenAnswer = rb4.getText().toString();

                if (chosenAnswer.equals(answer)) {
                    answerText.setText("That's the right answer!");
                    currentScore += 10;
                }
                else {
                    answerText.setText("Wrong answer. The correct answer is " + answer);
                    currentScore--;
                }
                scoreText.setText("Score: "+currentScore);

                nextButton.setEnabled(true);
                answerButton.setEnabled(false);
                if (currentScore > Long.parseLong(MainActivity.currentHighscore)) {
                    MainActivity.currentHighscore = new Long(currentScore).toString();

                    PlayActivity.highScoreText.setText("Your high score is "+MainActivity.currentHighscore);
                    new UpdateHighscroreTask().execute();
                }

            }


        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerButton.setEnabled(false);
                new FetchNextQuestionTask().execute();
            }
        });
    }

    private void displayResponse(Message response) {
        Toast.makeText(this, response.getText(), Toast.LENGTH_LONG).show();
    }


    private class UpdateHighscroreTask extends AsyncTask<Void, Void, Message> {

        @Override
        protected Message doInBackground(Void... voids) {
            final String url = "http://10.0.2.2:8080/updatehighscore";
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);
                /*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("username", MainActivity.currentUsername));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));*/
                StringEntity params = new StringEntity("{\"id\":100,\"subject\":\""+MainActivity.currentUsername+"\",\"text\":\""+MainActivity.currentHighscore+"\"}");
                params.setContentType("application/json");
                request.setEntity(params);
                HttpResponse response = httpclient.execute(request, MainActivity.staticHttpContext);
                return new Message(0,"updateHighscore",response.getStatusLine().toString());
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Message(0, e.getStatusText(), e.getLocalizedMessage());
            } catch (ResourceAccessException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Message(0, e.getClass().getSimpleName(), e.getLocalizedMessage());
            }
            catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Message(0, e.getMessage(), e.getLocalizedMessage());
            }

        }
        @Override
        protected void onPostExecute(Message result) {
            displayResponse(result);
        }
    }

    private class FetchNextQuestionTask extends AsyncTask<Void, Void, Message> {

        @Override
        protected Message doInBackground(Void... voids) {
            final String url = "http://10.0.2.2:8080/play";

            try {
                // Make the network request
                Log.d(TAG, url);
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);

                HttpResponse response = httpclient.execute(request, MainActivity.staticHttpContext);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                int count = 0;
                String set[] = new String[5];
                movieArray = new ArrayList<String>();
                for (String line = null; (line = reader.readLine()) != null;) {
                    Log.d(TAG,"Line:"+line+" length:"+line.length());
                    if (line.length() > 1) {
                        int beginoffset = 3, endoffset = 2;
                        if (count == 4)
                            endoffset = 1;
                        set[count] = line.substring(line.indexOf(':')+beginoffset,line.length()-endoffset);
                        movieArray.add(set[count]);
                        Log.d(TAG,"Movie:"+set[count]);
                        count++;
                    }
                }
                //movieSet = new MovieSet(set[0], set[1], set[2], set[3], set[4]);

                //return response.getBody();
                return new Message(0,"Play",response.getStatusLine().toString());
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Message(0, e.getStatusText(), e.getLocalizedMessage());
            } catch (ResourceAccessException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Message(0, e.getClass().getSimpleName(), e.getLocalizedMessage());
            }
            catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Message(0, e.getMessage(), e.getLocalizedMessage());
            }
        }

        @Override
        protected void onPostExecute(Message result) {
            rb1.setText(movieArray.get(0));
            rb2.setText(movieArray.get(1));
            rb3.setText(movieArray.get(2));
            rb4.setText(movieArray.get(3));
            rb1.setChecked(true);
            answer = movieArray.get(4);

            nextButton.setEnabled(false);
            answerButton.setEnabled(true);

        }
    }
}
