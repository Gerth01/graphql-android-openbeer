package com.awl.dt.socialbeer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BeerAdapter extends ArrayAdapter<FindBeerQuery.FindBeer> {

    public BeerAdapter(@NonNull Context context,  @NonNull List<FindBeerQuery.FindBeer> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FindBeerQuery.FindBeer beer = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_beer, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvName = convertView.findViewById(R.id.tvType);
        TextView tvHome = convertView.findViewById(R.id.tvDesc);
        ImageView imageView = convertView.findViewById(R.id.ivPaymentType);

        Picasso.with(getContext()).load(beer.picture).placeholder(getContext().getDrawable(R.drawable.icons8_beer)).error(getContext().getDrawable(R.drawable.icons8_beer)).into(imageView);

        // Populate the data into the template view using the data object
        tvTitle.setText(beer.beerName());
        tvName.setText(beer.description());
        if (beer.category != null)
            tvHome.setText(beer.category.categoryName());
        else
            tvHome.setText("no category");

        // Return the completed view to render on screen
        return convertView;
    }
}
