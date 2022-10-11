package io.openim.android.ouicore.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import io.openim.android.ouicore.databinding.DialogConfirmationLayoutBinding;

public class ConfirmationDialog extends Dialog {

    private DialogConfirmationLayoutBinding binding;

    private OnClickListener onPositiveClicked;
    private OnClickListener onNegativeClicked;

    private final String message, cancelText, okText;

    public ConfirmationDialog(
        @NonNull Context context,
        String message,
        String cancelText,
        String okText
    ) {
        super(context);
        this.message = message;
        this.cancelText = cancelText;
        this.okText = okText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DialogConfirmationLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvMessage.setText(message);
        binding.tvCancel.setText(cancelText);
        binding.tvOk.setText(okText);

        binding.tvCancel.setOnClickListener(v -> {
            if (onNegativeClicked != null)
                onNegativeClicked.onClick(this, DialogInterface.BUTTON_NEGATIVE);
        });
        binding.tvOk.setOnClickListener(v -> {
            if (onPositiveClicked != null)
                onPositiveClicked.onClick(this, DialogInterface.BUTTON_POSITIVE);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        int width = (int)(getContext().getResources().getDisplayMetrics().widthPixels * 0.8);
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void setOnPositiveClicked(OnClickListener onPositiveClicked) {
        this.onPositiveClicked = onPositiveClicked;
    }

    public void setOnNegativeClicked(OnClickListener onNegativeClicked) {
        this.onNegativeClicked = onNegativeClicked;
    }
}
