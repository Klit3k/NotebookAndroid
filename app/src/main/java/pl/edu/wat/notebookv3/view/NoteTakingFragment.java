package pl.edu.wat.notebookv3.view;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.image.ImagesPlugin;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.model.Reminder;
import pl.edu.wat.notebookv3.model.adapter.ImageAdapter;
import pl.edu.wat.notebookv3.model.safenote.SafenoteResponse;
import pl.edu.wat.notebookv3.util.AlarmReceiver;
import pl.edu.wat.notebookv3.util.SafenoteService;
import pl.edu.wat.notebookv3.viewmodel.NoteTakingViewModel;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;

import static android.content.Context.ALARM_SERVICE;

public class NoteTakingFragment extends Fragment {
    private NoteTakingFragmentArgs args;
    private TextInputEditText bodyInputText;
    private List<String> imageUrls;
    private TextInputEditText titleInputText;
    private NoteTakingViewModel noteTakingViewModel;
    private String tag;
    boolean isStarred;
    private Markwon markwon;
    private RecyclerView imageRecyclerView;
    private ImageAdapter imageAdapter;

    private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrls = new ArrayList<>();
    }

    public AlertDialog createShareDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View customLayout = getLayoutInflater().inflate(R.layout.fragment_share_options, null);
        SeekBar seekBar = customLayout.findViewById(R.id.seekBar);
        TextView textView3 = customLayout.findViewById(R.id.textView3);
        TextInputEditText passwordInput = customLayout.findViewById(R.id.note_password_input);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    textView3.setText("None");
                } else textView3.setText(progress + "\nmin");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setView(customLayout);
        builder.setTitle("Udostępnij")
                .setPositiveButton("Udostępnij !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Przygotowywanie");
                        progressDialog.show();
                        AlertDialog.Builder shareBuilder = new AlertDialog.Builder(getContext());
                        shareBuilder.setTitle("Link do wpisu");
                        View customMessage = getLayoutInflater().inflate(R.layout.fragment_share_dialog, null);
                        shareBuilder.setView(customMessage);


                        SafenoteService safenoteService = new SafenoteService();

                        new Thread(() -> {
                            try {
                                SafenoteResponse safenoteResponse = safenoteService.share(titleInputText.getText().toString()
                                        , bodyInputText.getText().toString()
                                        , passwordInput.getText().toString()
                                        , seekBar.getProgress(), -1);
//                                SafenoteResponse safenoteResponse = new SafenoteResponse();
//                                safenoteResponse.setLink("https://safenote.co/r/6554e24db3b554@91601813");
                                getActivity().runOnUiThread(() -> {
                                    TextView dialogText = customMessage.findViewById(R.id.dialog_text);
                                    dialogText.setText(safenoteResponse.getLink());
                                    dialogText.setPaintFlags(dialogText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                    dialogText.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            // Copy the Text to the clipboard
                                            ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                            TextView showTextParam = (TextView) v;
                                            manager.setText(showTextParam.getText());

                                            // Show a message:
                                            Toast.makeText(activity, "Link w schowku.", Toast.LENGTH_SHORT).show();
//                                            Snackbar.make(v, "Link w schowku.", Snackbar.LENGTH_SHORT)
//                                                    .show();;
                                            return true;
                                        }
                                    });
                                    shareBuilder
                                            .setNegativeButton("Idź do", (dialog1, which1) -> {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(safenoteResponse.getLink()));
                                                startActivity(browserIntent);
                                            })
                                            .setPositiveButton("OK", (dialog12, which12) -> {

                                            });
                                    progressDialog.dismiss();
                                    shareBuilder.show();
                                });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();

                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_taking, container, false);
        markwon = Markwon.create(getContext());

        bodyInputText = view.findViewById(R.id.body_input_text);
        titleInputText = view.findViewById(R.id.title_input_text);
        /* Markdown */
        markwon = Markwon.builder(getContext())
                .usePlugin(ImagesPlugin.create())
                .build();

        MarkwonEditor editor = MarkwonEditor.create(markwon);
        bodyInputText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                bodyInputText));

        /*=========*/
        noteTakingViewModel = new ViewModelProvider(this).get(NoteTakingViewModel.class);
        createNotificationChannel();
        if (getArguments() != null && !getArguments().isEmpty()) {
            args = NoteTakingFragmentArgs.fromBundle(getArguments());
            if (args.getBody() != null) bodyInputText.setText(args.getBody());
            if (args.getTitle() != null) titleInputText.setText(args.getTitle());
            if (args.getTag() != null) tag = args.getTag();
            isStarred = args.getStarred();
        }

        MaterialToolbar materialToolbar = view.findViewById(R.id.materialToolbar);
        materialToolbar.setOnMenuItemClickListener(menuClickItemListener());
        materialToolbar.setNavigationOnClickListener(onNavBackListener());


        /*
                Image recyclerview
         */
        imageRecyclerView = view.findViewById(R.id.recyclerView);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerView.setHasFixedSize(true);

        if (tag == null) {
            tag = UUID.randomUUID().toString();
            noteTakingViewModel.createNote(
                    tag,
                    titleInputText.getText().toString(),
                    bodyInputText.getText().toString()
            );

        }

        imageAdapter = new ImageAdapter(new ArrayList<>(), tag, DashboardFragment.getCurrentFolder());

        noteTakingViewModel.getImages(tag, args.getCurrentFolder()).observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> imagesUrl) {
                imageUrls = imagesUrl;
                imageAdapter = new ImageAdapter(imagesUrl, tag, DashboardFragment.getCurrentFolder());
                imageRecyclerView.setAdapter(imageAdapter);

                if (imageAdapter.getItemCount() == 0) {
                    imageRecyclerView.setVisibility(View.GONE);
                } else {
                    imageRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });


        return view;
    }

    @NonNull
    private View.OnClickListener onNavBackListener() {
        return v -> {
            String title = titleInputText.getText().toString();
            String body = bodyInputText.getText().toString();
            Log.d("Test", "currentFolder = " + args.getCurrentFolder());
            if (!body.isEmpty()) {
                if (tag != null)
                    noteTakingViewModel.updateNote(tag, title, body, imageUrls);
                else
                    noteTakingViewModel.createNote(title, body);
            }

            Navigation.findNavController(v).navigate(R.id.action_noteTakingFragment_to_dashboardFragment);
        };
    }

    @NonNull
    private Toolbar.OnMenuItemClickListener menuClickItemListener() {
        return item -> {
            if (item.getItemId() == R.id.share_item) {
                createShareDialog(getActivity()).show();
                return true;
            } else if (item.getItemId() == R.id.starNote) {
                if (!isStarred) {
                    item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.star, null));
                    Snackbar.make(getView(), "Usunięto z ważnych notatek.", Snackbar.LENGTH_SHORT).show();

                } else {
                    item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.star_filled, null));
                    Snackbar.make(getView(), "Dodano do ważnych notatek.", Snackbar.LENGTH_SHORT).show();
                }
            } else if (item.getItemId() == R.id.reminder_item) {
                showTimePicker();
            } else if (item.getItemId() == R.id.no_md) {
            } else if (item.getItemId() == R.id.md) {
                markwon.setMarkdown(bodyInputText, bodyInputText.getText().toString());
            } else if (item.getItemId() == R.id.add_image) {
                showAddImage();
            }
            return item.getItemId() == R.id.reminder_item;
        };
    }

    private void showAddImage() {
        ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case Activity.RESULT_OK:
                if (data != null && data.getData() != null) {
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Dodawanie obrazu");
                    progressDialog.show();

                    noteTakingViewModel.uploadImage(tag, data.getData())
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Snackbar.make(getView(), "Obraz został dodany pomyślnie.", Snackbar.LENGTH_SHORT).show();
                                    if(imageUrls != null)
                                        imageUrls.add(uri.toString());
                                    else
                                        imageUrls = new ArrayList<>(Arrays.asList(uri.toString()));

                                    noteTakingViewModel.updateImage(
                                            tag,
                                            titleInputText.getText().toString(),
                                            bodyInputText.getText().toString(),
                                            imageUrls
                                    );
                                    progressDialog.dismiss();
                                }
                            });

                }
                break;
            case ImagePicker.RESULT_ERROR:
                Snackbar.make(getView(), "ERROR", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /*===========================================
                         Notifications
         ===========================================*/
    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "general";
            String desc = "Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("general", name, importance);
            channel.setDescription(desc);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private MaterialDatePicker.Builder<Long> datePickerBuilder;

    private void showTimePicker() {
        datePickerBuilder = MaterialDatePicker.Builder
                .datePicker();


        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Wybierz godzinę alarmu")
                .build();
        MaterialDatePicker<Long> datePicker = datePickerBuilder.build();

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                LocalDateTime date = Instant.ofEpochMilli(datePicker.getSelection())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                calendar.set(Calendar.YEAR, date.getYear());
                calendar.set(Calendar.MONTH, date.getMonthValue() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
                setAlarm(titleInputText.getText().toString()
                        , calendar.getTimeInMillis());
            }
        });
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Long select = (Long) selection;


                timePicker.show(getParentFragmentManager(), "general");

            }
        });

        datePicker.show(getParentFragmentManager(), "general");

    }

    @SuppressLint("ScheduleExactAlarm")
    private void setAlarm(String message, long time) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());

        Reminder reminder = Reminder.builder()
                .id(UUID.randomUUID().toString())
                .message(message)
                .remindDate(date.toString())
                .build();
        Log.d("Test:Calendar", date.format(DateTimeFormatter.ISO_DATE_TIME));

        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("Message", reminder.getMessage());
        intent.putExtra("RemindDate", reminder.getRemindDate().toString());
        intent.putExtra("id", reminder.getId());

        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_MUTABLE);


//        Map<String, Object> data = new HashMap<>();
//        data.put("packageName", getContext().getPackageName());
//        data.put("className", AlarmReceiver.class.getName());
//
//        reminder.setPendingIntent(data);
        noteTakingViewModel.createReminder(reminder);

//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(time, pendingIntent), pendingIntent);

        Toast.makeText(getContext(), "Ustawiono alarm", Toast.LENGTH_SHORT).show();
    }


}
