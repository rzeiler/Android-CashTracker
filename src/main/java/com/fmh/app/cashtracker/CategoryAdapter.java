package com.fmh.app.cashtracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fmh.app.cashtracker.Models.Category;

import java.util.List;

/**
 * Created by ralf on 01.02.18.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private static final long FADE_DURATION = 500;
    private List<?> categoryList;
    private Listener mListener;
    private Context context;

    public interface Listener { // create an interface
        void onItemClick(View view, int position); // create callback function
    }

    //constructor
    public CategoryAdapter(List<?> categoryList, Listener AdapterClickListener, Context context) {
        this.categoryList = categoryList;
        this.mListener = AdapterClickListener;
        this.context = context;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_row, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.CategoryViewHolder holder, final int position) {
        Category _category = (Category) categoryList.get(position);

        String bl = _category.getTitle();
        holder.tvTitle.setText(bl);
        if (bl.length() > 0) {
            bl = bl.substring(0, 1).toUpperCase();
            holder.tvBigletter.setText(bl);
        }
        holder.tvSum.setText(String.format("%.2f €", _category.getTotal()));
        holder.ratingBar.setRating(_category.getRating());



        // Set the view to fade in
        setFadeAnimation(holder.itemView, position);

        holder.tvBigletter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemClick(view, position);
                    }
                }
        );

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemClick(view, position);
                    }
                }
        );

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBigletter, tvTitle, tvSum;
        public RatingBar ratingBar;

        public CategoryViewHolder(View v) {
            super(v);
            tvBigletter = v.findViewById(R.id.tvBigletter);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvSum = v.findViewById(R.id.tvSum);
            ratingBar = v.findViewById(R.id.ratingBar);
        }
    }

    private void setFadeAnimation(View view, int position) {
        Animation animation = AnimationUtils.loadAnimation(this.context, R.anim.push_left_in);
        animation.setDuration(FADE_DURATION);
        //animation.setStartOffset(position * FADE_DURATION / 4);
        view.startAnimation(animation);
    }

}
