package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArticleAdapter articleAdapter;
    private ArrayList<NewsSource> fullSources = new ArrayList<>();
    private ArrayList<Article> articleList = new ArrayList<>();
    private ArrayList<NewsSource> sourcesList = new ArrayList<>();
    private final ArrayList<String> topicsList = new ArrayList<>();
    private final ArrayList<String> languageList = new ArrayList<>();
    private final ArrayList<String> countriesList = new ArrayList<>();
    private FrameLayout frame;
    private String topicfilter= "All";
    private String languagefilter= "All";
    private String countriesfilter= "All";
    protected HashMap <String, String> countryHash;
    protected HashMap<String, String> languageHash;
    private Menu menu;
    private String[] Names;
    private String source = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countryHash = parseCountry();
        languageHash = parseLanguage();
        setContentView(R.layout.activity_main);
        ViewPager2 feedPager = findViewById(R.id.feedPager);
        articleAdapter = new ArticleAdapter(this, articleList);
        feedPager.setAdapter(articleAdapter);


        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener((parent, view, position, id)-> selectItem(position));
        frame = findViewById(R.id.frameLayout);



        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );
        if (getSupportActionBar() != null) {  // <== Important!
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        doArticleDownload();

    }
    private void doArticleDownload(){
        ArticleDownloadRunnable loaderTaskRunnable = new ArticleDownloadRunnable(this,source);
        new Thread(loaderTaskRunnable).start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        if(item.hasSubMenu()){
            return true;
        }
        int parent = item.getGroupId();
        String menuItem = item.getTitle().toString();

        if (parent == 0){
            topicfilter = menuItem;
        }
        if (parent == 1){
            countriesfilter = menuItem;
        }
        if (parent == 2){
            languagefilter = menuItem;
        }
        filterSources();

        return super.onOptionsItemSelected(item);
    }
    public void filterSources(){
        sourcesList.clear();
        for (int i=0;i<fullSources.size();i++){
            NewsSource s = fullSources.get(i);
            if (!s.getCategory().equals(topicfilter) && !topicfilter.equals("All") ){
                continue;
            }
            if(!s.getCountry().equals(countriesfilter) && !countriesfilter.equals("All")){
                continue;
            }
            if(!s.getLanguage().equals(languagefilter) && !languagefilter.equals("All")){
                continue;
            }
            sourcesList.add(s);
        }

        Names = new String [sourcesList.size()];
        for (int i =0; i< sourcesList.size();i++){

            Names[i]= sourcesList.get(i).getName();
        }
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.list_view, Names));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public void makeMenu() {
        topicsList.add("All");
        languageList.add("All");
        countriesList.add("All");
        for (int i=0; i<fullSources.size(); i++){
            String tempCategory = fullSources.get(i).getCategory();
            String tempLanguage= fullSources.get(i).getLanguage();
            String tempCountries= fullSources.get(i).getCountry();

            if(!topicsList.contains(tempCategory)){
                topicsList.add(tempCategory);
            }
            if (!languageList.contains(tempLanguage)){
                languageList.add(tempLanguage);
            }
            if (!countriesList.contains(tempCountries)){
                countriesList.add(tempCountries);
            }

        }
        SubMenu subMenu = menu.addSubMenu("Topics");
        for (int i=0; i<topicsList.size(); i++){
            subMenu.add(0, i,i, topicsList.get(i));
        }
        subMenu = menu.addSubMenu("Countries");
        for (int i=0; i<countriesList.size(); i++){
            subMenu.add(1, i,i, countriesList.get(i));
        }

        subMenu = menu.addSubMenu("Languages");
        for (int i=0; i<languageList.size(); i++){
            subMenu.add(2, i,i, languageList.get(i));
        }

    }
    private void selectItem(int position) {
        source = sourcesList.get(position).getId();
        doArticleDownload();
        mDrawerLayout.closeDrawer(mDrawerList);
        setTitle(sourcesList.get(position).getName());
    }

    public void updateSources(ArrayList<NewsSource> s){
        fullSources = new ArrayList<>(s);
        sourcesList = new ArrayList<>(fullSources);

        Names = new String [fullSources.size()];
        for (int i =0; i< fullSources.size();i++){

            Names[i]= fullSources.get(i).getName();
        }
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.list_view, Names));
        makeMenu();
    }
    public void updateArticle(ArrayList<Article> a){
        articleList = new ArrayList<>(a);
        articleAdapter = new ArticleAdapter(this, articleList);
        ViewPager2 feedPager = findViewById(R.id.feedPager);
        feedPager.setAdapter(articleAdapter);
        frame.setVisibility(View.INVISIBLE);

    }
    private HashMap<String, String> parseCountry() {
        HashMap<String, String> countryHash = new HashMap<>();
        try {
            InputStream is = getResources().openRawResource(R.raw.country_codes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder result = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }

            JSONObject jObjMain = new JSONObject(result.toString());
            JSONArray jCountries = jObjMain.getJSONArray("countries");

            for (int i = 0; i < jCountries.length(); i++) {
                JSONObject jsonObject = jCountries.getJSONObject(i);
                String code = jsonObject.getString("code");
                String name = jsonObject.getString("name");

                countryHash.put(code, name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return countryHash;
    }

   private HashMap<String, String> parseLanguage() {
       HashMap<String, String> languageHash = new HashMap<>();
        try {
            InputStream is = getResources().openRawResource(R.raw.language_codes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder result = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }

            JSONObject jObjMain = new JSONObject(result.toString());
            JSONArray jCountries = jObjMain.getJSONArray("languages");

            for (int i = 0; i < jCountries.length(); i++) {
                JSONObject jsonObject = jCountries.getJSONObject(i);
                String code = jsonObject.getString("code");
                String name = jsonObject.getString("name");

                languageHash.put(code, name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return languageHash;
    }

}