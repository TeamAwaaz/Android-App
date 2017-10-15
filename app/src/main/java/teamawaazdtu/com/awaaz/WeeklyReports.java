package teamawaazdtu.com.awaaz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;

public class WeeklyReports extends AppCompatActivity implements OnChartValueSelectedListener {
                int naman;
    ArrayList<String> xVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_reports);

        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        yvalues.add(new Entry(13f, 0));
        yvalues.add(new Entry(20f, 1));
        yvalues.add(new Entry(17f, 2));
        yvalues.add(new Entry(28f, 3));
        yvalues.add(new Entry(22f, 4));

        PieDataSet dataSet = new PieDataSet(yvalues, "");


        xVals = new ArrayList<String>();

        xVals.add("Anger");
        xVals.add("Happy");
        xVals.add("Sad");
        xVals.add("Stressed");
        xVals.add("Excited");

        PieData data = new PieData(xVals, dataSet);
        pieChart.animateXY(1000,1000);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(14f);
        pieChart.setData(data);
        pieChart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        pieChart.setOnChartValueSelectedListener(this);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_weekly_reports);
        relativeLayout.getBackground().setAlpha(50);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;

        String xVal = xVals.get(e.getXIndex());
        Toast.makeText(this, xVal, Toast.LENGTH_SHORT).show();
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {

    }
}
