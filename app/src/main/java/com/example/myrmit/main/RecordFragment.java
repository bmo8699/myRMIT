package com.example.myrmit.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myrmit.R;
import com.example.myrmit.SignInActivity;
import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.arrayAdapter.HistoryArrayAdapter;
import com.example.myrmit.model.objects.History;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 *
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {

    TextView tvUsername;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    ProgressBar progressBarGPA;
    ProgressBar credits_progress_bar;
    TextView tvGPA;
    TextView tvCredits;
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    TextView tvStudent_ID;
    CardView history;
    TextView tvDOB;
    TextView tvProgram;
    TextView tvGender;
    TextView tvRole;
    private Button logout;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        logout = view.findViewById(R.id.log_out_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), SignInActivity.class));
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


        // Display user's information
        tvUsername = view.findViewById(R.id.tv_fragment_record_username);
        tvGPA = view.findViewById(R.id.tvGPA);
        tvCredits = view.findViewById(R.id.tvCredits);
        history = view.findViewById(R.id.history_card);
        tvStudent_ID = view.findViewById(R.id.tvStudent_ID);
        tvDOB = view.findViewById(R.id.tvDOB);
        tvRole = view.findViewById(R.id.role);
        tvGender = view.findViewById(R.id.tvGender);
        tvProgram = view.findViewById(R.id.tvProgram);
        progressBarGPA = view.findViewById(R.id.progressBarGPA);
        credits_progress_bar = view.findViewById(R.id.credits_progress_bar);

        if (currentUser != null) {
            progressBarGPA.setMax(4 * 10);
            progressBarGPA.setProgress((int) (0));
            credits_progress_bar.setMax(384);
            credits_progress_bar.setProgress(0);
            String userEmail = currentUser.getEmail();
            assert userEmail != null;
            DocumentReference userRef = firebaseHandler.getAccount(userEmail);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(DocumentSnapshot user) {
                    String role = user.getString("role");
                    String name = user.getString("name");
                    String dob = user.getString("dob");
                    String gender = user.getString("gender");
                    String studentID = userEmail.split("@")[0];
                    assert role != null;
                    userRef.collection("programCode").document("program").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String program = (String) task.getResult().get("code");
                            if (role.equals("student")) {
                                task.getResult().getReference().collection("data").document("finishCourses").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        ArrayList<String> gradeList = (ArrayList<String>) task.getResult().get("grade");
                                        ArrayList<String> courseList = (ArrayList<String>) task.getResult().get("courseList");
                                        assert gradeList != null;
                                        int credits = gradeList.size() * 12;
                                        firebaseHandler.getProgram(program).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @SuppressLint("DefaultLocale")
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                ArrayList<String> list = (ArrayList<String>) task.getResult().get("courses");
                                                ArrayList<History> histories = new ArrayList<>();
                                                for (int i = 0; i < Objects.requireNonNull(courseList).size(); i++){
                                                    histories.add(get(courseList.get(i), gradeList.get(i)));
                                                }
                                                assert list != null;
                                                double gpa = 0;
                                                for (String grade : gradeList) {
                                                    switch (grade) {
                                                        case "PA":
                                                            gpa += 1;
                                                            break;
                                                        case "CR":
                                                            gpa += 2;
                                                            break;
                                                        case "DI":
                                                            gpa += 3;
                                                            break;
                                                        case "HD":
                                                            gpa += 4;
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                                setHistoryClick(histories);
                                                gpa = gpa / gradeList.size();
                                                credits_progress_bar.setMax(list.size() * 12);
                                                tvUsername.setText(name);
                                                tvRole.setText("Student");
                                                tvGPA.setText(String.format("%.2f", gpa));
                                                progressBarGPA.setProgress((int) (gpa * 10));
                                                credits_progress_bar.setProgress((int) credits);
                                                tvCredits.setText(credits + "/" + (list.size() * 12));
                                                tvStudent_ID.setText(studentID);
                                                tvDOB.setText(dob);
                                                tvGender.setText(gender);
                                                tvProgram.setText(program);
                                            }
                                        });
                                    }
                                });
                            }
                            else {
                                progressBarGPA.setMax(0);
                                progressBarGPA.setProgress((int) (0));
                                credits_progress_bar.setMax(0);
                                credits_progress_bar.setProgress(0);
                                tvUsername.setText(name);
                                tvRole.setText("Lecturer");
                                tvGPA.setText("N/A");
                                tvCredits.setText("N/A");
                                tvStudent_ID.setText(studentID);
                                tvDOB.setText(dob);
                                tvGender.setText(gender);
                                tvProgram.setText(program);
                            }
                        }
                    });
                }
            });
        }
        else {
            progressBarGPA.setMax(0);
            progressBarGPA.setProgress((int) (0));
            credits_progress_bar.setMax(0);
            credits_progress_bar.setProgress(0);
            tvGPA.setText("N/A");
            tvUsername.setText("Guest");
            tvRole.setText("Guest");
            tvCredits.setText("N/A");
            tvStudent_ID.setText("N/A");
            tvDOB.setText("N/A");
            tvGender.setText("N/A");
            tvProgram.setText("N/A");
        }
        return view;
    }

    private void setHistoryClick(ArrayList<History> histories){
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.dialog_view_history, null);
                dialog.setView(view);
                final AlertDialog alert = dialog.create();
                ListView listView = view.findViewById(R.id.historylist);
                ArrayAdapter<History> adapter = new HistoryArrayAdapter(getActivity(), histories);
                listView.setAdapter(adapter);
                alert.show();
            }
        });
    }

    private History get(String name, String gpa){
        return new History(name, gpa);
    }

}