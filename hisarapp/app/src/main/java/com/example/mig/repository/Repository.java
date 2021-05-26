package com.example.mig.repository;

import android.content.SharedPreferences;

import com.example.mig.model.HisarResponse;
import com.example.mig.model.Place;
import com.example.mig.model.Poi;
import com.example.mig.network.ApiService;
import com.example.mig.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Repository {
    private static final String TAG = Repository.class.getSimpleName();

    ApiService apiService;
    //    HisarDao hisarDao;
    HisarResponse hisarResponse;
    SharedPreferences sharedPreferences;

    @Inject
    public Repository(ApiService apiService, HisarResponse hisarResponse, SharedPreferences sharedPreferences) {
        this.apiService = apiService;
        this.hisarResponse = hisarResponse;
        this.sharedPreferences = sharedPreferences;
    }

    public Observable<ResponseBody> getVersions() {
        return apiService.getVersions();
    }

    public Observable<Response<ResponseBody>> getFile() {
        return apiService.downloadFile();
    }

    public Observable<List<Place>> getAllPlacesList() {
        return Observable.create(emitter -> {
            try {
                List<Place> places;
                switch (Locale.getDefault().getLanguage()) {
                    case "en":
                        places = hisarResponse.getEn().getPlaceList();
                        break;
                    case "bg":
                        places = hisarResponse.getBg().getPlaceList();
                        break;
                    case "ro":
                        places = hisarResponse.getRo().getPlaceList();
                        break;
                    default:
                        places = hisarResponse.getEn().getPlaceList();
                }
                emitter.onNext(places);
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });

    }

    public Observable<List<Poi>> getPoisByType(String type) {
        return Observable.create(emitter -> {
            try {
                List<Poi> pois = new ArrayList<>();
                Place place;
                switch (Locale.getDefault().getLanguage()) {
                    case "en":
                        place = hisarResponse.getEn().getPlaceList().get(getCurrentPlaceId() - 1);
                        break;
                    case "bg":
                        place = hisarResponse.getBg().getPlaceList().get(getCurrentPlaceId() - 1);
                        break;
                    case "ro":
                        place = hisarResponse.getRo().getPlaceList().get(getCurrentPlaceId() - 1);
                        break;
                    default:
                        place = hisarResponse.getEn().getPlaceList().get(getCurrentPlaceId() - 1);
                }
                switch (type) {
                    case Constants.SIGHTS_KEY:
                        for (Poi poi : place.getPois().getSights()) {
                            if (poi.getType().equals(Constants.SIGHTS_KEY)) {
                                pois.add(poi);
                            }
                        }
                        break;
                    case Constants.REST_KEY:
                        for (Poi poi : place.getPois().getRestaurants()) {
                            if (poi.getType().equals("restaurant")) {
                                pois.add(poi);
                            }
                        }
                        break;
                    case Constants.HOTEL_KEY:
                        for (Poi poi : place.getPois().getHotels()) {
                            if (poi.getType().equals("hotel")) {
                                pois.add(poi);
                            }
                        }
                        break;
                }
                emitter.onNext(pois);
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });

    }

//    public Observable<ArrayList<Hotel>> getHotels() {
//        return Observable.create((ObservableOnSubscribe<ArrayList<Hotel>>) emitter -> {
//            try {
//                emitter.onNext(hisarResponse.getEn().getPlaceList().get(getCurrentPlaceId() - 1).getPois().getHotels());
//                emitter.onComplete();
//            } catch (Exception e) {
//                e.printStackTrace();
//                emitter.onError(e);
//            }
//        });
//
//    }
//
//    public Observable<ArrayList<Sight>> getSights() {
//        return Observable.create((ObservableOnSubscribe<ArrayList<Sight>>) emitter -> {
//            try {
//                emitter.onNext(hisarResponse.getEn().getPlaceList().get(getCurrentPlaceId() - 1).getPois().getSights());
//                emitter.onComplete();
//            } catch (Exception e) {
//                e.printStackTrace();
//                emitter.onError(e);
//            }
//        });
//
//    }

    public Observable<Place> getCurrentPlace(String langCode) {
        return Observable.create(emitter -> {
            try {
                Place place;
                switch (langCode) {
                    case "en":
                        place = hisarResponse.getEn().getPlaceList().get(getCurrentPlaceId() - 1);
                        break;
                    case "bg":
                        place = hisarResponse.getBg().getPlaceList().get(getCurrentPlaceId() - 1);
                        break;
                    case "ro":
                        place = hisarResponse.getRo().getPlaceList().get(getCurrentPlaceId() - 1);
                        break;
                    default:
                        place = hisarResponse.getEn().getPlaceList().get(getCurrentPlaceId() - 1);
                }
                emitter.onNext(place);
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });

    }

    public Observable<Place> getPlaceById(int id) {
        return Observable.create(emitter -> {
            try {
                Place place;
                switch (Locale.getDefault().getLanguage()) {
                    case "en":
                        place = hisarResponse.getEn().getPlaceList().get(id);
                        break;
                    case "bg":
                        place = hisarResponse.getBg().getPlaceList().get(id);
                        break;
                    case "ro":
                        place = hisarResponse.getRo().getPlaceList().get(id);
                        break;
                    default:
                        place = hisarResponse.getEn().getPlaceList().get(id);
                }
                emitter.onNext(place);
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });

    }

    public int getCurrentPlaceId() {
        return sharedPreferences.getInt("CURRENT_ID", -1);
    }

    public void setCurrentPlaceId(int id) {
        sharedPreferences.edit().putInt("CURRENT_ID", id).apply();
    }

    public void setCurrentFileVersion(int version) {
        sharedPreferences.edit().putInt("CURRENT_FILE_VERSION", version).apply();
    }

    public int getCurrentFileVersion() {
        return sharedPreferences.getInt("CURRENT_FILE_VERSION", -1);
    }

    public void setIsPlaceFav(int id, boolean isFav) {
        sharedPreferences.edit().putBoolean(String.valueOf(id), isFav).apply();
    }

    public boolean getIsPlaceFav(int id) {
        return sharedPreferences.getBoolean(String.valueOf(id), false);
    }

    public void setLanguageLocale(String locale) {
        sharedPreferences.edit().putString(Constants.LANG_KEY, locale).apply();
    }

    public String getLanguageLocale() {
        return sharedPreferences.getString(Constants.LANG_KEY, "AUTO");
    }

    public void setIntroSkipped(boolean skipped) {
        sharedPreferences.edit().putBoolean(Constants.INTRO_KEY, skipped).apply();
    }

    public boolean isIntroSkipped() {
        return sharedPreferences.getBoolean(Constants.INTRO_KEY, false);
    }
}
