package fin.ex.newsaggregator;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.newsaggregator.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class ArticleDownloadRunnable implements Runnable {
    private static final String TAG = "DownloadRunnable";
    private static final String yourAPIKey ="9f654e82b23c4e10b176475c5f969c2a";
    private static final String apiUrl = "https://newsapi.org/v2/";
    private String source = null;
    private final MainActivity mainActivity;


    public ArticleDownloadRunnable(MainActivity mainActivity, String source) {
        this.mainActivity = mainActivity;
        this.source = source;
    }
    public void run() {
        Uri.Builder buildURL = Uri.parse(apiUrl).buildUpon();

        if (source != null) {
            buildURL.appendPath("top-headlines");
            buildURL.appendQueryParameter("sources", source);
        }
        else {
            buildURL.appendPath("sources");
        }

        buildURL.appendQueryParameter("apiKey", yourAPIKey);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "URL: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                InputStream is = connection.getErrorStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                handleError(sb.toString());
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }

        handleResults(sb.toString());
    }
    public void handleError(String s) {
        String msg = "Error: ";
        try {
            JSONObject jObjMain = new JSONObject(s);
            msg += jObjMain.getString("message");

        } catch (JSONException e) {
            msg += e.getMessage();
        }

        String finalMsg = String.format("%s (%s, %s)", msg, source);
        Log.d(TAG, "handleError: finalMsg");
    }
    public void handleResults(final String jsonString){
        if (source == null){
            final ArrayList<NewsSource> sourceList = parseSource(jsonString);
            mainActivity.runOnUiThread(()-> mainActivity.updateSources(sourceList));
        } else {
            final ArrayList<Article> articleList = parseArticle(jsonString);
            mainActivity.runOnUiThread(()-> mainActivity.updateArticle(articleList));
        }

    }
    private ArrayList<NewsSource> parseSource(String s){
        ArrayList<NewsSource> sourceArrayList = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jSources = jObjMain.getJSONArray("sources");
            for (int i =0; i<jSources.length(); i++){
                JSONObject jSource = (JSONObject) jSources.get(i);
                String id = jSource.getString("id");
                String name = jSource.getString("name");
                String category = jSource.getString("category");
                String language = jSource.getString("language").toUpperCase();
                language = mainActivity.languageHash.get(language);
                String country = jSource.getString("country").toUpperCase();
                country= mainActivity.countryHash.get(country);
                sourceArrayList.add(new NewsSource(id,name,category,language, country));
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return sourceArrayList;

    }

    private ArrayList<Article> parseArticle(String s){
        ArrayList<Article> articleArrayList = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jArticles = jObjMain.getJSONArray("articles");
            for (int i=0; i < jArticles.length(); i++){
                JSONObject jArticle = (JSONObject) jArticles.get(i);
                String author = jArticle.getString("author");
                String title = jArticle.getString("title");
                String description = jArticle.getString("description");
                String url = jArticle.getString("url");
                String imageUrl = jArticle.getString("urlToImage");
                Drawable image = retrieveImgFromWeb(imageUrl);
                String date = jArticle.getString("publishedAt");
                date = date.substring(0,date.length() -1);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date d = format.parse(date);
                date = new SimpleDateFormat("MMM d yyyy, h:mm a", Locale.getDefault()).format(d);

                articleArrayList.add(new Article(title, date, author, image, description, url));

            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return articleArrayList;
    }
    public Drawable retrieveImgFromWeb(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "article image");
            return d;
        } catch (Exception e) {
            return ContextCompat.getDrawable(mainActivity, R.drawable.noimage);
        }
    }

}
