package com.awl.dt.socialbeer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.awl.dt.socialbeer.FindBeererByIDQuery;
import com.awl.dt.socialbeer.R;
import com.awl.dt.socialbeer.request.RequestHelper;
import com.squareup.picasso.Picasso;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BeererActivity extends AppCompatActivity {

    private static String TAG = BeererActivity.class.getSimpleName();

    @BindView(R.id.account_image) ImageView accountPic;
    @BindView(R.id.account_name) TextView accountName;
    @BindView(R.id.account_desc) TextView accountDesc;
    @BindView(R.id.home_button) ImageView homeButton;

    @BindView(R.id.rateLayout) LinearLayout rateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beerer);

        ButterKnife.bind(this);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent accountIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(accountIntent);
            }
        });

        performBeererRequest();
    }

    private void performBeererRequest(){
        FindBeererByIDQuery findBeererByIDQuery = FindBeererByIDQuery.builder().beererId(3).build();

        RequestHelper requestHelper = RequestHelper.getInstance();
        requestHelper.performQuery(findBeererByIDQuery, new ApolloCall.Callback<FindBeererByIDQuery.Data>() {
            @Override
            public void onResponse(@Nonnull final Response<FindBeererByIDQuery.Data> response) {
                Log.d(TAG, response.data().toString());

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {

                        FindBeererByIDQuery.FindBeererById beerer = response.data().findBeererById();

                        Picasso.with(getApplicationContext()).load(beerer.picture()).into(accountPic);
                        accountName.setText(beerer.beererName());
                        accountDesc.setText(beerer.description());

                        for (final FindBeererByIDQuery.Rated rated : beerer.rated()) {
                            View child = getLayoutInflater().inflate(R.layout.item_rated_beer, null);
                            ImageView imageView = child.findViewById(R.id.beerImg);
                            Picasso.with(getApplicationContext()).load(rated.beer().picture()).placeholder(getDrawable(R.drawable.icons8_beer)).error(getDrawable(R.drawable.icons8_beer)).into(imageView);

                            child.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    createRatedDialog(rated);
                                }
                            });

                            rateLayout.addView(child);

                        }

                    }
                });
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });
    }


    private void createRatedDialog(FindBeererByIDQuery.Rated rated){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View child = inflater.inflate(R.layout.dialog_beer_rate, null);

        ImageView imageView = child.findViewById(R.id.beerImg);

        Picasso.with(getApplicationContext()).load(rated.beer().picture()).placeholder(getDrawable(R.drawable.icons8_beer)).error(getDrawable(R.drawable.icons8_beer)).into(imageView);

        TextView textView = child.findViewById(R.id.beerName);
        textView.setText(rated.beer().beerName());

        TextView ratedTextView = child.findViewById(R.id.rate_note);
        ratedTextView.setText(rated.rate().rating() + "");

        TextView commentTv = child.findViewById(R.id.rate_comment);
        commentTv.setText(rated.rate().comment());

        dialog.setView(child);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();
    }
}
