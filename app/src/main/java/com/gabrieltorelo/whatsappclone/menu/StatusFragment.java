package com.gabrieltorelo.whatsappclone.menu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.FragmentStatusBinding;
import com.gabrieltorelo.whatsappclone.service.DataTimeService;
import com.gabrieltorelo.whatsappclone.view.activities.display.DisplayStatusActivity;
import com.gabrieltorelo.whatsappclone.view.activities.status.StatusActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StatusFragment extends Fragment {
    private FragmentStatusBinding binding;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    public int numStatus = 0;
    private int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_status, container, false);

        myStatus();
        initActionClick();
        return binding.getRoot();
    }

    public StatusFragment() {

    }

    private void initActionClick() {
        binding.layoutBtnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Ações Click", Toast.LENGTH_LONG).show();
            }
        });

        binding.layoutShareMyStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Share Click", Toast.LENGTH_LONG).show();
            }
        });
    }

    /* THE STATUS ADAPTER IS MISSING */
    private void myStatus(){
        reference.child("StatusDaily").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userID = dataSnapshot.child("userID").getValue().toString();
                    if (userID.equals(firebaseUser.getUid())) {
                        i++;
                    }
                }
                if (i > 0) {
                    getMyStatus();
                    i = 0;
                }
                else{
                    getMyStatusProfile();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), R.string.error_unexpected, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMyStatusProfile() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String imageProfile = documentSnapshot.getString("imageProfile");

                        if (!imageProfile.equals("")){
                            Glide.with(getContext()).load(imageProfile).into(binding.imageProfile);
                        }

                        binding.imageProfile.setBorderWidth(0f);
                        binding.btnAddStatus.setVisibility(View.VISIBLE);
                        binding.layoutBtnAction.setVisibility(View.GONE);
                        binding.layoutShareMyStatus.setVisibility(View.GONE);
                        binding.tvDesc.setText(R.string.add_status);
                        binding.layoutMyStatus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().startActivity(new Intent(getContext(), StatusActivity.class));
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Get Data", "onFailure: ERROR - "+e.getMessage());
            }
        });
    }

    /* THE STATUS ADAPTER IS MISSING */
    private void getMyStatus() {
        reference.child("StatusDaily").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()){
                    final String id = dataSnapshot.child("id").getValue().toString();
                    final String userID = dataSnapshot.child("userID").getValue().toString();
                    String createdDate = dataSnapshot.child("createdDate").getValue().toString();
                    String createdTime = dataSnapshot.child("createdTime").getValue().toString();
                    final String imageStatus = dataSnapshot.child("imageStatus").getValue().toString();
                    final String textStatus = dataSnapshot.child("textStatus").getValue().toString();
                    final String viewCount = dataSnapshot.child("viewCount").getValue().toString();
                    final String userView = dataSnapshot.child("userView").getValue().toString();
                    final String statusDate;

                    if (userID.equals(firebaseUser.getUid())) {
                        if (createdTime.equals(DataTimeService.getCurrentTime())){
                            statusDate = getResources().getString(R.string.just_now);
                            binding.tvDesc.setText(statusDate);
                        }
                        else if (createdDate.equals(DataTimeService.getCurrentDate())){
                            statusDate = String.format("%s, %s",
                                    getResources().getString(R.string.today),
                                    createdTime);
                            binding.tvDesc.setText(statusDate);
                        }
                        else {
                            statusDate = String.format("%s, %s",
                                    getResources().getString(R.string.yesterday),
                                    createdTime);
                            binding.tvDesc.setText(statusDate);
                        }

                        Glide.with(getContext()).load(imageStatus).into(binding.imageProfile);
                        binding.imageProfile.setBorderWidth(7f);
                        if (userView.equals("YES")){
                            binding.imageProfile.setBorderColor(Color.rgb(188, 191, 197));
                        }
                        else {
                            binding.imageProfile.setBorderColor(Color.rgb(49, 195, 172));
                        }
                        binding.btnAddStatus.setVisibility(View.GONE);
                        binding.layoutBtnAction.setVisibility(View.VISIBLE);
                        binding.layoutShareMyStatus.setVisibility(View.VISIBLE);
                        numStatus++;
                        binding.layoutMyStatus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().startActivity(new Intent(getContext(), DisplayStatusActivity.class)
                                        .putExtra("userID", userID)
                                        .putExtra("imageStatus", imageStatus)
                                        .putExtra("statusDate", statusDate)
                                        .putExtra("viewCount", viewCount)
                                        .putExtra("numStatus", numStatus)
                                        .putExtra("textStatus", textStatus));
                                if (!userView.equals("YES")) {
                                    reference.child("StatusDaily")
                                            .child(dataSnapshot.getKey())
                                            .child("userView").setValue("YES");
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), R.string.error_unexpected, Toast.LENGTH_LONG).show();
            }
        });
    }
}