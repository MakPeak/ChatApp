package io.bytechat.demo.oldrelease.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import io.bytechat.demo.R;
import io.bytechat.demo.oldrelease.vm.LoginVM;
import io.bytechat.demo.oldrelease.vm.MainVM;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.bytechat.demo.databinding.FragmentDiscoverBinding;
import io.openim.android.ouicore.base.BaseFragment;

public class DiscoverFragment extends BaseFragment<MainVM> implements LoginVM.ViewAction{

    FragmentDiscoverBinding binding ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        binding.setVm(vm);
        vm.setIView(this);
        vm.discover();

        vm.discoverURL.observe(requireActivity(), data->{
            if(data.isEmpty()){
                binding.webview.setVisibility(View.GONE);
                binding.rlNoWebview.setVisibility(View.VISIBLE);
            } else {
                binding.webview.setVisibility(View.VISIBLE);
                binding.rlNoWebview.setVisibility(View.GONE);
                // this will enable the javascript.
                binding.webview.getSettings().setLoadsImagesAutomatically(true);
                binding.webview.getSettings().setJavaScriptEnabled(true);
                binding.webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                binding.webview.getSettings().setDomStorageEnabled(true);
                binding.webview.getSettings().setUseWideViewPort(true);
                binding.webview.setWebViewClient(new WebViewClient());

                binding.webview.getSettings().setLoadWithOverviewMode(true);
                // WebViewClient allows you to handle
                // onPageFinished and override Url loading.
//                binding.webview.setWebViewClient(new MyWebViewClient());
                System.out.println("WEBVIEW URL: " + data);

                binding.webview.loadUrl(data);
            }

        });
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(MainVM.class);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void jump() {

    }

    @Override
    public void err(String msg) {

    }

    @Override
    public void succ(Object o) {

    }

    @Override
    public void initDate() {

    }

}


