package com.example.mig.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.mig.R;
import com.example.mig.adapters.SliderAdapter;
import com.example.mig.databinding.ActivityIntroBinding;
import com.example.mig.model.SliderModal;
import com.example.mig.utils.Utils;
import com.example.mig.viewmodel.IntroViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class IntroActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLL;
    private SliderAdapter adapter;
    private ArrayList<SliderModal> sliderModalArrayList;
    private TextView[] dots;
    private int size;
    private Button nextBtn, skipBtn;
    private ActivityIntroBinding binding;
    private IntroViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(IntroViewModel.class);
        if (viewModel.isIntroSkipped()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        Utils.checkLocale(this, viewModel.getLangLocale());
        setContentView(binding.getRoot());

        // initializing all our views.
        viewPager = findViewById(R.id.idViewPager);
        dotsLL = findViewById(R.id.idLLDots);
        nextBtn = findViewById(R.id.btnNext);
        nextBtn.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == 2) {
                viewModel.setIntroSkipped(true);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });
        skipBtn = findViewById(R.id.idBtnSkip);
        skipBtn.setOnClickListener(view -> {
            viewModel.setIntroSkipped(true);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // in below line we are creating a new array list.
        sliderModalArrayList = new ArrayList<>();

        // on below 3 lines we are adding data to our array list.
        sliderModalArrayList.add(new SliderModal(getString(R.string.slide_1_title), getString(R.string.slide_1_text)));
        sliderModalArrayList.add(new SliderModal(getString(R.string.slide_2_title), getString(R.string.slide_2_text)));
        sliderModalArrayList.add(new SliderModal(getString(R.string.slide_3_title), getString(R.string.slide_3_text)));

        // below line is use to add our array list to adapter class.
        adapter = new SliderAdapter(IntroActivity.this, sliderModalArrayList);

        // below line is use to set our
        // adapter to our viw pager.
        viewPager.setAdapter(adapter);

        // we are storing the size of our
        // array list in a variable.
        size = sliderModalArrayList.size();

        // calling method to add dots indicator
        addDots(size, 0);

        // below line is use to call on
        // page change listener method.
        viewPager.addOnPageChangeListener(viewListner);
    }

    private void addDots(int size, int pos) {
        // inside this method we are
        // creating a new text view.
        dots = new TextView[size];

        // below line is use to remove all
        // the views from the linear layout.
        dotsLL.removeAllViews();

        // running a for loop to add
        // number of dots to our slider.
        for (int i = 0; i < size; i++) {
            // below line is use to add the
            // dots and modify its color.
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("•"));
            dots[i].setTextSize(35);

            // below line is called when the dots are not selected.
            dots[i].setTextColor(getResources().getColor(R.color.black));
            dotsLL.addView(dots[i]);
        }
        if (dots.length > 0) {
            // this line is called when the dots
            // inside linear layout are selected
            dots[pos].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    // creating a method for view pager for on page change listener.
    ViewPager.OnPageChangeListener viewListner = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == 2) {
                nextBtn.setText(getString(R.string.slide_button2));
            } else {
                nextBtn.setText(getString(R.string.next));
            }
        }

        @Override
        public void onPageSelected(int position) {
            // we are calling our dots method to
            // change the position of selected dots.
            addDots(size, position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}