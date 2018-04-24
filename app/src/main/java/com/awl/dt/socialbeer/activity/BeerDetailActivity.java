package com.awl.dt.socialbeer.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.awl.dt.socialbeer.FindBeerByIdQuery;
import com.awl.dt.socialbeer.R;
import com.awl.dt.socialbeer.RateBeerMutation;
import com.awl.dt.socialbeer.Utils;
import com.awl.dt.socialbeer.request.RequestHelper;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BeerDetailActivity extends AppCompatActivity implements RatingDialogListener{
    private static String TAG = BeerDetailActivity.class.getSimpleName();

    @BindView(R.id.beer_image) ImageView beer_img;
    @BindView(R.id.beerName) TextView beerName;
    @BindView(R.id.beerDesc) TextView beerDesc;
    @BindView(R.id.beerAbv) TextView beerAbv;
    @BindView(R.id.noteButton) Button noteButton;

    private int currentBeerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        currentBeerId = Integer.parseInt(intent.getExtras().getString(Utils.EXTRA_ID_BEER));

        performBeerRequest(currentBeerId);

        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    private void performBeerRequest(int id){
        FindBeerByIdQuery findBeerByIdQuery = FindBeerByIdQuery.builder().beerId(id).build();

        RequestHelper requestHelper = RequestHelper.getInstance();

        requestHelper.performQuery(findBeerByIdQuery, new ApolloCall.Callback<FindBeerByIdQuery.Data>() {
            @Override
            public void onResponse(@Nonnull final Response<FindBeerByIdQuery.Data> response) {
                Log.d(TAG, response.data().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FindBeerByIdQuery.FindBeer findBeer = response.data().findBeer().get(0);
                        Picasso.with(getApplicationContext()).load(findBeer.picture()).placeholder(getDrawable(R.drawable.icons8_beer)).error(getDrawable(R.drawable.icons8_beer)).into(beer_img);
                        beerName.setText(findBeer.beerName());
                        beerDesc.setText(findBeer.description());
                        beerAbv.setText(findBeer.abv()+"");
                    }
                });
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });
    }

    private void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(2)
                .setTitle("Rate this application")
                .setDescription("Please select some stars and give your feedback")
//                .setDefaultComment("This app is pretty cool !")
                .setStarColor(R.color.starColor)
                .setNoteDescriptionTextColor(R.color.noteDescriptionTextColor)
                .setTitleTextColor(R.color.titleTextColor)
                .setDescriptionTextColor(R.color.commentTextColor)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.hintTextColor)
                .setCommentTextColor(R.color.commentTextColor)
                .setCommentBackgroundColor(R.color.backgroundDialogColor)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(BeerDetailActivity.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(int rate, String comment) {

        RateBeerMutation rateBeerMutation = RateBeerMutation.builder().beerId(currentBeerId)
                .comment(comment)
                .rating(rate)
                .me(3).build();

        RequestHelper requestHelper = RequestHelper.getInstance();
        requestHelper.performMutation(rateBeerMutation, new ApolloCall.Callback() {
            @Override
            public void onResponse(@Nonnull Response response) {
                Log.d(TAG, response.data().toString());
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {

                Log.e(TAG, e.getMessage(), e);
            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}
