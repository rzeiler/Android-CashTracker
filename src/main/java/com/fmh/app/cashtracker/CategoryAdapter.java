package com.fmh.app.cashtracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ralf on 01.02.18.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private static final long FADE_DURATION = 500;
    private List<Category> categoryList;
    private Listener mListener;
    private Context context;

    public interface Listener { // create an interface
        void onItemClick(View view, int position); // create callback function
    }

    //constructor
    public CategoryAdapter(List<Category> categoryList, Listener AdapterClickListener, Context context) {
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
        Category _category = categoryList.get(position);
        holder.title.setText(_category.getTitle());
        holder.genre.setText(String.valueOf(_category.getRating()));
        holder.year.setText(_category.getUser());
        // Set the view to fade in
        setFadeAnimation(holder.itemView);

        holder.title.setOnClickListener(
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
        public TextView title, year, genre;

        public CategoryViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            genre = v.findViewById(R.id.genre);
            year = v.findViewById(R.id.year);
        }
    }

    private void setFadeAnimation(View view) {
        Animation animation = AnimationUtils.loadAnimation(this.context, R.anim.push_left_in);
        animation.setDuration(FADE_DURATION);
        view.startAnimation(animation);
    }
}
