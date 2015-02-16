package com.example.darksync;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    }
    
    public void sendMessage(View view){
    	Log.i("DarkSync", "Send Message called");
    	EditText hostip = (EditText) findViewById(R.id.hostip);
    	Log.i("DarkSync", "hostip" + hostip);
    	EditText eport = (EditText) findViewById(R.id.port);
    	String ip = hostip.getText().toString();
    	String port = eport.getText().toString();
    	Log.i("DarkSync", "Found ip, port" + ip + ":"+ port);
    	EditText e2 = (EditText) findViewById(R.id.hostname);
    	e2.setText(ip + ":" + port);
    	new NetworkTask().execute();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public static class NetworkManager {
    	public static String rpc_request(String hostname, String methodname, String params) throws IOException {
        	HttpClient httpclient = new DefaultHttpClient();
        	methodname = "/api/" + methodname.replace('.', '/');
        	String url = hostname + methodname;
            HttpPost httppost = new HttpPost(url);
            JSONArray jsonArr = new JSONArray();
            jsonArr.put(params);
            String p = jsonArr.toString();
            Log.i("DarkSync", "Fetching " + url + "," + p);
            //passes the results to a string builder/entity
            StringEntity se = new StringEntity(p);
	        httppost.setEntity(se);
            //sets the post request as the resulting string

            HttpResponse response = httpclient.execute(httppost);
			
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();
                
                return responseString;
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
    	}
    }
    	
	public class NetworkTask extends AsyncTask<String, Void, String> {
	    public String doInBackground(String... urls) {
	    	try {
	        	EditText hostip = (EditText) findViewById(R.id.hostip);
	        	Log.i("DarkSync", "hostip" + hostip);
	        	EditText eport = (EditText) findViewById(R.id.port);
	        	String ip = hostip.getText().toString();
	        	String port = eport.getText().toString();
	        	Log.i("DarkSync", "Found ip, port" + ip + ":"+ port);
	        	EditText e2 = (EditText) findViewById(R.id.hostname);
	        	e2.setText(ip + ":" + port);
	        	String hostname = "http://"+ ip + ":" + port;
	        	String dname = ((EditText) findViewById(R.id.name)).getText().toString();
	            //Your code goes here
				NetworkManager.rpc_request(hostname, "v1_connect", dname);
				String res = NetworkManager.rpc_request(hostname, "v1_get_data", dname);
				return res;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Failure:" + e.toString();
	        }
	    }

	    public void onPostExecute(String response) {
	    	EditText res = (EditText) findViewById(R.id.response);
	    	res.setText(response);
	    }
	}
}
