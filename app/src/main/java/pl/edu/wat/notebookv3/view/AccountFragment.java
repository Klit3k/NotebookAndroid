package pl.edu.wat.notebookv3.view;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.slider.LabelFormatter;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.repository.FirebaseUserRepository;
import pl.edu.wat.notebookv3.viewmodel.AccountViewModel;
import pl.edu.wat.notebookv3.viewmodel.DashboardViewModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class AccountFragment extends Fragment {
    private AccountViewModel viewModel;
    private AccountFragmentArgs args;
    public static final int[] COLOR = {
            Color.rgb(92,182,80), Color.rgb(139,95,202), Color.rgb(	166,179,70),
            Color.rgb(191,70,138), Color.rgb(77,181,152), Color.rgb(	203,84,44),
            Color.rgb(101,136,202), Color.rgb(203,150,60), Color.rgb(206,130,192),
            Color.rgb(98,122,55), Color.rgb(205,74,91), Color.rgb(191,120,89)
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        args = AccountFragmentArgs.fromBundle(getArguments());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy, HH:mm:ss");
        List<LocalDateTime> localDateTimes = new ArrayList<>();
        HashMap<Month, Integer> info = new HashMap<>();

        TextView email = view.findViewById(R.id.email_text);
        email.setText(Html.fromHtml(String.format("Email: <b>%s</b>", viewModel.getEmail())));
        TextView lastDate = view.findViewById(R.id.last_note_text);
        lastDate.setText(Html.fromHtml(String.format("Last note: <b>%s</b>", args.getLastNoteDate())));

        PieChart chart  = view.findViewById(R.id.barChart);
        viewModel.getTimes().observe(getViewLifecycleOwner(), x -> {
            Log.d("Test", String.valueOf(x.size()));
            for (String dateStr : x) {
                LocalDate localDate = LocalDateTime.parse(dateStr, formatter).toLocalDate();

                if(info.get(localDate.getMonth()) == null ) {
                    info.put(localDate.getMonth(), 1);
                } else {
                    info.put(localDate.getMonth(), info.get(localDate.getMonth())+1);
                }
            }

            List<PieEntry> entries = new ArrayList<>();
            for (Map.Entry<Month, Integer> entry : info.entrySet()) {
                Month date = entry.getKey();
                int value = entry.getValue();
                if (value != 0) {
                    entries.add(new PieEntry(value, date.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())));
                }
            }

            PieDataSet dataSet = new PieDataSet(entries, null);
//            dataSet.setGradientColor(Color.rgb(193, 37, 82), Color.rgb(179, 100, 53));
            dataSet.setColors(COLOR);
            dataSet.setSliceSpace(1f);


            PieData data = new PieData(dataSet);
            data.setValueFormatter(new DefaultValueFormatter(0));
            data.setValueTextSize(18);
            data.setDrawValues(true);
            data.setValueTextColor(Color.WHITE);
            data.setValueTypeface(Typeface.MONOSPACE);
            data.setValueTextSize(15f);

            chart.setHighlightPerTapEnabled(false);
            chart.animateY(1400, Easing.EaseOutBack);
            chart.setRotationEnabled(false);
            chart.setData(data);
            chart.getDescription().setEnabled(true);
            chart.setDrawEntryLabels(true);
            chart.setCenterText(String.format("Your activities \n("+x.size()+")"));
            chart.setHoleRadius(40f);
            chart.setTransparentCircleRadius(55f);
            chart.getLegend().setWordWrapEnabled(true);
            chart.getDescription().setEnabled(false);


//            Legend legend = chart.getLegend();
//            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//            legend.setForm(Legend.LegendForm.CIRCLE );
//            legend.setFormSize(12f);
//            legend.setDrawInside(false);
//            legend.setTextSize(15f);
//            legend.setXEntrySpace(30f);
            chart.getLegend().setEnabled(false);

            chart.invalidate();
        });
        return view;
    }
}