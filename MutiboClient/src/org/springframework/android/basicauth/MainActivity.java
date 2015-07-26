/*
 * Copyright 2010-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.android.basicauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Roy Clarkson
 */
public class MainActivity extends AbstractAsyncActivity {

	protected static final String TAG = MainActivity.class.getSimpleName();
    private boolean login_success;
    public static String sid;
    public static HttpContext staticHttpContext;
    public static String currentUsername;
    public static String currentHighscore;
    public static String currentRole;

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);

		// Initiate the request to the protected service
		final Button submitButton = (Button) findViewById(R.id.submit);
		final Button signupButton = (Button) findViewById(R.id.signup);
        sid="";
        login_success = false;
		submitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new FetchSecuredResourceTask().execute();
			}
		});
		signupButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), "Sign up!", Toast.LENGTH_LONG).show();
				Intent signup = new Intent(MainActivity.this, SignupActivity.class);
				startActivity(signup);
			}
		});
	}
	
	// ***************************************
	// Private methods
	// ***************************************
	private void displayResponse(Message response) {
		Toast.makeText(this, response.getText(), Toast.LENGTH_LONG).show();
	}

	// ***************************************
	// Private classes
	// ***************************************
	private class FetchSecuredResourceTask extends AsyncTask<Void, Void, Message> {

		private String username;

		private String password;

		@Override
		protected void onPreExecute() {
			//showLoadingProgressDialog();

			// build the message object
			EditText editText = (EditText) findViewById(R.id.username);
			this.username = editText.getText().toString();

			editText = (EditText) findViewById(R.id.password);
			this.password = editText.getText().toString();
		}

		@Override
		protected Message doInBackground(Void... params) {
			//final String url = getString(R.string.base_uri) + "/getmessage";
			final String url = "http://10.0.2.2:8080/login";

			// Populate the HTTP Basic Authentitcation header with the username and password
			//HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
			//HttpHeaders requestHeaders = new HttpHeaders();
			//requestHeaders.setAuthorization(authHeader);
			//requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			// Create a new RestTemplate instance
			//RestTemplate restTemplate = new RestTemplate();
			//restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());


			try {				// Make the network request
				Log.d(TAG, url);
                //Log.d(TAG,requestHeaders.toString());
                //Log.d(TAG,authHeader.toString());

				/*ResponseEntity<Message> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(requestHeaders), Message.class);
                String sidparameter = response.getHeaders().toSingleValueMap().values().toArray()[1].toString();
                String sidstr = sidparameter.substring(sidparameter.indexOf('=')+1,sidparameter.indexOf(';'));

                Log.d(TAG,sidstr);
                sid=sidstr;*/

                DefaultHttpClient httpclient = new DefaultHttpClient();
                Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
                httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, defaultcreds);
                CookieStore cookieStore = new BasicCookieStore();
                HttpContext localContext = new BasicHttpContext();
                localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
                staticHttpContext = localContext;
                HttpGet request = new HttpGet(url);
                HttpResponse response = httpclient.execute(request, localContext);
                Log.d(TAG,response.getStatusLine().toString());
                Log.d(TAG,localContext.toString());

                //if(response.getBody().getId() == 100)
                login_success = false;
                if(response.getStatusLine().getStatusCode()==200)
                    login_success = true;

                if (login_success) {
                    MainActivity.currentUsername = username;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    int count = 0;
                    for (String line = null; (line = reader.readLine()) != null;) {
                        Log.d(TAG, "Line:" + line + " length:" + line.length());
                        if (count == 2) {
                            MainActivity.currentRole = line.substring(line.indexOf(":")+3,line.length()-2);
                            Log.d(TAG,"role:"+MainActivity.currentRole);
                        }
                        else if (count == 3) {
                            MainActivity.currentHighscore = line.substring(line.indexOf(':')+3,line.length()-1);
                            Log.d(TAG,"score:"+MainActivity.currentHighscore);
                        }
                        count++;
                    }
                }

                return new Message(0,"Login",response.getStatusLine().toString());
			} catch (HttpClientErrorException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return new Message(0, e.getStatusText(), e.getLocalizedMessage());
			} catch (ResourceAccessException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return new Message(0, e.getClass().getSimpleName(), e.getLocalizedMessage());
			}
              catch (ClientProtocolException e) {
                  Log.e(TAG, e.getLocalizedMessage(), e);
                  return new Message(0, e.getMessage(), e.getLocalizedMessage());
              }
              catch (IOException e) {
                  Log.e(TAG, e.getLocalizedMessage(), e);
                  return new Message(0, e.getMessage(), e.getLocalizedMessage());
              }
		}

		@Override
		protected void onPostExecute(Message result) {
			//dismissProgressDialog();
			displayResponse(result);
            if(login_success) {
                Intent play = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(play);
            }
		}

	}
	
}
