package com.example.splitgraph;

import androidx.databinding.BindingAdapter;

import com.github.mikephil.charting.charts.PieChart;

public class PieChartBindingAdapter {
    @BindingAdapter("legendEnabled")
    public static void setLegendEnabled(PieChart chart, boolean enabled) {
        chart.getLegend().setEnabled(enabled);
    }
}