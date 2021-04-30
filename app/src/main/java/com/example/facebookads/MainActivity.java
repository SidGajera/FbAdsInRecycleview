package com.example.facebookads;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.internal.ads;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ITEMS_PER_AD = 6;

    private RecyclerView recyclerView;
    private List<Object> recyclerViewItems = new ArrayList<>();
    String[] str = {"Android_1", "Android_2", "Android_3", "Android_4", "Android_5", "Android_6", "Android_7",
            "Android_8", "Android_9", "Android_10", "Android_11", "Android_12", "Android_13", "Android_14",
            "Android_15", "Android_16", "Android_17", "Android_18", "Android_19", "Android_20",
            "Android_21", "Android_22", "Android_23", "Android_24", "Android_25"};

    boolean canShowFullscreenAd;
    InterstitialAd interstitialAd;
    RewardedVideoAd rewardedVideoAd;
    Boolean isInterstial = false, isReward = false;

    private final String TAG = MainActivity.class.getName();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadAd();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        for (int i = 1; i <= 30; i++) {
            recyclerViewItems.add(new ModelClass("Android_" + i));
        }

//        for (int i = 0; i < str.length; i++) {
//            ModelClass modelClass = new ModelClass();
//            modelClass.setName(str[i]);
//            recyclerViewItems.add(modelClass);
//        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, recyclerViewItems, new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                ModelClass modelClass = (ModelClass) recyclerViewItems.get(pos);
                intent = new Intent(MainActivity.this, MainActivity2.class);

                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                String KEY_CLICK_COUNT = null;
                int clickCount = prefs.getInt(KEY_CLICK_COUNT, 0);
                if (clickCount % 5 == 0) {

                    if (isReward) {
                        rewardedVideoAd.show();
                        intent.putExtra("EXTRA_SESSION_ID", modelClass.getName());
                    } else if (isInterstial) {
                        interstitialAd.show();
                        intent.putExtra("EXTRA_SESSION_ID", modelClass.getName());
                    } else {
                        Log.e(TAG, "Ad not load");
                    }

                } else {
                    intent.putExtra("EXTRA_SESSION_ID", modelClass.getName());
                    startActivity(intent);
                }
                clickCount++;
                prefs.edit().putInt(KEY_CLICK_COUNT, clickCount).apply();
            }
        });
        recyclerView.setAdapter(adapter);

        addBannerAds();
        loadBannerAds();
    }

    public void loadAd() {
        if (!isReward) {
            LoadRewardedAD();
        }
        if (!isInterstial) {
            interstitial();
        }
    }

    private void LoadRewardedAD() {

        rewardedVideoAd = new RewardedVideoAd(this, "488593069224727_488595159224518");
        RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                Toast.makeText(MainActivity.this, "RewardedAd not loading the ad. Try again!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                isReward = true;
                Toast.makeText(MainActivity.this, "onRewardedAdLoaded()", Toast.LENGTH_SHORT).show();
//                rewardedVideoAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                Toast.makeText(MainActivity.this, "onAdClicked()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }

            @Override
            public void onRewardedVideoCompleted() {
                loadAd();
                isReward = false;
                Toast.makeText(MainActivity.this, "Ad completed, now give reward to user", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoClosed() {
                loadAd();
                isReward = false;
                Log.e(TAG, "LoadRewardedAD: adclose  ");
                Toast.makeText(MainActivity.this, "VideoClosed()", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        };
        isReward = false;
        rewardedVideoAd.loadAd(rewardedVideoAd.buildLoadAdConfig().withAdListener(rewardedVideoAdListener).build());
    }

    private void interstitial() {

        interstitialAd = new InterstitialAd(this, "488593069224727_488594119224622");
        Log.e(TAG, "onCreate: " + interstitialAd);

        AbstractAdListener adListener = new AbstractAdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                Toast.makeText(MainActivity.this, "InterstitialAd is loading : " + error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                super.onError(ad, error);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                super.onAdLoaded(ad);
                isInterstial = true;
                Toast.makeText(MainActivity.this, "onInterstitialAdLoaded()", Toast.LENGTH_SHORT).show();
//                if (canShowFullscreenAd) {
//                    interstitialAd.show();
//                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                super.onAdClicked(ad);
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                super.onInterstitialDisplayed(ad);
                Log.e(TAG, "interstitial: AdShow  ");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                super.onInterstitialDismissed(ad);
                loadAd();
                Log.e(TAG, "interstitial: AdDismissed  ");
                isInterstial = false;
                startActivity(intent);
            }
        };
        InterstitialAd.InterstitialLoadAdConfig interstitialLoadAdConfig = interstitialAd.buildLoadAdConfig()
                .withAdListener(adListener).build();
        interstitialAd.loadAd(interstitialLoadAdConfig);
    }

    private void addBannerAds() {

        for (int i = 0; i <= recyclerViewItems.size(); i += ITEMS_PER_AD) {
            AdView adView = new AdView(this, "488593069224727_488593585891342", AdSize.BANNER_HEIGHT_50);
            recyclerViewItems.add(i, adView);
            Log.e(TAG, "addBannerAds: recyclerViewItems ITEMS_PER_AD = "+i);
        }
    }

    private void loadBannerAds() {
        loadBannerAd(0);
    }

    private void loadBannerAd(final int index) {

        if (index >= recyclerViewItems.size()) {
            return;
        }

        Object item = recyclerViewItems.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad" + " ad.");
        }

        final AdView adView = (AdView) item;

        adView.loadAd();

        AdListener adListener = new AdListener() {

            @Override
            public void onAdLoaded(Ad ad) {
                Log.e(TAG, "onAdLoaded: " + ad);
                Toast.makeText(MainActivity.this, "onAdloaded()", Toast.LENGTH_SHORT).show();
                loadBannerAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "onError: " + adError.getErrorMessage());
                Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                Toast.makeText(MainActivity.this, "onAdClicked()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        };
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    @Override
    protected void onDestroy() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.destroy();
            }
        }
        super.onDestroy();
    }
}