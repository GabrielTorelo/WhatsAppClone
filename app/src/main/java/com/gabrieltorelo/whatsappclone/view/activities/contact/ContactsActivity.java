package com.gabrieltorelo.whatsappclone.view.activities.contact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.adapter.ContactListAdapter;
import com.gabrieltorelo.whatsappclone.databinding.ActivityContactsBinding;
import com.gabrieltorelo.whatsappclone.model.user.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";
    private ActivityContactsBinding binding;
    private List<Users> list = new ArrayList<>();
    private ContactListAdapter adapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestorage;
    public static final int REQUEST_READ_CONTACTS = 79;
    private ArrayList mobileNumberArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestorage = FirebaseFirestore.getInstance();

        if (firebaseUser != null){
            getContactFromPhone();
        }

        if (mobileNumberArray != null) {
            getContactList();
        }

        initToolbar();
    }

    private void getContactFromPhone() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileNumberArray = getAllPhoneContacts();

        } else {
            requestPermission();
        }
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS
            }, REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
       String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileNumberArray = getAllPhoneContacts();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    private ArrayList getAllPhoneContacts() {
        ArrayList<String> phoneList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));

                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

        return phoneList;
    }

    private void getContactList() {
        firestorage.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots){
                        String userID = snapshots.getString("userID");
                        String userName = snapshots.getString("userName");
                        String imageProfile = snapshots.getString("imageProfile");
                        String bio = snapshots.getString("bio");
                        String bioDate = snapshots.getString("bioDate");
                        String phone = snapshots.getString("userPhone");

                        Users user = new Users();
                        user.setUserID(userID);
                        user.setBio(bio);
                        user.setBioDate(bioDate);
                        user.setUserName(userName);
                        user.setImageProfile(imageProfile);
                        user.setUserPhone(phone);

                        if (userID != null && !userID.equals(firebaseUser.getUid())) {
                            if (mobileNumberArray.contains(user.getUserPhone())){
                                list.add(user);
                            }
                        }
                    }

                    binding.progressCircular.setVisibility(View.GONE);
                    binding.layoutNewGroup.setVisibility(View.VISIBLE);
                    binding.layoutNewContact.setVisibility(View.VISIBLE);

                    adapter = new ContactListAdapter(list, ContactsActivity.this);
                    binding.recyclerView.setAdapter(adapter);
            }
        });
    }

    private void initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}