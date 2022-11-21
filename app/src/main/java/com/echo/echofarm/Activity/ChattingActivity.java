package com.echo.echofarm.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.echo.echofarm.R;

import java.util.ArrayList;

public class ChattingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText sendingMessage;
    private Button sendMessageButton;
    private ArrayList<ChattingData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        // 액션바 제목
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000'>홍석희</font>"));


        recyclerView = findViewById(R.id.chatting_recyclerView);
        sendingMessage = findViewById(R.id.sending_message_editText);
        sendMessageButton = findViewById(R.id.send_message_button);


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!sendingMessage.getText().toString().equals("")) {
                        list.add(new ChattingData(sendingMessage.getText().toString(), 1));
                        ChattingDataAdapter adapter = new ChattingDataAdapter(ChattingActivity.this, list, "홍석희");
                        recyclerView.setAdapter(adapter);

                        recyclerView.scrollToPosition(list.size() - 1);
                        sendingMessage.setText("");

                        // send message info to server
                    }
                } catch (Exception e) {
                    Toast.makeText(ChattingActivity.this, "메세지만 입력 가능합니다.", Toast.LENGTH_SHORT);
                }
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        ArrayList<String> chatting = new ArrayList<>();
        chatting.add("내 채팅1");
        chatting.add("내 채팅2");
        chatting.add("상대 채팅 1");
        chatting.add("상대 채팅 2");
        chatting.add("내 채팅 3");

        ArrayList<Integer> code = new ArrayList<>();
        code.add(1);
        code.add(1);
        code.add(0);
        code.add(0);
        code.add(1);

        list = new ArrayList<>();
        for(int i = 0; i < 5; i++)
            list.add(new ChattingData(chatting.get(i), code.get(i)));

        ChattingDataAdapter adapter = new ChattingDataAdapter(this, list, "홍석희");
        recyclerView.setAdapter(adapter);
    }
}