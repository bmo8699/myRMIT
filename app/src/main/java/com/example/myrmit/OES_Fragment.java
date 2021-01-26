package com.example.myrmit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myrmit.model.objects.Course;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.myrmit.model.*;
import com.example.myrmit.model.arrayAdapter.CoursesArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class OES_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private ListView listView;
    private ArrayList<Course> courses;
    private Button confirm;
    private ImageView loading;
    public OES_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private void setList() {
        courses = new ArrayList<>();
        listView.setAdapter(null);
        loading.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.INVISIBLE);
        firebaseHandler.getProgramOfStudent(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                firebaseHandler.getProgram((String)task.getResult().get("code")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<String> courseList = (ArrayList<String>) task.getResult().get("courses");
                        ArrayList<String> feb = (ArrayList<String>) task.getResult().get("feb");
                        ArrayList<String> jun = (ArrayList<String>) task.getResult().get("jun");
                        ArrayList<String> oct = (ArrayList<String>) task.getResult().get("oct");
                        ArrayList<Boolean> isFeb = new ArrayList<>();
                        ArrayList<Boolean> isJun = new ArrayList<>();
                        ArrayList<Boolean> isOct = new ArrayList<>();
                        assert courseList != null;
                        assert feb != null;
                        assert jun != null;
                        assert oct != null;
                        firebaseHandler.getAccount(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String role = (String) task.getResult().get("role");
                                assert role != null;
                                if (role.equals("student")){
                                    firebaseHandler.getCompletedCourses(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            ArrayList<String> completedCourses = (ArrayList<String>) task.getResult().get("courseList");
                                            assert completedCourses != null;
                                            List<Course> list = new ArrayList<Course>();
                                            for (int i = 0; i < courseList.size(); i++) {
                                                list.add(get(courseList.get(i)));
                                                boolean isExist = false;
                                                for (String course: completedCourses){
                                                    if (course.equals(courseList.get(i))){
                                                        isExist = true;
                                                        break;
                                                    }
                                                }
                                                if (!isExist) {
                                                    isFeb.add(feb.get(i).equals("1"));
                                                    isJun.add(jun.get(i).equals("1"));
                                                    isOct.add(oct.get(i).equals("1"));
                                                }
                                                else {
                                                    isFeb.add(false);
                                                    isJun.add(false);
                                                    isOct.add(false);
                                                }
                                            }
                                            firebaseHandler.getEnrolledCourses(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    ArrayList<String> enrolledList = (ArrayList<String>) task.getResult().get("list");
                                                    ArrayList<String> sem = (ArrayList<String>) task.getResult().get("semester");
                                                    assert sem != null;
                                                    for (int i = 0; i < Objects.requireNonNull(enrolledList).size(); i++) {
                                                        for (Course course : list) {
                                                            if (course.getName().equals(enrolledList.get(i))) {
                                                                if (sem.get(i).equals("feb")) {
                                                                    course.setFeb(true);
                                                                } else if (sem.get(i).equals("jun")) {
                                                                    course.setJun(true);
                                                                } else course.setOct(true);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    task.getResult().getReference().getParent().document("progressingCourse").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            ArrayList<String> progressList = (ArrayList<String>) task.getResult().get("list");
                                                            assert progressList != null;
                                                            ArrayList<String> progress = new ArrayList<>();
                                                            for (String course: courseList){
                                                                boolean isExist = false;
                                                                for (String inProgress : progressList){
                                                                    if (course.equals(inProgress)){
                                                                        isExist = true;
                                                                        break;
                                                                    }
                                                                }
                                                                if (isExist){
                                                                    progress.add("1");
                                                                }
                                                                else progress.add("0");
                                                            }
                                                            try {
                                                                ArrayAdapter<Course> adapter = new CoursesArrayAdapter(getActivity(), list, isFeb, isJun, isOct, progress, true);
                                                                listView.setAdapter(adapter);
                                                                loading.setVisibility(View.INVISIBLE);
                                                                confirm.setVisibility(View.VISIBLE);
                                                            }catch (Exception ignored){}
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                                else if (role.equals("lecturer")){
                                    firebaseHandler.getProgressingCourse(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            ArrayList<String> progressingCourse = (ArrayList<String>) task.getResult().get("list");
                                            List<Course> list = new ArrayList<Course>();
                                            assert progressingCourse != null;
                                            if (progressingCourse.size() == 0){
                                                firebaseHandler.getAllAccounts().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> taskk) {
                                                        ArrayList<String> teachingCourses = new ArrayList<>();
                                                        int[] i = {0};
                                                        for (DocumentSnapshot documentSnapshot: taskk.getResult()){
                                                            String role = (String) documentSnapshot.get("role");
                                                            assert role != null;
                                                            if (role.equals("student")){
                                                                firebaseHandler.getProgressingCourse(documentSnapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        i[0]++;
                                                                        ArrayList<String> courses = (ArrayList<String>) task.getResult().get("list");
                                                                        assert courses != null;
                                                                        for (String course : courses){
                                                                            if (teachingCourses.size() == 0 || !teachingCourses.contains(course)){
                                                                                teachingCourses.add(course);
                                                                            }
                                                                        }
                                                                        if (i[0] == taskk.getResult().size()){
                                                                            firebaseHandler.getProgressingCourse(user).update("list", teachingCourses);
                                                                            setList();
                                                                        }
                                                                    }
                                                                });
                                                            } else i[0]++;
                                                        }
                                                    }
                                                });
                                            }
                                            else {
                                                ArrayList<String> progress = new ArrayList<>();
                                                for (String course: courseList){
                                                    list.add(get(course));
                                                    isFeb.add(false);
                                                    isJun.add(false);
                                                    isOct.add(false);
                                                    boolean isExist = false;
                                                    for (String inProgress : progressingCourse){
                                                        if (course.equals(inProgress)){
                                                            isExist = true;
                                                            break;
                                                        }
                                                    }
                                                    if (isExist){
                                                        progress.add("1");
                                                    }
                                                    else progress.add("0");
                                                }
                                                try {
                                                    ArrayAdapter<Course> adapter = new CoursesArrayAdapter(getActivity(), list, isFeb, isJun, isOct, progress, false);
                                                    listView.setAdapter(adapter);
                                                    loading.setVisibility(View.INVISIBLE);
                                                    confirm.setEnabled(false);
                                                } catch (Exception ignored){}
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private Course get(String s) {
        Course course = new Course(s);
        courses.add(course);
        return course;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.oes_fragment, container, false);
        loading = view.findViewById(R.id.imageView);
        confirm = view.findViewById(R.id.button2);
        listView = view.findViewById(R.id.listview);
        confirmChange(view);
        setList();
        return view;
    }

    private void confirmChange(View view){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHandler.getEnrolledCourses(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<String> list = new ArrayList<>();
                        ArrayList<String> semester = new ArrayList<>();
                        for (Course course: courses){
                            if (course.isFeb() || course.isJun() || course.isOct()){
                                list.add(course.getName());
                                if (course.isFeb()){
                                    semester.add("feb");
                                }
                                else if (course.isJun()){
                                    semester.add("jun");
                                }
                                else if (course.isOct()){
                                    semester.add("oct");
                                }
                            }
                        }
                        if (countMaxEnrol(semester, "feb") < 5 && countMaxEnrol(semester, "jun") < 5 && countMaxEnrol(semester, "oct") < 5) {
                            ArrayList<String> enrolledCourse = (ArrayList<String>) task.getResult().get("list");
                            ArrayList<String> newSemester = (ArrayList<String>) task.getResult().get("semester");
                            assert enrolledCourse != null;
                            if (isChange(enrolledCourse, list, semester, newSemester)) {
                                Toast.makeText(view.getContext(), "Save Successful!", Toast.LENGTH_SHORT).show();
                                firebaseHandler.confirmEnrolment("s3740819@rmit.edu.vn", list, semester);
                                setList();
                            } else
                                Toast.makeText(view.getContext(), "There is no change in enrolment!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (countMaxEnrol(semester, "feb") >= 5){
                                showDialog(view, "The max number of courses for Feb semester is 4 only! Please try again!");
                            } else if (countMaxEnrol(semester, "jun") >= 5){
                                showDialog(view, "The max number of courses for Jun semester is 4 only! Please try again!");
                            } else showDialog(view, "The max number of courses for Nov semester is 4 only! Please try again!");
                        }
                    }
                });

            }
        });
    }

    private int countMaxEnrol(ArrayList<String> list, String semester){
        int count = 0;
        for (String sem: list){
            if (sem.equals(semester)){
                count++;
            }
        }
        return count;
    }

    public void showDialog(View view, String text){
        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext(), R.style.Theme_AppCompat_Dialog_Alert).create();
        alertDialog.setTitle("Fail!");
        alertDialog.setMessage(text);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private boolean isChange(ArrayList<String> enrolCourses, ArrayList<String> newList, ArrayList<String> semester, ArrayList<String> newSemester){
        if (enrolCourses.size() != newList.size()){
            return true;
        }
        else {
            for (int i = 0; i < newList.size(); i++){
                if (!newList.get(i).equals(enrolCourses.get(i))){
                    return true;
                }
                else if (!semester.get(i).equals(newSemester.get(i))){
                    return true;
                }
            }
            return false;
        }
    }

}