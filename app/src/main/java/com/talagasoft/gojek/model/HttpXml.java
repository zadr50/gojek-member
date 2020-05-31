package com.talagasoft.gojek.model;

import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by compaq on 12/06/2016.
 */

public class HttpXml {

    StringBuilder stringBuilder;
    Document mDoc;
    private String _url;
    boolean mSuccess=false;

    private ArrayList<Node> _arNode;

    public HttpXml(String mUrl) {
        _url=mUrl;
        mDoc=GetUrl(_url);
    }
    public HttpXml(){

    }

    public boolean Success(){return mSuccess;}

    public Document GetUrl(String myUrl){
        Log.d("GetUrl",myUrl);
        stringBuilder = GetUrlData(myUrl);
        Document doc = null;
        try {
            doc=loadXMLFromString(String.valueOf(stringBuilder));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return doc;
    }
    public Document ParseData(StringBuilder stringBuilder){
        Document doc = null;
        try {
            doc=loadXMLFromString(String.valueOf(stringBuilder));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public StringBuilder GetUrlData(String myUrl) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            DefaultHttpClient client = new DefaultHttpClient();
            URL url = new URL(myUrl);
            URI website = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
                    url.getPath(), url.getQuery(), url.getRef());

            HttpGet request = new HttpGet();
            request.setURI(website);

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            stringBuilder = new StringBuilder();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
            if(stringBuilder.toString().contains("success")) {
                mSuccess=true;
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //Log.d("HttpXml.GetUrlData()",myUrl + ": " + stringBuilder);
        return stringBuilder;
    }

    public static Document loadXMLFromString(String xml) throws IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputSource is = new InputSource(new StringReader(xml));

        return builder.parse(is);
    }
    public int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    public String getKey(String vKeyName) {
        String vResult="";
        if(mDoc==null && stringBuilder == null) return vResult;

        try {
            if(mDoc==null) {
                mDoc = loadXMLFromString(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        if(mDoc != null){
            NodeList nl1;
            nl1 = mDoc.getElementsByTagName(vKeyName);
            if ( nl1 != null ) {
                Node node1 = nl1.item(0);
                if ( node1 != null ) {
                    vResult = node1.getTextContent();
                } else {
                    //Log.d("getKey","Unknown Node !");
                }
            }
        }
        return vResult;
    }

    public float getKeyFloat(String vKey) {
        String s=getKey(vKey);
        if(s.contentEquals("")){
            return 0;
        }
        return Float.parseFloat(s);
    }

    public void getGroup(String vToken) {
        if(mDoc != null) {
            if(_arNode == null){
                _arNode=new ArrayList<>();
            }
            NodeList nl;
            nl = mDoc.getElementsByTagName(vToken);
            if(nl != null){
                for(int i=0;i<nl.getLength();i++){
                    _arNode.add(nl.item(i));
                }
            }
        }
    }

    public int getCount() {
        if(_arNode!=null) {
            return _arNode.size();
        } else {
            return 0;
        }
    }

    public int getKeyInt(String vKey) {
        String s=getKey(vKey);
        if(s.isEmpty())s="0";
        return Integer.parseInt(s);
    }
    public float getKeyIndexFloat(int i, String vKeyName) {
        String v=""+getKeyIndex(i,vKeyName);
        if(v==""){
            v="0";
        }
        return Float.parseFloat(v);
    }
    public int getKeyIndexInt(int i,String vKeyName){
        return (int) getKeyIndexFloat(i,vKeyName);
    }
    public String getKeyIndex(int position, String vKeyName) {
        String vResult="";
        if(_arNode != null ) {
            NodeList nl = _arNode.get(position).getChildNodes();    //get row index
            for(int i=0;i<nl.getLength();i++){  //field name
                Node nd=nl.item(i);
                String vKey=nd.getNodeName();
                String vValue=nd.getNodeValue();
                if (vKey.contains(vKeyName)) {
                    Node nd2 = nd.getChildNodes().item(0);
                    if (nd2 != null) {
                        vResult = nd2.getTextContent();
                        return vResult;
                    }
                }

            }
            //Log.d("getKeyIndex.vResult",vResult);
        }
        return vResult;
    }
    public static String postData(String url, JSONObject jsonObject){

        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            DefaultHttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            //jsonObject.accumulate("name", person.getName());
            //jsonObject.accumulate("country", person.getCountry());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
