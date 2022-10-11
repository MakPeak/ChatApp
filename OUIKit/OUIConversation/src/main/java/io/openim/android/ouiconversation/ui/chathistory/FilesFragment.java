package io.openim.android.ouiconversation.ui.chathistory;

import static io.openim.android.ouiconversation.utils.Constant.MsgType.FILE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.FileTabItemBinding;
import io.openim.android.ouiconversation.databinding.FragmentFilesBinding;
import io.openim.android.ouiconversation.utils.FloatBtnUtil;
import io.openim.android.ouiconversation.vm.ChatHistoryVM;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.SearchResult;

public class FilesFragment extends BaseFragment<ChatHistoryVM> implements ChatHistoryVM.ViewAction {
    FragmentFilesBinding binding ;
    String chatID ;
    RecyclerViewAdapter<Message, FilesFragment.RecyclerViewItem> adapter ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        bindVM(ChatHistoryVM.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter<Message, FilesFragment.RecyclerViewItem>(FilesFragment.RecyclerViewItem.class) {
            @Override
            public void onBindView(@NonNull FilesFragment.RecyclerViewItem holder, Message data, int position) {
                System.out.println("FilesFragment " + data.getContent() + " " + data.getFileElem().getFileName());
//                String fileName = data.getFileElem().getFileName().replaceAll("%20", " ");
                String fileName = "";
                try {
                    fileName = java.net.URLDecoder.decode(data.getFileElem().getFileName(), StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    // not going to happen - value came from JDK's own StandardCharsets
                    e.printStackTrace();
                }
                holder.v.message.setText(fileName);
//                holder.v.ivUser.load(data.getSenderFaceUrl());
//                holder.v.ivUser.setBackgroundResource(R.drawable.background_gradient_user_chat_btn);
                holder.v.sender.setText(data.getSenderNickname());
                holder.v.searchLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FloatBtnUtil utilsFunctions = new FloatBtnUtil(getActivity());
                        if (utilsFunctions.checkPermission(getActivity(), 1001, getView())) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data.getFileElem().getSourceUrl())));
                            } catch (Exception e) {
                                e.getStackTrace();
                            }

                        }
                    }
                });

                int index = fileName.lastIndexOf('.');
                if(index > 0) {
                    String extension = fileName.substring(index + 1);
                    System.out.println(fileName + "\t" + extension);

                    switch(extension) {
                        case "pdf":
                            Glide.with(getContext())
                                .load(getImage("ic_file_ftp"))
                                .into(holder.v.ivUser);
                            break;
                        case "txt":
                            Glide.with(getContext())
                                .load(getImage("ic_file_txt"))
                                .into(holder.v.ivUser);
                            break;
                        case "ppt":
                            Glide.with(getContext())
                                .load(getImage("ic_file_ppt"))
                                .into(holder.v.ivUser);
                            break;
                        case "jpg":
                            Glide.with(getContext())
                                .load(getImage("ic_file_image"))
                                .into(holder.v.ivUser);
                        case "jpeg":
                            Glide.with(getContext())
                                .load(getImage("ic_file_image"))
                                .into(holder.v.ivUser);
                            break;
                        case "xls":
                            Glide.with(getContext())
                                .load(getImage("ic_file_excel"))
                                .into(holder.v.ivUser);
                        case "xlsx":
                            Glide.with(getContext())
                                .load(getImage("ic_file_excel"))
                                .into(holder.v.ivUser);
                            break;
                        case "mp4":
                            Glide.with(getContext())
                                .load(getImage("ic_file_video"))
                                .into(holder.v.ivUser);
                            break;
                        case "mp3":
                            Glide.with(getContext())
                                .load(getImage("ic_file_music"))
                                .into(holder.v.ivUser);
                            break;
                        case "zip":
                            Glide.with(getContext())
                                .load(getImage("ic_file_zip"))
                                .into(holder.v.ivUser);
                            break;
                        case "rar":
                            Glide.with(getContext())
                                .load(getImage("ic_file_zip"))
                                .into(holder.v.ivUser);
                            break;
                        case "docx":
                            Glide.with(getContext())
                                .load(getImage("ic_file_word"))
                                .centerCrop()
                                .into(holder.v.ivUser);
                            break;
                        default:
                            Glide.with(getContext())
                                .load(getImage("ic_file_other"))
                                .centerCrop()
                                .into(holder.v.ivUser);
                    }
                }
            }
        };

        List<Message> list = new LinkedList<>();
        adapter.setItems(list);
        binding.recyclerView.setAdapter(adapter);

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable editable) {
                vm.ConvId.setValue(chatID);
                vm.searchKeyword.setValue(editable.toString());
                if(!editable.toString().isEmpty())
                    vm.searchMessage(FILE);
                else
                    adapter.setItems(new LinkedList<>());
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFilesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onSuccess(SearchResult body) {
        List<Message> list = new LinkedList<>();
        for (Message o : body.getSearchResultItems().get(0).getMessageList()){
            list.add(o);
        }
        adapter.setItems(list);
    }

    @Override
    public void onEmpty() {
        System.out.println("onEmpty ");
        vm.searchKeyword.setValue("");
        vm.searchMessage(FILE);
    }

    public static class RecyclerViewItem extends RecyclerView.ViewHolder {
        public FileTabItemBinding v;

        public RecyclerViewItem(@NonNull View parent) {
            super((FileTabItemBinding.inflate(LayoutInflater.from(parent.getContext()), (ViewGroup) parent, false).getRoot()));
            v = FileTabItemBinding.bind(this.itemView);

        }
    }

    public int getImage(String imageName) {
        int drawableResourceId = getContext().getResources().getIdentifier(imageName, "drawable", getContext().getPackageName());
        return drawableResourceId;
    }
}
