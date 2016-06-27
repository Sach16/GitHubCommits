package com.pissay.gitcommitlist.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pissay.gitcommitlist.R;
import com.pissay.gitcommitlist.models.GitModelData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by S.K. Pissay on 9/5/16.
 */
public class CustomRecyclerAdapterForListOfCustomersByLoans extends RecyclerView.Adapter{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static ArrayList<GitModelData> m_cObjJsonLoansCust;
    private Context m_cObjContext;

    public CustomRecyclerAdapterForListOfCustomersByLoans(Context pContext, ArrayList<GitModelData> pObjJsonLoansData){
        this.m_cObjJsonLoansCust = pObjJsonLoansData;
        this.m_cObjContext = pContext;
    }

    @Override
    public int getItemCount() {
        return m_cObjJsonLoansCust.size();
    }

    @Override
    public int getItemViewType(int position) {
        return m_cObjJsonLoansCust.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Nullable
        @Bind(R.id.NAME)
        TextView name;

        @Nullable
        @Bind(R.id.NAME_ID)
        TextView nameId;

        @Nullable
        @Bind(R.id.NAME_MESSAGE)
        TextView nameMsg;


        public DataObjectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.progressBar1)
        ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View lView;
        // paging logic
        if (viewType == VIEW_ITEM) {
            lView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.lead_list_cell, parent, false);
            DataObjectHolder ldataObjectHolder = new DataObjectHolder(lView);
            vh =  ldataObjectHolder;
        } else {
            lView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressdialog_paging, parent, false);
            ProgressViewHolder lprogressViewHolder = new ProgressViewHolder(lView);
            vh =  lprogressViewHolder;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof DataObjectHolder) {

            final GitModelData lObjCustomer = m_cObjJsonLoansCust.get(position);

            try {
                ((DataObjectHolder) holder).name.setText(lObjCustomer.getAuthor().getLogin());
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                ((DataObjectHolder) holder).nameId.setText(""+lObjCustomer.getAuthor().getId());
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                ((DataObjectHolder) holder).nameMsg.setText(lObjCustomer.getCommit().getMessage());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

}