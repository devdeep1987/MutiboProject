package org.springframework.android.basicauth;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by Devdeep on 2/28/2015.
 */
public class AddSetActivity extends Activity {
    protected static final String TAG = AddSetActivity.class.getSimpleName();
    private String movie1, movie2, movie3, movie4, answer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addset_activity_layout);

        final Button saveSetButton = (Button)findViewById(R.id.savesetbutton);
        saveSetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.movie1editText);
                movie1 = editText.getText().toString();
                editText = (EditText) findViewById(R.id.movie2editText);
                movie2 = editText.getText().toString();
                editText = (EditText) findViewById(R.id.movie3editText);
                movie3 = editText.getText().toString();
                editText = (EditText) findViewById(R.id.movie4editText);
                movie4 = editText.getText().toString();
                editText = (EditText) findViewById(R.id.answereditText);
                answer = editText.getText().toString();

                if (movie1.trim().isEmpty() || movie2.trim().isEmpty() || movie3.trim().isEmpty() || movie4.trim().isEmpty() || answer.trim().isEmpty())
                    displayMessage("Please fill in all the fields");
                else if (answer.equals(movie1) || answer.equals(movie2) || answer.equals(movie3) || answer.equals(movie4))
                    new AddSetTask().execute();
                else
                    displayMessage("Answer should match one of the fields");



            }
        });
    }

    private void displayMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    }

    private void displayResponse(Message response) {
        Toast.makeText(this, response.getText(), Toast.LENGTH_LONG).show();
    }

    private class AddSetTask extends AsyncTask<Void, Void, Message> {

        @Override
        protected Message doInBackground(Void... voids) {
            final String url = "http://10.0.2.2:8080/addset";
            DefaultHttpClient httpclient = new DefaultHttpClient();
            //HttpGet request = new HttpGet(url);
            HttpPost request = new HttpPost(url);


            try {
                StringEntity params = new StringEntity("{\"movie1\":\""+movie1+"\",\"movie2\":\""+movie2+"\",\"movie3\":\""+movie3+"\",\"movie4\":\""+movie4+"\",\"answer\":\""+answer+"\"}");
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
    }
}
