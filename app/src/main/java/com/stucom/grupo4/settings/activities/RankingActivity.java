package com.stucom.grupo4.settings.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.stucom.grupo4.settings.persistence.SharedPrefsData;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initRecyclerView();
    }
    @Override protected void onResume() {
        super.onResume();
        rankingRequest();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void rankingRequest() {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String token = prefs.getString(SharedPrefsData.Keys.TOKEN.name(), "");

        String requestURL = APIData.API_URL + "ranking" + "?token=" + token;
        StringRequest request = new StringRequest(
                Request.Method.GET,
                requestURL,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        // Store Users ranking
                        List<User> ranking = parseAPIResponse(response);
                        sortRankingByTotalScoreDesc(ranking);

                        // Set UsersAdapter with Users ranking to recyclerView
                        UsersAdapter adapter = new UsersAdapter(ranking);
                        recyclerView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {}
                }
        );
        MyVolley.getInstance(this).add(request);
    }
    private List<User> parseAPIResponse(String response) {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<APIResponse<List<User>>>() {}.getType();
        APIResponse<List<User>> apiResponse = gson.fromJson(response, typeToken);

        return apiResponse.getData();
    }
    private @TargetApi(24) void sortRankingByTotalScoreDesc(List<User> ranking) {
        ranking.sort(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o2.getTotalScore() - o1.getTotalScore();
            }
        });
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        final TextView name;
        final TextView score;
        final ImageView imageView;

        UserViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView_name);
            score = itemView.findViewById(R.id.textView_score);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
    class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {

        private final List<User> users;

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

            // Set User values in ranking layout
            viewHolder.name.setText(user.getName());
            viewHolder.score.setText(getString(R.string.txt_score, user.getTotalScore()));
            Picasso.get().load(user.getImage()).into(viewHolder.imageView);

            // Define OnClick() for whole ranking layout
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int listPos = viewHolder.getAdapterPosition();
                    User user = users.get(listPos);

                    // Show User details activity
                    Intent intent = new Intent(RankingActivity.this, UserDetailsActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });
        }
        @Override public int getItemCount() { return users.size(); }
    }
}