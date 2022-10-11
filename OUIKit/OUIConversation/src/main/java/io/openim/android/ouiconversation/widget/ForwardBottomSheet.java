package io.openim.android.ouiconversation.widget;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.LinkedList;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.FragmentDeleteBottomSheetBinding;
import io.openim.android.ouiconversation.databinding.FragmentForwardBottomSheetBinding;
import io.openim.android.ouiconversation.ui.ForwardMessageActivity;
import io.openim.android.ouiconversation.vm.ChatVM;


public class ForwardBottomSheet extends BottomSheetDialogFragment {
    FragmentForwardBottomSheetBinding binding ;

    ChatVM vm ;
    String nickname ;
    public ForwardBottomSheet(ChatVM vm , String nickname) {
        this.nickname = nickname ;
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
        binding = FragmentForwardBottomSheetBinding.inflate(inflater, container, false);

        binding.forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title ;
                if(vm.isSingleChat)
                    title =  "Chat history from "+nickname;
                else
                    title = "Group chat history";
                ForwardMessageActivity.forwardMsg = vm.createMergerMessage( title ) ;
                ForwardBottomSheet.this.getActivity().startActivity(new Intent(ForwardBottomSheet.this.getActivity() , ForwardMessageActivity.class));
                vm.selectedMessages.setValue(new LinkedList<>());
                dismiss();
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
