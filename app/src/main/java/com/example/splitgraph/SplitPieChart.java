package com.example.splitgraph;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class SplitPieChart {

    private final PieChart mChart;
    private final TextView mText;
    private final byte[] mCsvContents;
    private final Context mContext;

    public SplitPieChart(Context context, PieChart chart, TextView text, String name, byte[] csvContents) {

        this.mChart = chart;
        this.mChart.setCenterText(name);
        this.mCsvContents = csvContents;
        this.mText = text;
        this.mContext = context;
    }

    public void setPieChart(LocalDate fromDate) {
        CSVReaderHeaderAware csv;
        try {
            csv = new CSVReaderHeaderAware(new InputStreamReader(new ByteArrayInputStream(mCsvContents)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            csv.readMap();
        } catch (IOException | CsvValidationException ignored) {}

        Map<String, Float> entrieMap = new HashMap<>();
        while (true) {
            try {
                String[] currValues = csv.readNext("Category", "Cost", "Date");
                if (Objects.equals(currValues[0], "Payment")) {
                    continue;
                }
                if (Float.parseFloat(currValues[1]) < 50) {
                    currValues[0] = "Small Expenses";
                }
                if (fromDate != null) {
                    LocalDate entryDate = LocalDate.parse(currValues[2], DateTimeFormatter.ISO_LOCAL_DATE);
                    if (entryDate.isBefore(fromDate)) {
                        continue;
                    }
                }
                Float sum = entrieMap.get(currValues[0]);
                entrieMap.put(currValues[0], (sum == null ? 0 : sum) + Float.parseFloat(currValues[1]));
            } catch (IOException | CsvValidationException e) {
                break;
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        AtomicReference<Float> finalSum = new AtomicReference<>((float) 0);
        entrieMap.forEach((label, sum) -> {
            entries.add(new PieEntry(sum, label));
            finalSum.updateAndGet(v -> v + sum);
        });

        String name = (String) mChart.getCenterText();
        PieDataSet dataSet = new PieDataSet(entries, "My Chart");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(mChart));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.getDescription().setEnabled(false);
        mChart.setEntryLabelColor(Color.BLACK);
        mChart.setCenterText("Pie Chart");
        mChart.animateY(2000);
        mChart.setCenterText(name);
        mChart.invalidate(); // refresh

        mText.setText(String.format(Locale.getDefault(), mContext.getResources().getString(R.string.total_sum_f), finalSum.get()));
    }
}
