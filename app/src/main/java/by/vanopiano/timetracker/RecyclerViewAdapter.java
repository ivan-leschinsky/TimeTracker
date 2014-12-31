package by.vanopiano.timetracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by De_Vano on 31 dec, 2014
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final Context mContext;
    private final String[] mDataset;

    public RecyclerViewAdapter(Context context, String[] dataset) {
        mContext = context;
        mDataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String[] values = mDataset[position].split(",");
        String countryName = values[0];
        String url = "http://lorempixel.com/800/600/sports/" + String.valueOf(position);
        viewHolder.mTextView.setText(countryName);
        Picasso.with(viewHolder.mImageView.getContext())
                .load(url)
                .into(viewHolder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.text);
            mImageView = (ImageView) v.findViewById(R.id.image);
        }
    }
}
