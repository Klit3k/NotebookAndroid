package pl.edu.wat.notebookv3.view;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.util.AlarmReceiver;
import pl.edu.wat.notebookv3.viewmodel.AccountViewModel;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static android.content.Context.ALARM_SERVICE;

public class AccountFragment extends Fragment {
    private AccountViewModel viewModel;

    public static final int[] COLOR = {
            Color.rgb(92, 182, 80), Color.rgb(139, 95, 202), Color.rgb(166, 179, 70),
            Color.rgb(191, 70, 138), Color.rgb(77, 181, 152), Color.rgb(203, 84, 44),
            Color.rgb(101, 136, 202), Color.rgb(203, 150, 60), Color.rgb(206, 130, 192),
            Color.rgb(98, 122, 55), Color.rgb(205, 74, 91), Color.rgb(191, 120, 89)
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        TextView email = view.findViewById(R.id.email_text);
        TextView lastDate = view.findViewById(R.id.last_note_text);

        email.setText(Html.fromHtml(String.format("Email: <b>%s</b>", viewModel.getEmail())));



        /*===================================================================
         *              Charts
         ===================================================================*/
        PieChart chart = view.findViewById(R.id.barChart);
        HashMap<Month, Integer> info = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");

        viewModel.getLogs().observe(getViewLifecycleOwner(), logs -> {
            Log.d("Test", String.valueOf(logs.size()));

            List<LocalDateTime> list = logs.stream()
                    .map(e -> LocalDateTime.parse(e.getTime()))
                    .sorted()
                    .collect(Collectors.toList());


            for (LocalDateTime date : list) {
                lastDate.setText(Html.fromHtml(String.format("Last note: <b>%s</b>", date.format(formatter))));

                if (info.get(date.getMonth()) == null) {
                    info.put(date.getMonth(), 1);
                } else {
                    info.put(date.getMonth(), info.get(date.getMonth()) + 1);
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
            chart.setCenterText(String.format("Your activities \n(" + logs.size() + ")"));
            chart.setHoleRadius(40f);
            chart.setTransparentCircleRadius(55f);
            chart.getLegend().setWordWrapEnabled(true);
            chart.getDescription().setEnabled(false);


            chart.getLegend().setEnabled(false);

            chart.invalidate();
        });

        /*===================================================================
         *              Delete accouunt
         ===================================================================*/
        view.findViewById(R.id.delete_account_button)
                .setOnClickListener(new View.OnClickListener() {
                    public String getRandom() {
                        int leftLimit = 48; // numeral '0'
                        int rightLimit = 122; // letter 'z'
                        int targetStringLength = 6;
                        Random random = new Random();

                        return random.ints(leftLimit, rightLimit + 1)
                                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                                .limit(targetStringLength)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString();
                    }
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        View view = getLayoutInflater().inflate(R.layout.dialog_remove_input_text, null);
                        EditText inputText = view.findViewById(R.id.foldersName);
                        TextView deleteCode = view.findViewById(R.id.deletion_code);
                        String random = getRandom();
//                        String random = "";
                        deleteCode.setText(random);


                                builder.setTitle("Czy napewno chcesz usunąć konto ?")
                                .setView(view)
                                .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (inputText.getText().toString().equals(random)) {
                                            viewModel.removeAccount();
                                            Navigation.findNavController(v).navigate(R.id.action_accountFragment_to_loginFragment);
                                        } else Toast.makeText(getContext(), "Kod nie jest poprawny.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.create().show();
                    }
                });
        return view;
    }


}