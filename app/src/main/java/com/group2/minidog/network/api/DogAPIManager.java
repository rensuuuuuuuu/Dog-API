package com.group2.minidog.network.api;

import androidx.annotation.NonNull;

import com.group2.minidog.model.DogAPIModel;
import com.group2.minidog.network.App;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DogAPIManager implements DogAPIManagerI {

    @Inject
    public Retrofit retrofit;
    private final DogAPICalls dogAPICalls;
    private final DogAPIManagerListeners listeners;

    public DogAPIManager(DogAPIManagerListeners listeners) {
        App.getAppComponent().inject(this);
        this.dogAPICalls = retrofit.create(DogAPICalls.class);
        this.listeners = listeners;
    }

    @Override
    public void getAllDogs(){
        Call<ArrayList<DogAPIModel>> call = dogAPICalls.getAllDogs();
        call.enqueue(new Callback<ArrayList<DogAPIModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DogAPIModel>> call, @NonNull Response<ArrayList<DogAPIModel>> response) {
                ArrayList<DogAPIModel> dogAPIModels = new ArrayList<>();
                if (response.body() != null) {
                    dogAPIModels = response.body();
                }
                listeners.onSuccess(dogAPIModels);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<DogAPIModel>> call, @NonNull Throwable t) {
                listeners.onFail(t);
            }
        });
    }

    @Override
    public void getDogsFromAPage(/*String limit, String page*/){
        String limit = "10", page = "1";
        Call<ArrayList<DogAPIModel>> call = dogAPICalls.getDogsFromAPage(limit, page);
        call.enqueue(new Callback<ArrayList<DogAPIModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DogAPIModel>> call, @NonNull Response<ArrayList<DogAPIModel>> response) {
                ArrayList<DogAPIModel> dogAPIModels = new ArrayList<>();
                if (response.body() != null) {
                    dogAPIModels = response.body();
                }
                listeners.onSuccess(dogAPIModels);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<DogAPIModel>> call, @NonNull Throwable t) {
                listeners.onFail(t);
            }
        });
    }

    @Override
    public void searchDog(String name){
        Call<ArrayList<DogAPIModel>> call = dogAPICalls.searchDog(name);
        call.enqueue(new Callback<ArrayList<DogAPIModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DogAPIModel>> call, @NonNull Response<ArrayList<DogAPIModel>> response) {
                ArrayList<DogAPIModel> dogAPIModels = new ArrayList<>();
                if (response.body() != null) {
                    dogAPIModels = response.body();
                }
                listeners.onSuccess(dogAPIModels);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<DogAPIModel>> call, @NonNull Throwable t) {
                listeners.onFail(t);
            }
        });
    }
}
