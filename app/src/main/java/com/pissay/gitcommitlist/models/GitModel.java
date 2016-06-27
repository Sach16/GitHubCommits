package com.pissay.gitcommitlist.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandrasekhar on 22/6/16.
 */
public class GitModel {

    @SerializedName("data")
    @Expose
    private List<GitModelData> data = new ArrayList<GitModelData>();

    /**
     *
     * @return
     * The data
     */
    public List<GitModelData> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<GitModelData> data) {
        this.data = data;
    }

}
