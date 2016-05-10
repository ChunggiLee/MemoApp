package chung.memoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import chung.memoapp.Util.JSONParser;
import chung.memoapp.swipemenulistview.SwipeMenu;
import chung.memoapp.swipemenulistview.SwipeMenuCreator;
import chung.memoapp.swipemenulistview.SwipeMenuItem;
import chung.memoapp.swipemenulistview.SwipeMenuListView;


public class MainActivity extends ActionBarActivity {

    private static String TAG = "MainActivity";

    Button Btngetdata;

    TextView memIdxTxt;
    TextView subjectTxt;
    TextView regTimeTxt;
    TextView contentTxt;
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();

    //URL to get JSON Array
    private static String url = "http://chunggi.net/Memo/bbs/parseList.php/";

    //JSON Node Names
    private static final String TAG_OS = "items";
    private static final String TAG_SUBJECT = "subject";
    private static final String TAG_REG_TIME = "reg_date";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_MEMBER_IDX = "member_idx";

    SwipeMenuListView listView;
    //CustomArrayAdapter adapter;
    SimpleAdapter adapter;

    JSONArray android = null;
    JSONParse jsonParse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffff6064));

        setContentView(R.layout.activity_main);
        listView = (SwipeMenuListView)findViewById(R.id.listView);
       /* final List<String> list = new ArrayList<>();
        list.add("Test 1");
        list.add("Test 2");
        list.add("Test 3");*/
        //adapter = new CustomArrayAdapter(this, R.layout.list_item, list);

        Btngetdata = (Button)findViewById(R.id.getdata);
        Btngetdata.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                oslist.clear();
                new JSONParse().execute(url);

            }
        });


        adapter = new SimpleAdapter(MainActivity.this, oslist,
                R.layout.list_item,
                new String[] { TAG_SUBJECT,TAG_REG_TIME, TAG_CONTENT, TAG_MEMBER_IDX }, new int[] {
                R.id.subjectText,R.id.regTimeText, R.id.contentText, R.id.memIdxText});
        listView.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                //create an action that will be showed on swiping an item in the list
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(new ColorDrawable(Color.DKGRAY));
                // set width of an option (px)
                item1.setWidth(200);
                item1.setTitle("Open");
                item1.setTitleSize(18);
                item1.setTitleColor(Color.WHITE);
                menu.addMenuItem(item1);

                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                item2.setBackground(new ColorDrawable(Color.RED));
                item2.setWidth(200);
                item2.setTitle("Delete");
                item2.setTitleSize(18);
                item2.setTitleColor(Color.WHITE);
                menu.addMenuItem(item2);
            }
        };
        //set MenuCreator
        listView.setMenuCreator(creator);
        // set SwipeListener
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //String value = (String) adapter.getItem(position);
                switch (index) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, OpenMemoActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Open 1 for ", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Delete 2 for ", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.actionButton);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(MainActivity.this, WriteMemoActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            memIdxTxt = (TextView) findViewById(R.id.memIdxText);
            subjectTxt = (TextView)findViewById(R.id.subjectText);
            regTimeTxt = (TextView)findViewById(R.id.regTimeText);
            contentTxt = (TextView)findViewById(R.id.contentText);
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = null;
            try {
                json = jParser.getJSONFromUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_OS);
                for(int i = 0; i < android.length(); i++){
                    JSONObject c = android.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String memIdx = c.getString(TAG_MEMBER_IDX);
                    String subject = c.getString(TAG_SUBJECT);
                    String regTime = c.getString(TAG_REG_TIME);
                    String content = c.getString(TAG_CONTENT);

                    // Adding value HashMap key => value

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_MEMBER_IDX, memIdx);
                    map.put(TAG_SUBJECT, subject);
                    map.put(TAG_REG_TIME, regTime);
                    map.put(TAG_CONTENT, content);

                    Log.d(TAG, memIdx + " , " + subject + " , " + regTime + " , " + content);

                    oslist.add(map);
                    listView=(SwipeMenuListView)findViewById(R.id.listView);

                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Toast.makeText(MainActivity.this, "You Clicked at "+oslist.get(+position).get("name"), Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
