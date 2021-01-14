package com.example.myrmit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class TimetableFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public TimetableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.timetable_fragment, container, false);
        HorizontalCalendar[] horizontalCalendar = {new HorizontalCalendar.Builder(view.getRootView(), R.id.calendarView)
                .datesNumberOnScreen(5)
                .range(Calendar.getInstance(), Calendar.getInstance())
                .configure().textSize(12, 12, 14).colorTextBottom(Color.YELLOW, Color.GREEN).end()
                .build()};
        firebaseHandler.getCurrentSemester().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String sem = (String) task.getResult().get("semester");
                Date date;
                if (sem.split(",")[0].equals("feb")) {
                    date = new Date(Integer.parseInt(sem.split(", ")[1]), Calendar.FEBRUARY, 1);//the year field adds 1900 on to it.
                }
                else if (sem.split(",")[0].equals("jun")){
                    date = new Date(Integer.parseInt(sem.split(", ")[1]), Calendar.JUNE, 1);//the year field adds 1900 on to it.
                }
                else date = new Date(Integer.parseInt(sem.split(", ")[1]), Calendar.NOVEMBER, 1);//the year field adds 1900 on to it.
                
                Calendar startDate = new GregorianCalendar();
                startDate.setTime(date);
                startDate.add(Calendar.MONTH, 0);
                Calendar endDate =  new GregorianCalendar();
                endDate.setTime(date);
                endDate.add(Calendar.MONTH, 3);
                horizontalCalendar[0].refresh();
                horizontalCalendar[0].setRange(startDate,endDate);
                horizontalCalendar[0].setCalendarListener(new HorizontalCalendarListener() {
                    @Override
                    public void onDateSelected(Calendar date, int position) {
                        //do something
                    }
                });
            }
        });
        return view;
    }
}