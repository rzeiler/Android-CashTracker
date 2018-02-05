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
 * Created by ralf on 05.02.18.
 */

class CategoryDetailsAdapter extends RecyclerView.Adapter<CategoryDetailsAdapter.CategoryViewHolder> {

    private static final long FADE_DURATION = 500;
    private List<CategorySum> _categorySumList;
    private Context context;

    //constructor
    public CategoryDetailsAdapter(List<CategorySum> categorySumList, Context context) {
        this._categorySumList = categorySumList;

        this.context = context;
    }

    @Override
    public CategoryDetailsAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_detail_row, parent, false);
        return new CategoryDetailsAdapter.CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryDetailsAdapter.CategoryViewHolder holder, final int position) {
        CategorySum _categorySumItem = _categorySumList.get(position);

        holder.tvDate.setText(_categorySumItem.getMonth());
        holder.tvSum.setText(String.format("%.2f €", _categorySumItem.getTotal()));

        // Set the view to fade in
        setFadeAnimation(holder.itemView);


    }

    @Override
    public int getItemCount() {
        return _categorySumList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvSum;

        public CategoryViewHolder(View v) {
            super(v);

            tvDate = v.findViewById(R.id.tvDate);
            tvSum = v.findViewById(R.id.tvSum);
        }
    }

    private void setFadeAnimation(View view) {
        Animation animation = AnimationUtils.loadAnimation(this.context, R.anim.push_left_in);
        animation.setDuration(FADE_DURATION);
        view.startAnimation(animation);
    }

}
