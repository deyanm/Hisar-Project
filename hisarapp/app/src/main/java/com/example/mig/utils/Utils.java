package com.example.mig.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class Utils {

    public static int dp2px(Resources resource, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp, resource.getDisplayMetrics()
        );
    }

    public static float px2dp(Resources resource, float px) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                px,
                resource.getDisplayMetrics()
        );
    }

    public static void showSnackbar(Activity activity, int mainText, int actionString,
                                    View.OnClickListener listener) {
        Snackbar.make(
                activity.findViewById(android.R.id.content),
                activity.getString(mainText),
                Snackbar.LENGTH_LONG)
                .setAction(activity.getString(actionString), listener).show();
    }

    //    public void getPlaceFromUtil(Context context, int id) {
//        Utils.getJsonFromAppFolder(context)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(places -> place.setValue(places.get(0)), error -> Log.d(TAG, "error getting place " + error.getMessage()));
//    }

//    public static Observable<List<Place>> getJsonFromAppFolder(Context context) {
//        return Observable.create((ObservableOnSubscribe<List<Place>>) emitter -> {
//            String yourFilePath = context.getFilesDir() + "/" + Constants.jsonFileName;
//
//            Gson gson = new Gson();
//            BufferedReader br;
//            try {
//                br = new BufferedReader(new FileReader(yourFilePath));
//                Type type = new TypeToken<HisarResponse>(){}.getType();
//                HisarResponse hisarResponse = gson.fromJson(br, type);
//                List<Place> places;
//                switch (Locale.getDefault().getLanguage()) {
//                    case "en":
//                        places = hisarResponse.getEn().getPlaceList();
//                        break;
//                    case "bg":
//                        places = hisarResponse.getBg().getPlaceList();
//                        break;
//                    case "ro":
//                        places = hisarResponse.getRo().getPlaceList();
//                        break;
//                    default:
//                        places = hisarResponse.getEn().getPlaceList();
//                }
//                emitter.onNext(places);
//                emitter.onComplete();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                emitter.onError(e);
//            }
//        });
//
//    }

    public static void checkLocale(Activity activity, String locale) {
        if (!locale.equals("AUTO")) {
            Locale currentAppLocale = new Locale(locale);
            Locale.setDefault(currentAppLocale);
            Resources resources = activity.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(currentAppLocale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }

}
