package com.talagasoft.gojek.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.talagasoft.gojek.libs.HttpHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import java.util.HashMap;



public class HttpJson extends AsyncTask<Void, Void, Void> {
    boolean mSuccess=false;
    String TAG="HttpJson";
    String mUrl="";
    Context mContext=null;

    public boolean Success(){return mSuccess;}

    public Document GetUrl(String myUrl){
        this.mUrl=myUrl;
        return null;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
    @Override
    protected Void doInBackground(Void... voids) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String url = this.mUrl;
        String jsonStr = sh.makeServiceCall(url);

        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Getting JSON Array node
                JSONArray contacts = jsonObj.getJSONArray("contacts");

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);
                    String id = c.getString("id");
                    String name = c.getString("name");
                    String email = c.getString("email");
                    String address = c.getString("address");
                    String gender = c.getString("gender");

                    // Phone node is JSON Object
                    JSONObject phone = c.getJSONObject("phone");
                    String mobile = phone.getString("mobile");
                    String home = phone.getString("home");
                    String office = phone.getString("office");

                    // tmp hash map for single contact
                    HashMap<String, String> contact = new HashMap<>();

                    // adding each child node to HashMap key => value
                    contact.put("id", id);
                    contact.put("name", name);
                    contact.put("email", email);
                    contact.put("mobile", mobile);

                    // adding contact to contact list
                    //contactList.add(contact);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());

            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");

        }

        return null;
    }
}
