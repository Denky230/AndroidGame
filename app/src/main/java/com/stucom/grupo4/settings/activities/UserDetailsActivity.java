package com.stucom.grupo4.settings.activities;

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
import android.widget.Button;
import android.widget.EditText;
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
import com.stucom.grupo4.settings.model.Message;
import com.stucom.grupo4.settings.model.User;
import com.stucom.grupo4.settings.persistence.SharedPrefsData;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {

    private User user;

    private TextView txtName;
    private ImageView imgUser;
    private EditText edMessage;
    private Button btnSend;
    private RecyclerView recyclerView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        txtName = findViewById(R.id.textView_name);
        imgUser = findViewById(R.id.imageView);
        edMessage = findViewById(R.id.edMessage);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edMessage.getText().toString();
                sendMessage(message);
            }
        });

        initRecyclerView();

        // Assign User data from Intent
        user = (User) getIntent().getExtras().getSerializable("user");
        txtName.setText(user.getName());
        Picasso.get().load(user.getImage()).into(imgUser);
    }
    @Override protected void onResume() {
        super.onResume();

        // Restore message pending to send, if any
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String message = prefs.getString(String.valueOf(user.getId()), "");
        edMessage.setText(message);

        // Get messages from API
        messagesToAndFromRequest();
    }
    @Override protected void onPause() {
        // Save message in SharedPreferences just in case
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(String.valueOf(user.getId()), edMessage.getText().toString());
        ed.apply();

        super.onPause();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void sendMessage(String message) {
        // Send message to API
        sendMessageRequest(message);
        // Empty out message field
        edMessage.setText("");
        // Empty out SharedPreferences pending messages
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.remove(String.valueOf(user.getId()));
        ed.apply();
    }

    private void sendMessageRequest(final String message) {
        String requestURL = APIData.API_URL + "message/" + user.getId();
        StringRequest request = new StringRequest(
                Request.Method.PUT,
                requestURL,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        // Update message list
                        messagesToAndFromRequest();
                    }
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {}
                }) {
            @Override protected Map<String, String> getParams() {
                SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
                String token = prefs.getString(SharedPrefsData.Keys.TOKEN.name(), "");

                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("text", message);
                return params;
            }
        };
        MyVolley.getInstance(this).add(request);
    }
    private void messagesToAndFromRequest() {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String token = prefs.getString(SharedPrefsData.Keys.TOKEN.name(), "");
        String requestURL = APIData.API_URL + "message/" + user.getId() + "?token=" + token;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                requestURL,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        // Get message list from API
                        List<Message> messages = parseAPIResponse(response);

                        // Set MessagesAdapter with messages to recyclerView
                        MessagesAdapter adapter = new MessagesAdapter(messages);
                        recyclerView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {}
                }
        );
        MyVolley.getInstance(this).add(request);
    }
    private List<Message> parseAPIResponse(String response) {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<APIResponse<List<Message>>>() {}.getType();
        APIResponse<List<Message>> apiResponse = gson.fromJson(response, typeToken);

        return apiResponse.getData();
    }

    class MessageHolder extends RecyclerView.ViewHolder {

        final TextView text;

        MessageHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textView);
        }
    }
    class MessagesAdapter extends RecyclerView.Adapter<MessageHolder> {

        final List<Message> messages;

        MessagesAdapter(List<Message> messages) {
            super();
            this.messages = messages;
        }

        @NonNull @Override public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            Message message = messages.get(position);
            boolean isMessageMine = message.getId() != user.getId();

            // Inflate view with appropriate chat_message layout
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(
                            isMessageMine ? R.layout.chat_message_to : R.layout.chat_message_from,
                            parent, false
                    );
            return new MessageHolder(view);
        }
        @Override public void onBindViewHolder(@NonNull MessageHolder viewHolder, int position) {
            Message message = messages.get(position);

            // Set Message values in chat_message layout
            viewHolder.text.setText(message.getText());
        }
        @Override public int getItemCount() { return messages.size(); }
    }
}