package com.pissay.gitcommitlist.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chandrasekhar on 22/6/16.
 */
public class GitModelData {

    @SerializedName("commit")
    @Expose
    private Commit commit;
    @SerializedName("author")
    @Expose
    private Author author;

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
