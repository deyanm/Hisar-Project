package com.example.mig.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mig.model.LocationResponse;
import com.example.mig.model.Place;
import com.example.mig.repository.Repository;
import com.example.mig.utils.DataWrapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prof.rssparser.Channel;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    private Repository repository;
    private MutableLiveData<ArrayList<Place>> placesList = new MutableLiveData<>();
    private MutableLiveData<LocationResponse> locationResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<DataWrapper> version = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> fileResponse = new MutableLiveData<>();
    private MutableLiveData<Place> place = new MutableLiveData<>();
    private MutableLiveData<Location> location = new MutableLiveData<>();
    private MutableLiveData<Channel> articleListLive = null;
    private String urlString = "https://news.google.com/rss/search?q=rimska+grobnica+hisarq&hl=bg&gl=BG&ceid=BG:bg";
    private MutableLiveData<String> snackbar = new MutableLiveData<>();

    @Inject
    public MainViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<Channel> getChannel() {
        if (articleListLive == null) {
            articleListLive = new MutableLiveData<>();
        }
        return articleListLive;
    }

    private void setChannel(Channel channel) {
        this.articleListLive.postValue(channel);
    }

    public LiveData<String> getSnackbar() {
        return snackbar;
    }

    public void onSnackbarShowed() {
        snackbar.setValue(null);
    }

    public void fetchFeed() {

        Parser parser = new Parser.Builder().build();

        parser.onFinish(new OnTaskCompleted() {

            @Override
            public void onTaskCompleted(@NonNull Channel channel) {
                setChannel(channel);
            }

            @Override
            public void onError(@NonNull Exception e) {
                setChannel(new Channel(null, null, null, null, null, null, new ArrayList<>()));
                e.printStackTrace();
                snackbar.postValue("An error has occurred. Please try again");
            }
        });
        parser.execute(urlString);
    }

    public MutableLiveData<ResponseBody> getFileResponse() {
        return fileResponse;
    }

    public MutableLiveData<ArrayList<Place>> getPlacesList() {
        return placesList;
    }

    public MutableLiveData<DataWrapper> getVersion() {
        return version;
    }

    public MutableLiveData<LocationResponse> getCurrentWeatherObserver() {
        return locationResponseMutableLiveData;
    }

//    public void getPlaces() {
//        repository.getPlacesList()
//                .subscribeOn(Schedulers.io())
//                .map(placeResponse -> placeResponse)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(result -> placesList.setValue(result),
//                        error -> Log.e(TAG, "error getting places " + error.getMessage()));
//    }

    public void getCurrentWeather(String city) {
        repository.getCurrentWeather(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> locationResponseMutableLiveData.setValue(result),
                        error -> {
                            locationResponseMutableLiveData.setValue(new LocationResponse(error.getLocalizedMessage()));
                        });
    }

    public void getVersions() {
        repository.getVersions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(responseBody -> {
                    JsonObject jsonObject = new JsonParser().parse(responseBody.string()).getAsJsonObject();
                    return Observable.just(jsonObject.get("version").getAsInt());
                })
                .subscribe(
                        result -> version.setValue(new DataWrapper(result, null)),
                        error -> {
                            version.setValue(new DataWrapper(null, error.getMessage()));
                            Log.e(TAG, "error getting db file version: " + error.getMessage());
                        });
    }

    public void downloadFile() {
        repository.getFile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(responseBody -> Observable.just(responseBody.body()))
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        fileResponse.setValue(responseBody);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        Log.d(TAG, "Error " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onCompleted downloading the file");
                    }
                });
    }

    public int getCurrentPlaceId() {
        return repository.getCurrentPlaceId();
    }

    public void setCurrentPlaceId(int id) {
        repository.setCurrentPlaceId(id);
    }

    public int getCurrentFileVersion() {
        return repository.getCurrentFileVersion();
    }

    public void setCurrentFileVersion(int version) {
        repository.setCurrentFileVersion(version);
    }

    public MutableLiveData<Place> getPlace() {
        return place;
    }

    public void getCurrentPlace(String langCode) {
        repository.getCurrentPlace(langCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(returnPlace -> place.setValue(returnPlace), error -> Log.d(TAG, "error getting place " + error.getMessage()));
    }

    public MutableLiveData<Location> getLocation() {
        return location;
    }

    public String getLangLocale() {
        return repository.getLanguageLocale();
    }
}