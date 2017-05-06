package com.example.ewa.bmi;

import android.support.annotation.NonNull;
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

    public static final double WOMEN_LIQUID_PERCENT_VOLUME = 0.6;
    public static final double MAN_LIQUID_PERCENT_VOLUME = 0.7;
    public static final int GENDER_POSITION_MAN = 0;
    public static final int GENDER_POSITION_WOMEN = 1;
    public static final double NORMAL_BMI_BOTTOM_LIMIT = 18.5;
    public static final double NORMAL_BMI_UPPER_LIMIT = 25.0;

    Spinner spinnerGender;
    Spinner spinnerActivity;
    double activityIndex;
    boolean isWomen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerGender = (Spinner) findViewById(R.id.gender_spinner);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
                if (position == GENDER_POSITION_MAN) {
                    Toast.makeText(MainActivity.this, "Wybrano mężczyznę", Toast.LENGTH_SHORT).show();
                } else if (position == GENDER_POSITION_WOMEN) {
                    Toast.makeText(MainActivity.this, "Wybrano kobietę", Toast.LENGTH_SHORT).show();
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

        boolean isWomen = isWomen();
        double weight = parseWeight();
        double height = parseHeight();
        double age = parseAge();

        double ppm = calcPPM(isWomen, weight, height, age);
        double cpm = calcCPM(ppm);
        double bmi = calcBMI(weight, height);


        double weightToNormal = getWeightToNormal(bmi, height);
        double liquid = calcLiquid(weight, weightToNormal);

        double squaredHeight = height * height;

        double bmiOverweight = NORMAL_BMI_UPPER_LIMIT * squaredHeight;
        double bmiUnderweight = NORMAL_BMI_BOTTOM_LIMIT * squaredHeight;

        displayResults(ppm, cpm, bmi, liquid, weightToNormal, bmiOverweight, bmiUnderweight);

    }

    private double calcBMI(double weight, double height) {
        return weight / (Math.pow(height, 2));
    }

    private void displayResults(double ppm, double cpm, double bmi, double liquid, double weightToNormal, double bmiOverweight, double bmiUnderweight) {
        String overweightText = getOverweightText(bmiOverweight);
        String underWeightText = createUnderweightText(bmiUnderweight);
        String liquidValueText = getLiquidValueText(liquid);
        String ppmText = createPPMText(ppm);
        String cpmText = createCPMText(cpm);
        String bmiResult = createBMIValueText(bmi);
        String categoryText = createCategoryText(bmi, weightToNormal);

        StringBuilder resultTextBuilder = new StringBuilder();

        resultTextBuilder
                .append(bmiResult).append("\n")
                .append(categoryText).append("\n")
                .append(overweightText).append("\n")
                .append(underWeightText).append("\n")
                .append(liquidValueText).append("\n\n")
                .append("Zapotrzebowanie na energię:\n")
                .append(ppmText)
                .append(cpmText)
        ;

        setResultText(resultTextBuilder.toString());
    }

    @NonNull
    private String createCPMText(double cpm) {
        return " -  całtkowita przemiana materii (CPM) wynosi " + String.format("%.2f", cpm) + " kcal";
    }

    @NonNull
    private String createPPMText(double ppm) {
        return " - podstawowa przemiana materii (PPM) wynosi " + String.format("%.2f", ppm) + " kcal" + "\n";
    }

    @NonNull
    private String createUnderweightText(double bmiUnderweight) {
        return "Będziesz mieć niedowagę poniżej " + String.format("%.2f", bmiUnderweight) + " kg";
    }

    @NonNull
    private String getOverweightText(double bmiOverweight) {
        return "Będziesz mieć nadwagę powyżej " + String.format("%.2f", bmiOverweight) + " kg";
    }

    @NonNull
    private String getLiquidValueText(double liquid) {
        return "Ilość płynów ustrojowych to " + String.format("%.2f", liquid) + " kg";
    }

    @NonNull
    private String createBMIValueText(double bmi) {
        return "Twoje BMI wynosi: " + String.format("%.2f", bmi);
    }

    private String createCategoryText(double bmi, double weightToNormal) {
        String categoryText;
        if (bmi < NORMAL_BMI_BOTTOM_LIMIT) {
            categoryText = "Posiadasz niedowagę " + String.format("%.2f", weightToNormal) + " kg";
        } else if (bmi >= NORMAL_BMI_UPPER_LIMIT) {
            categoryText = "Posiadasz nadwagę " + String.format("%.2f", -weightToNormal) + " kg";
        } else {
            categoryText = "Posiadasz prawidłową wagę";
        }
        return categoryText;
    }

    private double getWeightToNormal(double bmi, double height) {

        double squaredHeight = Math.pow(height, 2);

        return (getNormalBMIThresholdForBMI(bmi) - bmi) * squaredHeight;
    }

    public double getNormalBMIThresholdForBMI(double bmi) {
        return Math.min(Math.max(bmi, NORMAL_BMI_BOTTOM_LIMIT), NORMAL_BMI_UPPER_LIMIT);
    }

    private double calcLiquid(double weight, double underWeightValue) {
        return (weight + underWeightValue) * getLiquidPercentVolume(isWomen) - 0.15 * underWeightValue;
    }

    private boolean isWomen() {
        return spinnerGender.getSelectedItemId() == GENDER_POSITION_WOMEN;
    }

    private void setResultText(String resultText) {
        TextView resultTextView = (TextView) findViewById(R.id.result_text);
        resultTextView.setText(resultText);
    }

    private double calcPPM(boolean isWomen, double weight, double height, double age) {
        if (isWomen) {
            return 665.09 + 9.56 * weight + 1.85 * height - 4.67 * age;
        } else {
            return 66.47 + 13.7 * weight + 5 * 100 * height - 6.76 * age;
        }
    }

    private double calcCPM(double ppm) {
        return ppm * activityIndex;
    }

    private double getLiquidPercentVolume(boolean isWomen) {
        return isWomen ? WOMEN_LIQUID_PERCENT_VOLUME : MAN_LIQUID_PERCENT_VOLUME;
    }

    private double parseAge() {
        EditText ageEditText = (EditText) findViewById(R.id.age_edit);
        String ageString = ageEditText.getText().toString();
        return Double.parseDouble(ageString);
    }

    private double parseHeight() {
        EditText heigthEditText = (EditText) findViewById(R.id.height_edit);
        String heightString = heigthEditText.getText().toString();
        return Double.parseDouble(heightString);
    }

    private double parseWeight() {
        EditText weightEditText = (EditText) findViewById(R.id.weight_edit);
        String weightString = weightEditText.getText().toString();
        return Double.parseDouble(weightString);
    }


}
