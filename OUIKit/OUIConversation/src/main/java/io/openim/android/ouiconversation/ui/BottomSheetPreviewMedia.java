package io.openim.android.ouiconversation.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.BottomSheetPreviewMediaBinding;

public class BottomSheetPreviewMedia extends BottomSheetDialogFragment {

    private BottomSheetPreviewMediaBinding binding;
    private OnClick onSaveToGalleryClicked;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetPreviewMediaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvSaveToGallery.setOnClickListener(v -> {
            if(onSaveToGalleryClicked != null)
                onSaveToGalleryClicked.onClick();
            dismiss();
        });
        binding.tvCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setOnSaveToGalleryClicked(OnClick onSaveToGalleryClicked) {
        this.onSaveToGalleryClicked = onSaveToGalleryClicked;
    }

    public interface OnClick {
        void onClick();
    }
}
