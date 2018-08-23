package com.example.lenovo.explistdemo;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.lenovo.explistdemo.rec_adapter.RecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private List<Group> groupList;
    private ProgressDialog progressDialog;
    public static final String BASE_URL = "https://jsonplaceholder.typicode.com/posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        groupList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(groupList);
        recyclerView.setAdapter(adapter);
        prepareGroupList();
    }

    private void prepareGroupList() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                Group gs = new Group();
                                gs.setGroupId(jsonObject.optInt("id"));
                                gs.setName(jsonObject.optString("title"));
                                prepareChildList(gs);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }
    private void prepareChildList(final Group group) {
        progressDialog.show();
        final String url=BASE_URL+"/"+group.getGroupId()+"/comments";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            ArrayList<Child> childList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Child child = new Child();
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                child.setChildId(jsonObject.getInt("id"));
                                child.setName(jsonObject.getString("name"));
                                childList.add(child);
                            }
                            group.setItems(childList);
                            groupList.add(group);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String locationAddress;
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

        }
    }
}
