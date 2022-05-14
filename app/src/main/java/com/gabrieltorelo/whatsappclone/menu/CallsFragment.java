package com.gabrieltorelo.whatsappclone.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.adapter.CallListAdapter;
import com.gabrieltorelo.whatsappclone.adapter.ChatListAdapter;
import com.gabrieltorelo.whatsappclone.model.CallList;

import java.util.ArrayList;
import java.util.List;

public class CallsFragment extends Fragment {

    public CallsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);

        List<CallList> list = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        list.add(new CallList("1","Gabriel Torelo","08 de dezembro 13:50 pm","https://media-exp1.licdn.com/dms/image/C4E03AQGjhQN4bBRPrw/profile-displayphoto-shrink_200_200/0/1591388276525?e=1613001600&v=beta&t=x2co6TKr5tymBxko5A2EWMyXyV0K82wkizr2setnBhU","income"));
//        list.add(new CallList("2","Luciano Huck","01 de dezembro 01:20 am","https://duckduckgo.com/i/a1aadc0e.jpg","missed"));
//        list.add(new CallList("3","Clone WhatsApp","02 de novembro 15:13 pm","https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.searchpng.com%2Fwp-content%2Fuploads%2F2019%2F03%2FWhatsapp-Icon-PNG-1.png&f=1&nofb=1","out"));
//
//        recyclerView.setAdapter(new CallListAdapter(list, getContext()));

        return view;
    }
}