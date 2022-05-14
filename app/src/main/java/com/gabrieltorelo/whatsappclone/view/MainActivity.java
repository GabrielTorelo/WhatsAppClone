package com.gabrieltorelo.whatsappclone.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivityMainBinding;
import com.gabrieltorelo.whatsappclone.manager.ConnectionService;
import com.gabrieltorelo.whatsappclone.menu.CallsFragment;
import com.gabrieltorelo.whatsappclone.menu.CameraStatusFragment;
import com.gabrieltorelo.whatsappclone.menu.ChatsFragment;
import com.gabrieltorelo.whatsappclone.menu.StatusFragment;
import com.gabrieltorelo.whatsappclone.view.activities.status.StatusActivity;
import com.gabrieltorelo.whatsappclone.view.activities.contact.ContactsActivity;
import com.gabrieltorelo.whatsappclone.view.activities.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ConnectionService connectionService;
//    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

//        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        initialize();
        initBtnClick();
    }

    private void initialize(){
        setUpViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        View camera = LayoutInflater.from(this).inflate(R.layout.custom_camera_tab, null);
        binding.tabLayout.getTabAt(0).setCustomView(camera);
        binding.viewPager.setCurrentItem(1);
        setSupportActionBar(binding.toolbar);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabIcon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        connectionService = new ConnectionService(this);
        connectionService.manageConnections();
    }

    private void initBtnClick(){
        binding.fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ContactsActivity.class));
            }
        });
    }

    private void setUpViewPager(ViewPager viewPager){
        MainActivity.SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraStatusFragment(), null);
        adapter.addFragment(new ChatsFragment(), "Conversas");
        adapter.addFragment(new StatusFragment(), "Status");
        adapter.addFragment(new CallsFragment(), "Chamadas");
        viewPager.setAdapter(adapter);
    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager){super (manager);}

        @Override
        public Fragment getItem(int position){return mFragmentList.get(position);}

        @Override
        public int getCount(){return mFragmentList.size();}

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){ return mFragmentTitleList.get(position);}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id){
            case R.id.menu_search : Toast.makeText(MainActivity.this, "Ação: Procurar", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_new_group : Toast.makeText(MainActivity.this, "Ação: Novo grupo", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_new_broadcast : Toast.makeText(MainActivity.this, "Ação: Nova transmissão", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_wa_web : Toast.makeText(MainActivity.this, "Ação: WhatsClone Web", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_starred_message : Toast.makeText(MainActivity.this, "Ação: Mensagens favoritas", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_settings : startActivity(new Intent(MainActivity.this, SettingsActivity.class )); break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeFabIcon(final int index){
        binding.fabAction.hide();

        switch (index){
            case 0 :
                binding.fabAction.hide();
                binding.fabAction.setVisibility(View.GONE);
                binding.fabActionSub.setVisibility(View.GONE);
//                startActivity(new Intent(MainActivity.this, StatusActivity.class));
                break;

            case 1 :
                binding.fabAction.show();
                binding.fabAction.setVisibility(View.VISIBLE);
                binding.fabActionSub.setVisibility(View.GONE);
                binding.fabAction.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_baseline_perm_contact_calendar_24, null));
                binding.fabAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                    }
                });
                break;

            case 2 :
                binding.fabAction.show();
                binding.fabAction.setVisibility(View.VISIBLE);
                binding.fabActionSub.setVisibility(View.VISIBLE);
                binding.fabAction.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_baseline_camera_alt_24, null));
                binding.fabAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, StatusActivity.class));
                    }
                });

                binding.fabActionSub.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_baseline_edit_24, null));
                binding.fabActionSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Edite Click", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case 3 :
                binding.fabAction.show();
                binding.fabAction.setVisibility(View.VISIBLE);
                binding.fabActionSub.setVisibility(View.VISIBLE);
                binding.fabAction.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_baseline_add_ic_call_24, null));
                binding.fabAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Chamada de Voz Click",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                binding.fabActionSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Chamada de Video Click",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                binding.fabActionSub.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_baseline_video_call_24, null));
                break;
        }
    }
}