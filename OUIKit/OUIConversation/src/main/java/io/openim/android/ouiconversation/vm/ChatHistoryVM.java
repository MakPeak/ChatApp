package io.openim.android.ouiconversation.vm;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.SearchResult;

public class ChatHistoryVM extends BaseViewModel<ChatHistoryVM.ViewAction> {
    public MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<String> searchKeyword = new MutableLiveData<>();
    public MutableLiveData<String> ConvId = new MutableLiveData<>();

    public void searchMessage(Integer... types) {
        List<String> keywordList = new LinkedList<>();
        List<Integer> typeList = new LinkedList<>(Arrays.asList(types));
        keywordList.add(searchKeyword.getValue());
        OpenIMClient.getInstance().messageManager.searchLocalMessages(new OnBase<SearchResult>() {
            @Override
            public void onError(int code, String error) {
                Log.e("Search chat : " , "code : " + code + " error : "+ error);
                IView.onError(error);
            }

            @Override
            public void onSuccess(SearchResult data) {
                if(data.getSearchResultItems() == null) {
                    IView.onEmpty();
                    return;
                }
                IView.onSuccess(data);
            }

        }, ConvId.getValue(), keywordList, 2, null, typeList, 0, 0, 1, 10000);
    }

    public interface ViewAction extends io.openim.android.ouicore.base.IView {

        void onError(String msg);

        void onSuccess(SearchResult o);

        void onEmpty();
    }
}
