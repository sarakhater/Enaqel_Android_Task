package com.bestoffers.elnaqeltask.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bestoffers.elnaqeltask.R;
import com.bestoffers.elnaqeltask.models.Movie;
import com.bestoffers.elnaqeltask.views.FavouriteFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by SaraKhater on 1/12/19.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

    private  Movie[] moviesArrayList;
    private Context context;
    private LayoutInflater layoutInflater;
    private Realm mRealm;


    public FavouriteAdapter(Context context, Movie[] moviesArrayList ,  Realm realm ) {
        try {
            this.moviesArrayList = moviesArrayList;
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
            this.mRealm = realm;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.favourite_list_item, parent, false);
        FavouriteViewHolder favouriteViewHolder = new FavouriteViewHolder(view);

        return favouriteViewHolder;
    }


    @Override
    public void onBindViewHolder(FavouriteViewHolder holder, final int position) {
        try {
            holder.txt_title.setText(moviesArrayList[position].getTitle());
            holder.txt_date.setText(moviesArrayList[position].getReleased());
            String image_url = moviesArrayList[position].getPoster();
            if(moviesArrayList[position].getImdbRating() != null) {
                holder.ratingBar.setRating(Float.parseFloat(moviesArrayList[position].getImdbRating()));
            }
            Picasso.with(context)
                    .load(image_url)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(moviesArrayList != null){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, moviesArrayList[position].getTitle());
                    context.startActivity(Intent.createChooser(intent, "Share This Movie "));
                }
            }
        });
        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mRealm.where(Movie.class).equalTo("Title", moviesArrayList[position].getTitle())
                                    .findFirst()
                                    .deleteFromRealm();
                         RealmResults<Movie> movieRealmResults = mRealm.where(Movie.class).findAll();
                         moviesArrayList = movieRealmResults.toArray(new Movie[movieRealmResults.size()]);
                                    Handler mHandler = new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(Message message) {
                                    // This is where you do your work in the UI thread.

                                    notifyDataSetChanged();
                                }
                            };
                            Message message = mHandler.obtainMessage(0, null);
                            message.sendToTarget();
                        }
                    });

                }finally {
                    if(mRealm != null) {
                        mRealm.close();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {

            return moviesArrayList.length;

    }

    class FavouriteViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title , txt_date;
        ImageView imageView;
       ImageView btnShare, btnFav;
       RatingBar ratingBar;
        public FavouriteViewHolder(View itemView) {
            super(itemView);
            try {
                txt_title =  itemView.findViewById(R.id.txt_movie_title);
                txt_date = itemView.findViewById(R.id.txt_movie_date);
                imageView = itemView.findViewById(R.id.img_poster);
                btnShare = (ImageButton) itemView.findViewById(R.id.btn_share);
                btnFav = (ImageButton) itemView.findViewById(R.id.btn_remove_fav);
                ratingBar = itemView.findViewById(R.id.ratingBar);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
