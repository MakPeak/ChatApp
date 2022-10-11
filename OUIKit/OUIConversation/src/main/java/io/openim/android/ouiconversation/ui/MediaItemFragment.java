package io.openim.android.ouiconversation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import io.openim.android.ouiconversation.databinding.FragmentMediaItemBinding;
import io.openim.android.ouicore.utils.MediaFileUtil;

public class MediaItemFragment extends Fragment {

    public static final String ARG_MEDIA_URL = "media_url";
    public static final String ARG_FIRST_FRAME = "first_frame";

    private String mediaUrl;
    private String firstFrame;

    private FragmentMediaItemBinding binding;

    public MediaItemFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static MediaItemFragment newInstance(String mediaUrl, String firstFrame) {
        MediaItemFragment fragment = new MediaItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEDIA_URL, mediaUrl);
        args.putString(ARG_FIRST_FRAME, firstFrame);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mediaUrl = getArguments().getString(ARG_MEDIA_URL);
            firstFrame = getArguments().getString(ARG_FIRST_FRAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMediaItemBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (TextUtils.isEmpty(mediaUrl)) return;

        if (MediaFileUtil.isImageType(mediaUrl)) {
            binding.pic.setVisibility(View.VISIBLE);
            Glide.with(this)
                .load(mediaUrl)
                .into(binding.pic);
        } else if (MediaFileUtil.isVideoType(mediaUrl)) {
            binding.videoThumb.setVisibility(View.VISIBLE);
            binding.icVideoPlay.setVisibility(View.VISIBLE);
            System.out.println("firstFrame "+firstFrame);
            Glide.with(this)
                .load(firstFrame)
                .into(binding.videoThumb);

            binding.videoThumb.setOnClickListener(v -> startActivity(
                new Intent(view.getContext(), PreviewActivity.class)
                    .putExtra(PreviewActivity.MEDIA_URL, mediaUrl)
                    .putExtra(PreviewActivity.FIRST_FRAME, firstFrame)));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
