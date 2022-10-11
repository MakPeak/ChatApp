package io.bytechat.demo.ui.profile;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;


import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.bytechat.demo.databinding.FragmentDatePickerBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.bytechat.demo.vm.PersonalInfoViewModel;
import io.openim.android.ouicore.base.BaseFragment;

public class DatePickerFragment extends BaseFragment<ProfileViewModel> implements ProfileViewModel.ViewAction {

    private FragmentDatePickerBinding binding;
    View view;
    int dateInInt;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        Date currentDate = Calendar.getInstance().getTime();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -100); // to get previous year add -1
        Date previousDate = cal.getTime();

        new SingleDateAndTimePickerDialog.Builder(getContext())
            .curved()
            .displayMinutes(false)
            .displayHours(false)
            .displayDays(false)
            .displayMonth(true)
            .displayYears(true)
            .displayDaysOfMonth(true)
            .mainColor(Color.BLACK)
            .minDateRange(previousDate)
            .maxDateRange(currentDate)
            .backgroundColor(Color.WHITE)
            .listener(new SingleDateAndTimePickerDialog.Listener() {
                @Override
                public void onDateSelected(Date date) {
//                    Toast.makeText(getContext(), date.toString(), Toast.LENGTH_SHORT).show();
                    SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    try {
                        //DATE TO LONG
                        Date d = sdf.parse(date.toString());
                        long milliseconds = d.getTime();
                        dateInInt = (int) (milliseconds/1000);
                        SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd");
                        String stringDate = DateFor.format(d);
                        vm.birthday.postValue(stringDate);
                        vm.setBirthday(stringDate);
//                        System.out.println("Integer : " + dateInInt);
//                        System.out.println("Long : "+ milliseconds);
//                        System.out.println("Long date : " + d.toString());
//                        System.out.println("Int Date : " + new Date(((long)dateInInt) * 1000L));
                        // LONG TO DATE
//                        milliseconds = System.currentTimeMillis();
//                        Date da = new Date(milliseconds);
//                        System.out.println(da);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Navigation.findNavController(view).getPreviousBackStackEntry().getSavedStateHandle().set("date", String.valueOf(dateInInt));
                    Navigation.findNavController(view).popBackStack();
                }
            })
            .display();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDatePickerBinding.inflate(inflater, container, false);
        binding.setVm(vm);
        vm.setIView(this);

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ProfileViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onErr(String msg) {
        Toast.makeText(this.getActivity(), "Failed, Check your internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSucc(Object o) {
        this.getActivity().startActivity(new Intent(this.getActivity(), MainActivity.class));
        this.getActivity().finish();
    }


}
