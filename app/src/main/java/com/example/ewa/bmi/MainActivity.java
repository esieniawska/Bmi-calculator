package com.example.ewa.bmi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerGender;
    Spinner spinnerActivity;
    double liquid;
    double percent, weight, height, age;
    double ppm;
    double cpm;
    double activityIndex;
    boolean women;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerGender = (Spinner) findViewById(R.id.gender_spinner);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
                if (position == 0) {
                    Toast.makeText(MainActivity.this, "Wybrano mężczyznę", Toast.LENGTH_SHORT).show();
                    women = false;
                } else if (position == 1) {
                    Toast.makeText(MainActivity.this, "Wybrano kobietę", Toast.LENGTH_SHORT).show();
                    women = true;
                }

                calcBmi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerActivity = (Spinner) findViewById(R.id.activity_spinner);
        spinnerActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    activityIndex = 2.3;
                } else if (position == 1) {
                    activityIndex = 2;
                } else if (position == 2) {
                    activityIndex = 1.8;
                } else if (position == 3) {
                    activityIndex = 1.45;
                }

                calcBmi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button button = (Button) findViewById(R.id.calc_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcBmi();
            }
        });

    }

    private void calcBmi() {
        try {
            tryCalcBmi();
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Błędne dane", Toast.LENGTH_SHORT).show();
        }
    }


    private void tryCalcBmi() {
        parseWeight();

        parseHeight();

        parseAge();

        calcPPM();

        double bmi = weight / (Math.pow(height, 2));


        String resultString = "Twoje BMI wynosi: " + String.format("%.2f", bmi) + "\n";

        if (bmi < 18.5) {
            double underweight = (18.5 - bmi) * (Math.pow(height, 2));
            liquid = (weight + underweight) * percent - 0.15 * underweight;
            resultString = resultString + "Posiadasz niedowagę " + String.format("%.2f", underweight) + "kg" + "\n";
        } else if (bmi >= 25) {
            double overweight = (bmi - 25) * (Math.pow(height, 2));
            liquid = (weight - overweight) * percent + 0.15 * overweight;
            resultString += "Posiadasz nadwagę " + String.format("%.2f", overweight) + " kg" + "\n";
        } else {
            liquid = percent * weight;
            resultString += "Posiadasz prawidłową wagę" + "\n";
        }

        double bmi_overweight = 25 * height * height;
        resultString += "Będziesz mieć nadwagę przy " + String.format("%.2f", bmi_overweight) + " kg" + "\n";

        double bmi_underweight = 18.5 * height * height;
        resultString += "Będziesz mieć niedowagę przy " + String.format("%.2f", bmi_underweight) + " kg" + "\n";

        resultString += "Ilość płynów ustrojowych to " + String.format("%.2f", liquid) + " kg" + "\n \n";

        resultString += "Zapotrzebowanie na energię: " + "\n";

        resultString += " - podstawowa przemiana materii (PPM) wynosi " + String.format("%.2f", ppm) + " kcal"+"\n";
        cpm = ppm * activityIndex;

        resultString += " -  całtkowita przemiana materii (CPM) wynosi " + String.format("%.2f", cpm) + " kcal";
        TextView resultTextView = (TextView) findViewById(R.id.result_text);
        resultTextView.setText(resultString);

    }

    private void calcPPM() {
        if (women) {
            percent = 0.6;
            ppm = 665.09 + 9.56 * weight + 1.85 * height - 4.67 * age;
        } else {
            percent = 0.7;
            ppm = 66.47 + 13.7 * weight + 5 * 100 * height - 6.76 * age;
        }
    }

    private void parseAge() {
        EditText ageEditText = (EditText) findViewById(R.id.age_edit);
        String ageString = ageEditText.getText().toString();
        age = Double.parseDouble(ageString);
    }

    private void parseHeight() {
        EditText heigthEditText = (EditText) findViewById(R.id.height_edit);
        String heightString = heigthEditText.getText().toString();
        height = Double.parseDouble(heightString);
    }

    private void parseWeight() {
        EditText weightEditText = (EditText) findViewById(R.id.weight_edit);
        String weightString = weightEditText.getText().toString();
        weight = Double.parseDouble(weightString);
    }


}
