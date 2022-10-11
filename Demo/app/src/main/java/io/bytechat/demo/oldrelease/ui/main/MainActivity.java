package io.bytechat.demo.oldrelease.ui.main;

import static io.openim.android.ouicore.utils.Common.authViewModel;
import static io.openim.android.ouicore.utils.Constant.ID;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.JPushMessageReceiver;
import io.bytechat.demo.oldrelease.ui.login.LoginActivity;
import io.bytechat.demo.oldrelease.ui.search.AddConversActivity;
import io.bytechat.demo.oldrelease.utils.LocaleHelper;
import io.bytechat.demo.oldrelease.vm.LoginVM;
import io.bytechat.demo.oldrelease.vm.MainVM;
import io.bytechat.demo.R;
import io.bytechat.demo.databinding.ActivityMainBinding;
import io.bytechat.demo.databinding.LayoutAddActionBinding;
import io.bytechat.demo.ui.profile.VersionUpdateForceDialog;
import io.bytechat.demo.ui.profile.VersionUpdateLatestDialog;
import io.bytechat.demo.ui.profile.VersionUpdateUnforceDialog;
import io.openim.android.ouiconversation.vm.ContactListVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.utils.Constant;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;

public class MainActivity extends BaseActivity<MainVM, ActivityMainBinding> implements LoginVM.ViewAction {

    private int mCurrentTabIndex;
    private BaseFragment lastFragment, conversationListFragment, contactFragment;
    public static BottomNavigationView navView;
    String version = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("DISPLAY LANGUAGE: " + Locale.getDefault().getDisplayLanguage());
        LocaleHelper.settingLanguage(MainActivity.this);
        bindVM(MainVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityMainBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());

        view.setMainVM(vm);
        view.setLifecycleOwner(this);
        navView = (BottomNavigationView) findViewById(R.id.nav_view);

        PackageManager pm = getApplicationContext().getPackageManager();
        String pkgName = getApplicationContext().getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pkgInfo.versionName;

        for (int i = 0; i < navView.getChildCount(); i++) {

            ViewGroup vg = (ViewGroup) navView.getChildAt(i);

            for (int j = 0; j < vg.getChildCount(); j++){
                vg.getChildAt(j).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        System.out.println("hold click on nav view item ");
                        return true;
                    }
                });
            }



        }
        vm.discover();
        vm.getRecvFriendApplicationList();
        vm.getRecvGroupApplicationList();
        vm.getLatestVersion();

        if(getIntent().getExtras() != null) {
            String idUserOrGroup = getIntent().getExtras().getString("ID");
            System.out.println("idUserOrGroup: " + idUserOrGroup);
        }

        JPushInterface.resumePush(getApplicationContext());
        String registrationID = JPushInterface.getRegistrationID(getApplicationContext());
        Log.d("TAG", "onCreate:getRegistrationID "+registrationID);
