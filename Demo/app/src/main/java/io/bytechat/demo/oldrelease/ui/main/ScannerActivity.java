package io.bytechat.demo.oldrelease.ui.main;

import static io.openim.android.ouiconversation.ui.frowardmessage.ChatHistoryMergeMessagesActivity.title;
import static io.openim.android.ouicore.utils.Constant.ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.ActivityScannerBinding;
import io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity;
import io.bytechat.demo.oldrelease.vm.SearchVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.Constant;
import io.openim.android.ouicore.utils.Routes;

public class ScannerActivity extends BaseActivity{
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        System.out.println("Shikaaa ");
        CodeScannerView scannerView = (CodeScannerView) findViewById(R.id.scanner_view);
        try {
            mCodeScanner = new CodeScanner(this, scannerView);
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("scanner result : "+ result.getText());

                            String type_string= result.getText().substring(13,22);
                            System.out.println(result.getText());
                            System.out.println(type_string);

                            if(!result.getText().contains("io.openim.app")){
                                Toast.makeText(ScannerActivity.this,io.openim.android.ouicore.R.string.scan_wrong_qr_code, Toast.LENGTH_SHORT).show();
                                mCodeScanner.startPreview();
                                return;
//                                finishAffinity();
//                                startActivity(new Intent(ScannerActivity.this , MainActivity.class));
                            }
//                            Toast.makeText(ScannerActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                            if(type_string.contains("/joinGrou")){
                                String id_string= result.getText().substring(24,result.getText().length());
                                System.out.println(id_string);
//                                ARouter.getInstance().build(Routes.Group.DETAIL)
//                                    .withString(io.openim.android.ouicore.utils.Constant.GROUP_ID, id_string).navigation();
                                Intent theIntent = new Intent(ScannerActivity.this, MainActivity.class);
                                theIntent.putExtra("ID", id_string);
                                startActivity(theIntent);
                                Constant.SCAN_GROUP_ID = id_string;
                            }else{
                                String id_string= result.getText().substring(24,result.getText().length());
                                System.out.println(id_string);

//                                ScannerActivity.this.startActivity(new Intent(ScannerActivity.this, MainActivity.class).putExtra(ID, id_string));
//                                ScannerActivity.this.finish();
                                Intent theIntent = new Intent(ScannerActivity.this, MainActivity.class);
                                theIntent.putExtra("ID", id_string);
                                startActivity(theIntent);
                                Constant.SCAN_USER_ID = id_string;

                            }
                        }
                    });
                }
            });
            mCodeScanner.startPreview();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
