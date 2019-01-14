package com.bestoffers.elnaqeltask.views;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bestoffers.elnaqeltask.R;
import com.bestoffers.elnaqeltask.adapters.FavouriteAdapter;
import com.bestoffers.elnaqeltask.models.Movie;
import io.realm.Realm;
import io.realm.RealmResults;


public class FavouriteFragment extends Fragment {


    RecyclerView favRecycleView;
    private Realm mRealm;
    RealmResults<Movie> movieRealmResults;
    Movie[] movieArrayList;
    FavouriteAdapter recyclerAdapter;

    public FavouriteFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        mRealm = Realm.getDefaultInstance();
        findViews(view);
        return view;

    }

    private void findViews(View view) {
        favRecycleView = view.findViewById(R.id.fav_recycle_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        favRecycleView.setLayoutManager(linearLayoutManager);
        try {
            movieRealmResults = mRealm.where(Movie.class).findAll();
            movieArrayList = movieRealmResults.toArray(new Movie[movieRealmResults.size()]);
            recyclerAdapter = new FavouriteAdapter(getActivity(), movieArrayList, mRealm);
            favRecycleView.setAdapter(recyclerAdapter);
        } finally {
            if (mRealm != null) {
                mRealm.close();
            }
        }

    }


}