//        vm.visibility.observe(this, v -> view.isOnline.setVisibility(v));
//        vm.avatar.observe(this , data->{
//            Glide.with(this)
//                .load(data)
//                .into(view.avatar);
//        });

        initBottomBar();

        click();
    }

    @Override
    public void onResume(){
        super.onResume();
        ContactListVM.isActivityActive.setValue(true);
    }

    private void initBottomBar() {

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        vm.discoverResponseMutableLiveData.observe(this, data->{
            if(data.getData().getStatus() == 0){
                navView.getMenu().removeItem(R.id.navigation_discover);
            }
        });

        authViewModel.counterContact.observe(this, data -> {
            if(data > 0) {
                BadgeDrawable badge = MainActivity.navView.getOrCreateBadge(io.bytechat.demo.R.id.navigation_address_book);
                badge.setNumber(data);
                badge.setVisible(true);
            }else{
                BadgeDrawable badge = MainActivity.navView.getOrCreateBadge(io.bytechat.demo.R.id.navigation_address_book);
                badge.setVisible(false);
            }
        });

//        authViewModel.counterContact.observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer integer) {
//                BadgeDrawable badge = MainActivity.navView.getOrCreateBadge(io.bytechat.demo.R.id.navigation_address_book);
//                badge.setNumber(integer);
//            }
//
//        });

        vm.latestVersionDataMutableLiveData.observe(this, data->{
            if(data == null)
                return;

            int backendVersion = Integer.parseInt(data.getVersion().replace(".", ""));
            int appVersion = Integer.parseInt(version.replace(".", ""));

            if(backendVersion > appVersion) {
                if (data.getIsforce() == 2) {
                    VersionUpdateForceDialog versionUpdateForceDialog = new VersionUpdateForceDialog(MainActivity.this,
                        vm.latestVersionDataMutableLiveData.getValue().getDownloadUrl(), vm.latestVersionDataMutableLiveData.getValue().getVersion());
                    versionUpdateForceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    versionUpdateForceDialog.setCancelable(false);
                    versionUpdateForceDialog.setCanceledOnTouchOutside(false);
                    versionUpdateForceDialog.show();
                } else if (data.getIsforce() == 1) {
                    VersionUpdateUnforceDialog versionUpdateUnforceDialog = new VersionUpdateUnforceDialog(MainActivity.this,
                        vm.latestVersionDataMutableLiveData.getValue().getDownloadUrl(), vm.latestVersionDataMutableLiveData.getValue().getVersion());
                    versionUpdateUnforceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    versionUpdateUnforceDialog.show();
                }
            }
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(view.navView, navController);
    }

    private void click() {
//        showPopupWindow();
//        view.menuGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.men1)
//                    switchFragment(conversationListFragment);
//                if (checkedId == R.id.men2)
//                    switchFragment(contactFragment);
//            }
//        });
    }

//    private void showPopupWindow() {
//        view.addFriend.setOnClickListener(v -> {
//            //初始化一个PopupWindow，width和height都是WRAP_CONTENT
//            PopupWindow popupWindow = new PopupWindow(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            LayoutAddActionBinding view = LayoutAddActionBinding.inflate(getLayoutInflater());
//            view.addFriend.setOnClickListener(c -> {
//                popupWindow.dismiss();
//                startActivity(new Intent(this, AddFriendOptionsActivity.class));
//            });
//            view.addGroup.setOnClickListener(c -> {
//                popupWindow.dismiss();
//                startActivity(new Intent(this, JoinGroupOptionActivity.class));
//            });
//            view.createGroup.setOnClickListener(c -> {
//                popupWindow.dismiss();
//                ARouter.getInstance().build(Routes.Group.CREATE_GROUP).navigation();
//            });
//            //设置PopupWindow的视图内容
//            popupWindow.setContentView(view.getRoot());
//            //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
//            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
//            popupWindow.setOutsideTouchable(true);
//
//            //设置PopupWindow消失监听
//            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//
//                }
//            });
//            //PopupWindow在targetView下方弹出
//            popupWindow.showAsDropDown(v);
//
//        });
//    }


    @Override
    public void jump() {
        //token过期
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void err(String msg) {

    }

    @Override
    public void succ(Object o) {

    }

    @Override
    public void initDate() {
//        conversationListFragment = (BaseFragment) ARouter.getInstance().build(Routes.Conversation.CONTACT_LIST).navigation();
//        contactFragment = (BaseFragment) ARouter.getInstance().build(Routes.Contact.HOME).navigation();
//
//        if (null != contactFragment) {
//            contactFragment.setPage(2);
//            switchFragment(contactFragment);
//        }
//
//        if (null != conversationListFragment) {
//            conversationListFragment.setPage(1);
//            switchFragment(conversationListFragment);
//        }

//        getSupportFragmentManager().beginTransaction()
//            .add(R.id.fragment_container, contactListFragment).commit();
    }


    /**
     * 切换Fragment
     */
    private void switchFragment(BaseFragment fragment) {
//        try {
//            if (fragment != null && !fragment.isVisible() && mCurrentTabIndex != fragment.getPage()) {
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
////                if (!fragment.isAdded()) {
////                    transaction.add(R.id.fragment_container, fragment);
////                }
//                if (lastFragment != null) {
//                    transaction.hide(lastFragment);
//                }
//                transaction.show(fragment).commit();
//                lastFragment = fragment;
//                mCurrentTabIndex = lastFragment.getPage();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
