package io.openim.android.ouiconversation.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.ChoiceBottomBarLayoutBinding;
import io.openim.android.ouiconversation.ui.ChatActivity;
import io.openim.android.ouiconversation.ui.ForwardMessageActivity;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.sdk.models.Message;

public class ChoiceBottomBar {
    ChatActivity context ;
    ChoiceBottomBarLayoutBinding view ;
    ChatVM vm ;
    public ChoiceBottomBar(ChatActivity context, ChoiceBottomBarLayoutBinding layoutChoiceBottomBar , ChatVM vm,String nickname) {
        this.context = context ;
        this.view = layoutChoiceBottomBar ;
        this.vm = vm ;


        view.removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteBottomSheet fragment = new DeleteBottomSheet(vm);
                fragment.show(context.getSupportFragmentManager(),fragment.getTag());
            }
        });
        view.forwardAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ForwardBottomSheet fragment = new ForwardBottomSheet(vm,nickname);
                fragment.show(context.getSupportFragmentManager(),fragment.getTag());
            }
        });

    }
}
