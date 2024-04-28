package com.example.splitgraph;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.splitgraph.databinding.ActivityMainBinding;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SpinnerOnSelectedListener {
    @SuppressWarnings("UnusedDeclaration") final private static String TAG = "MainActivity";
    private SplitPieChart mChart = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (Objects.equals(intent.getAction(), Intent.ACTION_SEND) && Objects.equals(intent.getType(), "text/csv")) {
            ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
            SpinnerViewModel model = new SpinnerViewModel();
            model.setmSpinnerOnSelectedListener(this);
            binding.setViewModel(model);
            binding.setLifecycleOwner(this);
            handleCsvFile(intent);
        }
        else {
            Button button = new Button(this);
            button.setText(R.string.open_splitwise);
            button.setOnClickListener(v -> {
                Intent intent1 = getPackageManager().getLaunchIntentForPackage("com.Splitwise.SplitwiseMobile");
                if (intent1 != null) {
                    startActivity(intent1);
                }
            });
            setContentView(button);
        }
    }

    private void handleCsvFile(Intent intent) {
        Uri sharedFileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

        String chartName = "Pie Chart";
        String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        if (subject != null) {
            chartName = subject.substring("Splitwise export for ".length());
        }

        if (sharedFileUri != null) {
            try {
                InputStream sharedFile = getContentResolver().openInputStream(sharedFileUri);
                if (sharedFile != null) {
                    mChart = new SplitPieChart(this, findViewById(R.id.pie_chart), findViewById(R.id.total_text), chartName, IOUtils.toByteArray(sharedFile));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onItemSelected(LocalDate fromDate) {
        if (mChart != null) {
            mChart.setPieChart(fromDate);
        }
    }
}