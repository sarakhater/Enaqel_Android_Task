package com.bestoffers.elnaqeltask.views;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestoffers.elnaqeltask.R;
import com.bestoffers.elnaqeltask.connection.ApiClient;
import com.bestoffers.elnaqeltask.connection.ApiInterface;
import com.bestoffers.elnaqeltask.connection.CheckNetwork;
import com.bestoffers.elnaqeltask.models.Movie;
import com.squareup.picasso.Picasso;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    //Define Variables:
    EditText edtSearch;
    Button btnSearch;
    TextView txt_title, txt_date;
    ImageView imageView;
    ImageView btnShare, btnFav;
    RatingBar ratingBar;
    ProgressBar progressBar;
    RelativeLayout searchLayout;
    Movie movie;
    private Realm mRealm;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        findViews(view);
        mRealm = Realm.getDefaultInstance();
        return view;
    }

    private void findViews(View view) {
        edtSearch = view.findViewById(R.id.edt_search);
        btnSearch = view.findViewById(R.id.btn_search);
        txt_title = view.findViewById(R.id.txt_movie_title);
        txt_date = view.findViewById(R.id.txt_movie_date);
        imageView = view.findViewById(R.id.img_poster);
        btnShare = view.findViewById(R.id.btn_share);
        btnFav = view.findViewById(R.id.btn_add_fav);
        ratingBar = view.findViewById(R.id.ratingBar);
        searchLayout = view.findViewById(R.id.search_layout);
        searchLayout.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBar);
        addListeners();

    }

    private void addListeners() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtSearch.getText().toString().isEmpty())
                    if (CheckNetwork.isConnectingToInternet(getActivity())) {
                        searchMovie();
                    } else {
                        Toast.makeText(getActivity(), " Please,Check Network", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movie != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, movie.getTitle());
                    startActivity(Intent.createChooser(intent, "Share This Movie "));
                }
            }
        });
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Object object = realm.where(Movie.class).equalTo("Title", movie.getTitle()).findFirst();

                            if (object == null) {
                                Number currentIdNum = realm.where(Movie.class).max("movieID");
                                int nextId;
                                if (currentIdNum == null) {
                                    nextId = 1;
                                } else {
                                    nextId = currentIdNum.intValue() + 1;
                                }
                                movie.setMovieID(nextId);
                                //...
                                realm.insertOrUpdate(movie);
                                Handler mHandler = new Handler(Looper.getMainLooper()) {
                                    @Override
                                    public void handleMessage(Message message) {
                                        // This is where you do your work in the UI thread.
                                        btnFav.setBackground(getResources().getDrawable(R.drawable.ic_star_full));
                                        Toast.makeText(getActivity(), "Movie Added to Favourite Successfully", Toast.LENGTH_SHORT).show();

                                    }
                                };
                                Message message = mHandler.obtainMessage(0, null);
                                message.sendToTarget();

                            } else {
                                Handler mHandler = new Handler(Looper.getMainLooper()) {
                                    @Override
                                    public void handleMessage(Message message) {
                                        // This is where you do your work in the UI thread.
                                        Toast.makeText(getActivity(), " This Movie already favourite", Toast.LENGTH_SHORT).show();

                                    }
                                };
                                Message message = mHandler.obtainMessage(0, null);
                                message.sendToTarget();

                            }
                        }
                    });
                } catch(Exception e){
                    e.printStackTrace();
                }



            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                btnFav.setBackground(getResources().getDrawable(R.drawable.ic_star_empty));

            }
        });
    }

    private void searchMovie() {

        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<Movie> Call = apiInterface.getMovies(edtSearch.getText().toString(), "a204dcd3");
        Call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {

                movie = response.body();
               if(movie != null && ! movie.getTitle().isEmpty()){
                   setData(movie);
               }else{
                   Toast.makeText(getActivity(), "No Movie exist with this title", Toast.LENGTH_SHORT).show();
               }
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void setData(Movie movie) {
        searchLayout.setVisibility(View.VISIBLE);
        txt_title.setText(movie.getTitle());
        txt_date.setText(movie.getReleased());
        String image_url = movie.getPoster();
        if (!movie.getImdbRating() .isEmpty()) {
            ratingBar.setRating(Float.parseFloat(movie.getImdbRating()));
        }
        if(!image_url.isEmpty()) {
            Picasso.with(getActivity())
                    .load(image_url)
                    .error(R.mipmap.ic_launcher)
                    .into(imageView);
        }

    }

}
