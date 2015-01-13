package org.springframework.android.basicauth;


import java.util.Collections;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AbstractAsyncActivity {
	
	private String username;
	private String password;
	private String verify;
	private Message signupmsg;
    private boolean signup_success;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_activity_layout);
		final Button registerButton = (Button) findViewById(R.id.register);
		signupmsg=new Message(0,"","");
        signup_success = false;
		//signupmsg="";
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				EditText editText = (EditText) findViewById(R.id.enteruser);
				username = editText.getText().toString();
				editText = (EditText) findViewById(R.id.enterpass);
				password = editText.getText().toString();
				editText = (EditText) findViewById(R.id.enterverify);
				verify = editText.getText().toString();
				if (username.trim().isEmpty() || password.trim().isEmpty() || verify.trim().isEmpty())
					displayMessage("Please fill all the fields");
				else if (!password.equals(verify))
					displayMessage("Passwords don't match");
				else if (username.indexOf(" ")!=-1) 
					displayMessage("Username cannot contain spaces");
				else {
					//displayMessage("Ready to signup");
					//signupmsg=username+" "+password;
                    signupmsg.setSubject(username);
					signupmsg.setText(password);
					new FetchSecuredResourceTask().execute();
                    Log.d(TAG,"finished task");
                    if(signup_success)
                        finish();
				}
				
			}
		});
	}
	
	private void displayMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		
	}
	
	private class FetchSecuredResourceTask extends AsyncTask<Void, Void, Message> {

		@Override
		protected void onPreExecute() {
			//showLoadingProgressDialog();
			
		}
		
		@Override
		protected Message doInBackground(Void... params) {
			// TODO Auto-generated method stub
			final String url = "http://10.0.2.2:8080/signup";

            //HttpAuthentication authHeader = new HttpBasicAuthentication("roy", "spring");
			HttpHeaders requestHeaders = new HttpHeaders();
            //requestHeaders.setAuthorization(authHeader);
			//requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			requestHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Message> requestEntity = new HttpEntity<Message>(signupmsg, requestHeaders);

			// Create a new RestTemplate instance
			RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
			//restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

			try {
				// Make the network request
				Log.d(TAG, url);
				ResponseEntity<Message> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Message.class);
				//String response = restTemplate.postForObject(url, signupmsg, String.class);
                Log.d(TAG,response.getStatusCode().toString());
                if(response.getBody().getId() == 100)
                    signup_success = true;
				return response.getBody();
				//return new Message(0,"",response);
			} catch (HttpClientErrorException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return new Message(0, e.getStatusText(), e.getLocalizedMessage());
			} catch (ResourceAccessException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return new Message(0, e.getClass().getSimpleName(), e.getLocalizedMessage());
			}
		}
		
		@Override
		protected void onPostExecute(Message result) {
			//dismissProgressDialog();
			displayMessage(result.getText());
            if(signup_success)
                finish();
			
		}
		
	}

}
