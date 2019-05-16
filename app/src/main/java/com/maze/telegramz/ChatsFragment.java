package com.maze.telegramz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import androidx.recyclerview.widget.DividerItemDecoration;

import java.util.ArrayList;

import static com.maze.telegramz.ChatsAdapter.createChatsArrayList;
import static com.maze.telegramz.HomeActivity.ic;
import static com.maze.telegramz.Telegram.client;
import static com.maze.telegramz.Telegram.getChatList;
import static com.maze.telegramz.Telegram.setMe;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView chatsRecyclerView;
    public static RecyclerView.Adapter chatsAdapter;
    private RecyclerView.LayoutManager chatsLayoutManager;
    private OnFragmentInteractionListener mListener;
    public static ArrayList<ChatsItem> chatsArrayList = new ArrayList<>();

    public ChatsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
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
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
//        getChatList(200);
        chatsArrayList = createChatsArrayList();
        chatsRecyclerView = view.findViewById(R.id.chatsRecycler);
        chatsRecyclerView.setHasFixedSize(true);
        chatsLayoutManager = new LinearLayoutManager(container.getContext());
        chatsAdapter = new ChatsAdapter(chatsArrayList);
        chatsRecyclerView.setLayoutManager(chatsLayoutManager);
        chatsRecyclerView.setAdapter(chatsAdapter);
        chatsRecyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), LinearLayoutManager.VERTICAL));
        chatsRecyclerView.setAdapter(chatsAdapter);
        ic.refreshChatsRecycler();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
