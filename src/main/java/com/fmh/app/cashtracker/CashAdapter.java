package com.fmh.app.cashtracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by ralf on 06.02.18.
 */

class CashAdapter extends RecyclerView.Adapter<CashAdapter.CashViewHolder> {

    private static final long FADE_DURATION = 500;
    private List<Cash> cashList;
    private CashAdapter.Listener mListener;
    private long _Cashid;
    private Context context;
    private SimpleDateFormat DateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public interface Listener { // create an interface
        void onItemClick(View view, int position); // create callback function
    }

    //constructor
    public CashAdapter(long Cashid, List<Cash> cashList, CashAdapter.Listener AdapterClickListener, Context context) {
        this.cashList = cashList;
        this.mListener = AdapterClickListener;
        this.context = context;
        this._Cashid = Cashid;
    }

    @Override
    public CashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cash_row, parent, false);
        return new CashViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CashViewHolder holder, final int position) {
        Cash _cash = cashList.get(position);

        holder.Beschreibung.setText(_cash.getContent());
        holder.Summe.setText(String.format("%.2f", _cash.getTotal()));
        holder.Datum.setText(DateFormat.format(_cash.getCreateDate()));
        String stringArray[] = context.getResources().getStringArray(R.array.repeat_arrays);
        holder.Repeat.setText(stringArray[_cash.getRepeat()]);

        // Set the view to fade in
        setFadeAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemClick(view, position);
                    }
                }
        );

        if (_Cashid == _cash.getCashID()){
            int iColor = context.getResources().getColor(R.color.colorTextDisabled);
            holder.Beschreibung.setTextColor(iColor);
            holder.Summe.setTextColor(iColor);
            holder.Datum.setTextColor(iColor);
            holder.Repeat.setTextColor(iColor);
        }

     }

    @Override
    public int getItemCount() {
        return cashList.size();
    }

    public class CashViewHolder extends RecyclerView.ViewHolder {
        public TextView Beschreibung, Summe, Datum, Repeat;

        public CashViewHolder(View v) {
            super(v);
            Beschreibung = v.findViewById(R.id.tvTitle);
            Summe = v.findViewById(R.id.tvSummary);
            Datum = v.findViewById(R.id.tvDateButton);
            Repeat = v.findViewById(R.id.tvRepeat);
        }
    }

    private void setFadeAnimation(View view, int position) {
        Animation animation = AnimationUtils.loadAnimation(this.context, R.anim.push_left_in);
        animation.setDuration(FADE_DURATION);
        //animation.setStartOffset(position * FADE_DURATION / 4);
        view.startAnimation(animation);
    }
}
