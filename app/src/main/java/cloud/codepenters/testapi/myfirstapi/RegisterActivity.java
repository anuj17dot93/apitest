package cloud.codepenters.testapi.myfirstapi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText registerUsername, registerEmail, registerPassword;
    private TextView goToLogin;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupUIViews();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    //register the user
                    new sendDataToServer().execute();
                }
            }
        });
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }
    private void setupUIViews(){
        registerUsername=(EditText) findViewById(R.id.registerUsername);
        registerEmail=(EditText) findViewById(R.id.registerEmail);
        registerPassword=(EditText) findViewById(R.id.registerPassword);
        goToLogin=(TextView) findViewById(R.id.goToLogin);
        registerButton=(Button) findViewById(R.id.registerButton);
    }

    private Boolean validate(){
        Boolean result=false;

        String name=registerUsername.getText().toString();
        String email=registerEmail.getText().toString();
        String password=registerPassword.getText().toString();

        if(name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Bhai sab bharle pehle", Toast.LENGTH_SHORT).show();
        } else{
            result=true;
        }
        return result;
    }

    private String formatDataAsJSON() {
        String name=registerUsername.getText().toString();
        String email=registerEmail.getText().toString();
        String password=registerPassword.getText().toString();

        final JSONObject root= new JSONObject();
        try {
            root.put("name", name);
            root.put("email", email);
            root.put("password", password);
            root.put("password_confirmation", password);

            return root.toString();
        }
        catch (JSONException e1){
//            e1.printStackTrace();
            Log.d(TAG, "formatDataAsJSON: Can't format the data to JSON");
        }
        return null;
    }

    class sendDataToServer extends AsyncTask<Void,Void,String>{
        final String jsondata=formatDataAsJSON();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(RegisterActivity.this, "Thehro!!!!...", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(Void... params) {
//
            return getServerResponse(jsondata);
//            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(RegisterActivity.this, "Ab karle jo krna h..."+result, Toast.LENGTH_SHORT).show();
            TextView textview= (TextView) findViewById(R.id.textView);
            textview.setText(result);
        }


    }

/*    private void sendDataToServer (){
        final String jsondata=formatDataAsJSON();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return getServerResponse(jsondata);
            }
            @Override
            protected void onPostExecute(String result) {
                TextView textview= (TextView) findViewById(R.id.textView);
                textview.setText(result);
            }
        }.execute();

    }*/
    private String getServerResponse(String jsondata){
        HttpURLConnection connection;
//        TextView textview= (TextView) findViewById(R.id.textView);
//        textview.setText(jsondata);
        try {
            TextView textview= (TextView) findViewById(R.id.textView);
            URL url= new URL("http://testapi.codepenters.cloud/api/register");
            connection =(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput (true);
            connection.setDoOutput (true);
            connection.setUseCaches (false);
            connection.setRequestProperty("Content-Type","application/json");

            connection.connect();

            DataOutputStream dStream=new DataOutputStream(connection.getOutputStream());

            dStream.writeBytes(jsondata);
            dStream.flush();
            dStream.close();

            int responseCode=connection.getResponseCode();
//            textview.setText(responseCode);
            if(responseCode==connection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer response = new StringBuffer();

                String line;

                //READ LINE BY LINE
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                //RELEASE RES
                br.close();

                return response.toString();
            }
            else{

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
