package com.example.computer.androidcartserver.Model;

import java.util.List;

public class MyResponse {
    public Long multicast_id;
    public int success;
    public int failure;
    public int canonical_ads;
    public List<Result> results;

    public MyResponse() {
    }

    public MyResponse(Long multicast_id, int success, int failure, int canonical_ads, List<Result> results) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.canonical_ads = canonical_ads;
        this.results = results;
    }

    public Long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(Long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonical_ads() {
        return canonical_ads;
    }

    public void setCanonical_ads(int canonical_ads) {
        this.canonical_ads = canonical_ads;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
