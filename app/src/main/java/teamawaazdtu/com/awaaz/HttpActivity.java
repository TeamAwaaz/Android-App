package teamawaazdtu.com.awaaz;

import android.preference.PreferenceActivity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by kartik1 on 14-09-2017.
 */

public class HttpActivity {

    public ResponseHolder doGet(String url, Header header){

        HttpClient client = createHttpClient();
        HttpGet get = new HttpGet(url);
        ResponseHolder responseHolder = new ResponseHolder();

        if(header!=null){
            get.addHeader(header);
        }

        try {
            HttpResponse response = client.execute(get);
            responseHolder.responseCode = response.getStatusLine().getStatusCode();
            responseHolder.responseString = getStatusString(responseHolder.responseCode);
            InputStream content = response.getEntity().getContent();
            responseHolder.content = readStream(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseHolder;
    }

    public ResponseHolder doPost(String url, Header header, HttpEntity entity)
    {
        HttpClient client = createHttpClient();
        HttpPost httpPost = new HttpPost(url);// POST to API
        ResponseHolder responseHolder =new ResponseHolder();

        if (header != null)
        {
            httpPost.addHeader(header);
        }

        try
        {
            if(entity!=null)
                httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);

            responseHolder.responseCode = response.getStatusLine().getStatusCode();
            responseHolder.responseString = getStatusString(responseHolder.responseCode);
            InputStream content = response.getEntity().getContent();// Response
            responseHolder.content = readStream(content);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return responseHolder;
    }

    private String readStream(InputStream in) {
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while (read != null)
            {
                sb.append(read);
                read = br.readLine();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String getStatusString(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_OK ? "Success" : "Failed";
    }

    private HttpClient createHttpClient() {

        HttpParams params = new BasicHttpParams();

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params,true);

        SchemeRegistry schReg = new SchemeRegistry();

        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();

        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(),80));
        schReg.register(new Scheme("https", socketFactory, 443));

        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

        return new DefaultHttpClient(conMgr, params);
    }
}
