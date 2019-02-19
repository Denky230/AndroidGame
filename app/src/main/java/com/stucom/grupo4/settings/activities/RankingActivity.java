package com.stucom.grupo4.settings.activities;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.stucom.grupo4.settings.APIResponse;
import com.stucom.grupo4.settings.MyVolley;
import com.stucom.grupo4.settings.R;
import com.stucom.grupo4.settings.constants.APIData;
import com.stucom.grupo4.settings.model.User;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initRecyclerView();
    }
    @Override protected void onResume() {
        super.onResume();
        getRanking();
    }

    void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    void getRanking() {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String token = prefs.getString("token", "");

        StringRequest request = new StringRequest(
                Request.Method.GET,
                APIData.API_URL + "ranking" + "?token=" + token,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        List<User> ranking = parseAPIResponse(response);
                        UsersAdapter adapter = new UsersAdapter(ranking);
                        recyclerView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        MyVolley.getInstance(this).add(request);
    }
    List<User> parseAPIResponse(String response) {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<APIResponse<List<User>>>() {}.getType();
        APIResponse<List<User>> apiResponse = gson.fromJson(response, typeToken);

        return apiResponse.getData();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        UserViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
    class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {

        private List<User> users;

        UsersAdapter(List<User> users) {
            super();
            this.users = users;
        }

        @NonNull @Override public UserViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.ranking_item, parent, false);
            return new UserViewHolder(view);
        }
        @Override public void onBindViewHolder(@NonNull final UserViewHolder viewHolder, final int position) {
            User user = users.get(position);
            viewHolder.textView.setText(user.getName());
            Picasso.get().load(user.getImage()).into(viewHolder.imageView);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("dky", users.get(viewHolder.getAdapterPosition()).getName());
                }
            });
        }
        @Override public int getItemCount() { return users.size(); }
    }
}