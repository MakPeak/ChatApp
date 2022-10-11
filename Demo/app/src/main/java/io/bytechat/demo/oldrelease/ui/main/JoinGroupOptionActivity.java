package io.bytechat.demo.oldrelease.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;

import io.bytechat.demo.R;
import io.bytechat.demo.oldrelease.ui.search.AddConversActivity;
import io.bytechat.demo.oldrelease.ui.search.SearchConversActivity;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouigroup.ui.CreateGroupActivity;

public class JoinGroupOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group_option);

        ConstraintLayout createGroup , joinGroup ;
        createGroup = findViewById(R.id.create_layout);
        joinGroup = findViewById(R.id.join_layout);
        ImageView back ;
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance().build(Routes.Group.CREATE_GROUP).navigation();

            }
        });
        joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JoinGroupOptionActivity.this, SearchConversActivity.class).putExtra(AddConversActivity.IS_PERSON, false));
            }
        });
    }
}
