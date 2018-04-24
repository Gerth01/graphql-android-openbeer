package com.awl.dt.socialbeer.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.awl.dt.socialbeer.BeerAdapter;
import com.awl.dt.socialbeer.FindBeerQuery;
import com.awl.dt.socialbeer.R;
import com.awl.dt.socialbeer.Utils;
import com.awl.dt.socialbeer.request.RequestHelper;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.lvbeers) ListView listView;
    @BindView(R.id.etSearch) EditText searchBeer;
    @BindView(R.id.account_button) ImageView accountButton;

    private BeerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        adapter = new BeerAdapter(this, new ArrayList<FindBeerQuery.FindBeer>());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailIntent = new Intent(getApplicationContext(), BeerDetailActivity.class);
                detailIntent.putExtra(Utils.EXTRA_ID_BEER, ((FindBeerQuery.FindBeer)adapterView.getItemAtPosition(i)).beerID());
                startActivity(detailIntent);
            }
        });
        searchBeer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                performBeerRequest(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent accountIntent = new Intent(getApplicationContext(), BeererActivity.class);
                startActivity(accountIntent);
            }
        });

    }



    void performBeerRequest(String name){

        RequestHelper requestHelper = RequestHelper.getInstance();
        FindBeerQuery findBeerQuery = FindBeerQuery.builder().beerName(name).build();
        requestHelper.performQuery(findBeerQuery, new ApolloCall.Callback<FindBeerQuery.Data>() {
            @Override
            public void onResponse(@Nonnull final Response<FindBeerQuery.Data> response) {
                Log.d(TAG, response.data().toString());

                // Get a handler that can be used to post to the main thread

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {

                        adapter.clear();
                        adapter.addAll(response.data().findBeer());

                        adapter.notifyDataSetChanged();
                    } // This is your code
                });

            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, e.getMessage(), e);

            }
        });
    }
}
