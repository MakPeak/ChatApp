package io.openim.android.ouiconversation.widget;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.LinkedList;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.FragmentDeleteBottomSheetBinding;
import io.openim.android.ouiconversation.vm.ChatVM;

public class DeleteBottomSheet extends BottomSheetDialogFragment {

    FragmentDeleteBottomSheetBinding binding ;
    ChatVM vm ;
    public DeleteBottomSheet(ChatVM vm) {
        this.vm = vm ;
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDeleteBottomSheetBinding.inflate(inflater, container, false);
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vm.deleteMessages();
                dismiss();
                vm.selectedMessages.setValue(new LinkedList<>());
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return binding.getRoot();
    }
}
