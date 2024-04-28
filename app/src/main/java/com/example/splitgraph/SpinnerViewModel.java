package com.example.splitgraph;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpinnerViewModel extends ViewModel {

    private final MutableLiveData<List<String>> mSpinnerItems = new MutableLiveData<>();
    private SpinnerOnSelectedListener mSpinnerOnSelectedListener;

    public SpinnerViewModel() {
        List<String> items = new ArrayList<>();
        items.add("All Time");
        items.add("Last Year");
        items.add("Last Month");
        mSpinnerItems.setValue(items);
    }

    public void setmSpinnerOnSelectedListener(SpinnerOnSelectedListener spinnerOnSelectedListener) {
        this.mSpinnerOnSelectedListener = spinnerOnSelectedListener;
    }

    public MutableLiveData<List<String>> getSpinnerItems() {
        return mSpinnerItems;
    }

    public void onItemSelected(int position) {
        switch (Objects.requireNonNull(mSpinnerItems.getValue()).get(position)) {
            case "Last Year":
                mSpinnerOnSelectedListener.onItemSelected(LocalDate.now().minusYears(1));
                break;
            case "Last Month":
                mSpinnerOnSelectedListener.onItemSelected(LocalDate.now().minusMonths(1));
                break;
            default:
                mSpinnerOnSelectedListener.onItemSelected(null);
                break;
        }
    }
}

