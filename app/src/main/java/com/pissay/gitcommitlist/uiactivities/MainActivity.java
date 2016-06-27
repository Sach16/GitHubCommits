package com.pissay.gitcommitlist.uiactivities;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.VolleyError;
import com.pissay.gitcommitlist.Adapters.CustomRecyclerAdapterForListOfCustomersByLoans;
import com.pissay.gitcommitlist.R;
import com.pissay.gitcommitlist.macros.Macros;
import com.pissay.gitcommitlist.models.GitModelData;
import com.pissay.gitcommitlist.network.Constants;
import com.pissay.gitcommitlist.network.RequestManager;
import com.pissay.gitcommitlist.uiframework.GitBaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends GitBaseActivity {

    @Nullable
    @Bind(R.id.RECYC_COMM_LIST)
    RecyclerView m_cRecyclerView;

    private CustomRecyclerAdapterForListOfCustomersByLoans m_cRecyclerAdapter;
    
    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    ArrayList<GitModelData> m_cModelList;
    private String m_cStatusGroup;
    int m_cPos;
    private static int page = 1;

    @Override
    protected void handleUIMessage(Message pObjMessage) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        m_cModelList = new ArrayList<>();
        m_cLayoutManager = new LinearLayoutManager(this);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        m_cRecyclerView.setLayoutManager(m_cLayoutManager);
        m_cRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = m_cLayoutManager.getChildCount();
                    totalItemCount = m_cLayoutManager.getItemCount();
                    pastVisiblesItems = m_cLayoutManager.findFirstVisibleItemPosition();

//                    int page = totalItemCount / 15;
                    if (m_cLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            m_cLoading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            int lpage = page + 1;
                            doPagination(lpage);
                        }
                    }
                }
            }
        });

        //Calling team api
        displayProgressBar(-1, "Loading");
        HashMap<String, String> llParams = new HashMap<>();
        RequestManager.getInstance(this).placeRequest(Constants.RAILS_RAILS, GitModelData[].class, this, llParams, false);

    }

    private void doPagination(int pPage) {
        m_cModelList.add(null);
        HashMap<String, String> lParams = new HashMap<>();
        lParams.put(Macros.PAGINATION_PAGE, "" + pPage);
        RequestManager.getInstance(this).placeRequest(Constants.RAILS_RAILS, GitModelData[].class, this, lParams, false);
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod) {
        switch (apiMethod) {
            case Constants.RAILS_RAILS:
                GitModelData[] lLeadCustHead = (GitModelData[]) response;
                ArrayList<GitModelData> lGitModelData = new ArrayList<GitModelData>(Arrays.asList(lLeadCustHead));
                if (m_cLoading) {
                    for (GitModelData lModel : lGitModelData) {
                        m_cModelList.add(lModel);
                    }
                    if (null != m_cModelList && m_cModelList.size() > 0) {
                        m_cRecyclerAdapter = new CustomRecyclerAdapterForListOfCustomersByLoans(this, m_cModelList);
                        m_cRecyclerView.setAdapter(m_cRecyclerAdapter);
                    }
                } else {
                    m_cModelList.remove(m_cModelList.size() - 1);
                    m_cRecyclerAdapter.notifyItemRemoved(m_cModelList.size());
                    for (GitModelData lModel : lGitModelData) {
                        m_cModelList.add(lModel);
                    }
                    m_cRecyclerAdapter.notifyItemInserted(m_cModelList.size());
                    m_cLoading = true;
                }
                hideDialog();
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod) {
        super.onErrorResponse(error, apiMethod);
        hideDialog();
    }
}
