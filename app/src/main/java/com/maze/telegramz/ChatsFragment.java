package com.maze.telegramz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DividerItemDecoration;

import java.util.ArrayList;

import static com.maze.telegramz.ChatsAdapter.populateChatsArrayList;
import static com.maze.telegramz.ChatsAdapter.prefetchAllChatsLastMessages;
import static com.maze.telegramz.HomeActivity.ic;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment implements ChatsAdapter.OnChatClickListener {


    private RecyclerView chatsRecyclerView;
    public static RecyclerView.Adapter chatsAdapter;
    private RecyclerView.LayoutManager chatsLayoutManager;
    private OnFragmentInteractionListener mListener;
    public static ArrayList<ChatsItem> chatsArrayList = new ArrayList<>();

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        chatsArrayList = new ArrayList<>();
        chatsRecyclerView = view.findViewById(R.id.chatsRecycler);

        chatsLayoutManager = new LinearLayoutManager(container.getContext());
        chatsAdapter = new ChatsAdapter(chatsArrayList, this);
        chatsRecyclerView.setLayoutManager(chatsLayoutManager);
        chatsRecyclerView.setAdapter(chatsAdapter);
        chatsRecyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), LinearLayoutManager.VERTICAL));
        chatsRecyclerView.setAdapter(chatsAdapter);
        populateChatsArrayList(chatsArrayList);
        ic.refreshChatsRecycler();
        prefetchAllChatsLastMessages(chatsArrayList);
        return view;
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

    @Override
    public void onClick(int pos) {
        ChatsItem clickedItem = chatsArrayList.get(pos);
        Intent intent = new Intent(this.getContext(), ConvoActivity.class);
        intent.putExtra("title",clickedItem.getTitle());
        intent.putExtra("id",clickedItem.getId());
        startActivity(intent);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
