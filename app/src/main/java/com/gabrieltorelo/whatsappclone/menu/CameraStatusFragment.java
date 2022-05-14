package com.gabrieltorelo.whatsappclone.menu;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.FragmentCameraStatusBinding;

public class CameraStatusFragment extends Fragment {

    public CameraStatusFragment() {

    }

    private FragmentCameraStatusBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera_status, container, false);

        return binding.getRoot();
    }
}