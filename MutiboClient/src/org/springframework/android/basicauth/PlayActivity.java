package org.springframework.android.basicauth;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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

import java.io.IOException;
import java.util.Collections;

/**
 * Created by Devdeep on 1/4/2015.
 */
public class PlayActivity extends Activity {

    private boolean destroyed = false;
    protected static final String TAG = PlayActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity_layout);

        final Button playButton = (Button) findViewById(R.id.playbutton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FetchSecuredResourceTask().execute();
                //displayResponse(new Message(0,"Play","Beginning play!"));
            }
        });
    }

    private void displayResponse(Message response) {
        Toast.makeText(this, response.getText(), Toast.LENGTH_LONG).show();
    }

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

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }
}
