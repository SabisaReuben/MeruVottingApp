package com.example.mustapp;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class LauncherFragment extends Fragment {
    /**
     * Synchronizes other threads with the UIThread
     */
    private Handler handler;
    /**
     * Runs timeTasks in this fragment
     */
    private Timer timer;
    public LauncherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new Handler();
        this.timer = new Timer();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate the layout
        return inflater.inflate(R.layout.fragment_launcher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(requireActivity(), R.id
                .navHostFragment);
        //launch a loginFragment after delay of 4sec
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> navController.navigate(R.id.
                        action_launcherFragment_to_loginFragment, null, getNavOptions()));

            }
        },4000);

    }
    private static NavOptions getNavOptions() {
        return new NavOptions.Builder().
                setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .setPopUpTo(R.id.loginFragment, true)
                .setPopEnterAnim(android.R.anim.slide_in_left)
                .setPopExitAnim(android.R.anim.slide_out_right)
                .build();
    }
}
