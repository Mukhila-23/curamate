package com.example.curamate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddMedicineActivity extends AppCompatActivity {

    private EditText edtMedicineName;
    private TextView txtTimes;
    private ArrayList<String> timeList = new ArrayList<>();

    private CheckBox chkSun, chkMon, chkTue, chkWed, chkThu, chkFri, chkSat;

    private MedicineDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        edtMedicineName = findViewById(R.id.edtMedicineName);
        txtTimes = findViewById(R.id.txtTimes);

        chkSun = findViewById(R.id.chkSun);
        chkMon = findViewById(R.id.chkMon);
        chkTue = findViewById(R.id.chkTue);
        chkWed = findViewById(R.id.chkWed);
        chkThu = findViewById(R.id.chkThu);
        chkFri = findViewById(R.id.chkFri);
        chkSat = findViewById(R.id.chkSat);

        Button btnSetTime = findViewById(R.id.btnSetTime);
        Button btnSave = findViewById(R.id.btnSaveMedicine);

        dbHelper = new MedicineDbHelper(this);

        btnSetTime.setOnClickListener(v -> pickTime());
        btnSave.setOnClickListener(v -> saveMedicine());
    }

    private void pickTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, h, m) -> {
            String time = String.format("%02d:%02d", h, m);
            timeList.add(time);
            txtTimes.setText("Times: " + timeList);
        }, hour, minute, true).show();
    }

    private Set<String> getSelectedDays() {
        Set<String> days = new HashSet<>();
        if (chkSun.isChecked()) days.add("Sun");
        if (chkMon.isChecked()) days.add("Mon");
        if (chkTue.isChecked()) days.add("Tue");
        if (chkWed.isChecked()) days.add("Wed");
        if (chkThu.isChecked()) days.add("Thu");
        if (chkFri.isChecked()) days.add("Fri");
        if (chkSat.isChecked()) days.add("Sat");
        return days;
    }

    private void saveMedicine() {
        String name = edtMedicineName.getText().toString().trim();
        if (name.isEmpty() || timeList.isEmpty()) {
            edtMedicineName.setError("Enter medicine name and add times!");
            return;
        }

        List<String> times = new ArrayList<>(timeList);
        List<String> days = new ArrayList<>(getSelectedDays());

        dbHelper.addMedicine(name, times, days);

        for (String time : times) {
            String[] hm = time.split(":");
            int hour = Integer.parseInt(hm[0]);
            int minute = Integer.parseInt(hm[1]);
            scheduleAlarm(this, name, hour, minute, days);
        }

        Toast.makeText(this, "Medicine scheduled successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 🔹 Public static so BootReceiver can reuse it
    public static void scheduleAlarm(Context context, String medicine, int hour, int minute, List<String> days) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, MedicineReminderReceiver.class);
        intent.putExtra("medicine", medicine);
        intent.putStringArrayListExtra("days", new ArrayList<>(days));
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                ("med" + medicine + hour + minute).hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    pendingIntent
            );
            Log.d("AddMedicineActivity", "Scheduled " + medicine + " at " + hour + ":" + minute + " days=" + days);
        }
    }
}
