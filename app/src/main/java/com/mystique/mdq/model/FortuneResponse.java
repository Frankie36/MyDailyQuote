package com.mystique.mdq.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FortuneResponse {

    @SerializedName("fortune")
    @Expose
    private List<String> fortune = null;

    public List<String> getFortune() {
        return fortune;
    }

    public void setFortune(List<String> fortune) {
        this.fortune = fortune;
    }

}