package io.bytechat.demo.oldrelease.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import io.bytechat.demo.R;
import io.bytechat.demo.oldrelease.ui.search.AddConversActivity;
import io.bytechat.demo.oldrelease.ui.search.SearchConversActivity;

public class AddFriendOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_options);
        ConstraintLayout search , scan ;
        search = findViewById(R.id.search_layout);
        scan = findViewById(R.id.scan_layout);
        ImageView back ;
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddFriendOptionsActivity.this, SearchConversActivity.class));

            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddFriendOptionsActivity.this, ScannerActivity.class));

            }
        });
    }
}
