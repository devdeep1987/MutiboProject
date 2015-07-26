package org.springframework.android.basicauth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Devdeep on 1/4/2015.
 */
public class PlayActivity extends Activity {

    private boolean destroyed = false;
    protected static final String TAG = PlayActivity.class.getSimpleName();
    //private MovieSet movieSet;
    private ArrayList<String> movieArray;
    private TextView welcomeText;
    public static TextView highScoreText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity_layout);

        welcomeText = (TextView) findViewById(R.id.welcometext);
        welcomeText.setText("Welcome, "+MainActivity.currentUsername);
        highScoreText = (TextView) findViewById(R.id.highscoretext);
        highScoreText.setText("Your high score is "+MainActivity.currentHighscore);


        final Button playButton = (Button) findViewById(R.id.playbutton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FetchSecuredResourceTask().execute();
                //displayResponse(new Message(0,"Play","Beginning play!"));
            }
        });

        final Button addSetButton = (Button) findViewById(R.id.addsetbutton);
        //if (!MainActivity.currentRole.equals("admin"))
            //addSetButton.setVisibility(View.INVISIBLE);

        addSetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //new TestAdminTask().execute();
                Intent addset = new Intent(PlayActivity.this, AddSetActivity.class);
                startActivity(addset);
            }
        });
    }

    private void displayResponse(Message response) {
        Toast.makeText(this, response.getText(), Toast.LENGTH_LONG).show();
    }

    /*private class TestAdminTask extends AsyncTask<Void, Void, Message> {
        @Override
        protected Message doInBackground(Void... voids) {
            final String url = "http://10.0.2.2:8080/addset";
            DefaultHttpClient httpclient = new DefaultHttpClient();
            //HttpGet request = new HttpGet(url);
            HttpPost request = new HttpPost(url);


            try {
                StringEntity params = new StringEntity("{\"movie1\":\"My Left Foot\",\"movie2\":\"Lincoln\",\"movie3\":\"There Will Be Blood\",\"movie4\":\"Gangs of New York\",\"answer\":\"Gangs of New York\"}");
                params.setContentType("application/json");
                request.setEntity(params);
                HttpResponse response = httpclient.execute(request, MainActivity.staticHttpContext);
                return new Message(0,"AddSet",response.getStatusLine().toString());
            } catch (IOException e) {
                e.printStackTrace();
                return new Message(0, e.getMessage(), e.getLocalizedMessage());
            }
        }

        @Override
        protected void onPostExecute(Message result) {
            displayResponse(result);
        }
    }*/

    private class FetchSecuredResourceTask extends AsyncTask<Void, Void, Message> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Message doInBackground(Void... voids) {
            final String url = "http://10.0.2.2:8080/play";

            // Populate the HTTP Basic Authentitcation header with the username and password
           // HttpAuthentication authHeader = new HttpBasicAuthentication("xx", "yy");
            //HttpHeaders requestHeaders = new HttpHeaders();
            //requestHeaders.setAuthorization(authHeader);
            //requestHeaders.set("Cookie","JSESSIONID="+MainActivity.sid);
            //requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Create a new RestTemplate instance
            //RestTemplate restTemplate = new RestTemplate();
            //restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

            try {
                // Make the network request
                Log.d(TAG, url);
                //ResponseEntity<Message> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(requestHeaders), Message.class);
                //if(response.getBody().getId() == 100)
                    //login_success = true;
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
            displayResponse(result);

                Intent game = new Intent(PlayActivity.this, GameActivity.class);
                game.putExtra("MovieSet",movieArray);

                startActivity(game);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }
}
