package com.example.myrmit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    ViewPager viewPager;
    SwipeCardsAdapter swipCardsAdapter;
    List<News> newsList;
    CardView fragment_home_cardview_clubs;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Temp swipe cards
        newsList = new ArrayList<News>();
        newsList.add(new News(R.drawable.temp_news_image, "RMIT Postgraduation Day", "Join our postgraduate workshops and discover","RMIT"));
        newsList.add(new News(R.drawable.temp_news_image, "RMIT Postgraduation Day", "Join our postgraduate workshops and discover","RMIT"));
        newsList.add(new News(R.drawable.temp_news_image, "RMIT Postgraduation Day", "Join our postgraduate workshops and discover","RMIT"));

        swipCardsAdapter = new SwipeCardsAdapter(newsList, getContext());
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(swipCardsAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position < (swipCardsAdapter.getCount() - 1)) {

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fragment_home_cardview_clubs = view.findViewById(R.id.fragment_home_cardview_clubs);
        fragment_home_cardview_clubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment clubsFragment = new ClubsFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_home, clubsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        return view;
    }

}