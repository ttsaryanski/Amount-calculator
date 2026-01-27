package com.amountcalculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    private static final double EUR_TO_BGN_RATE = 1.95583;
    
    private EditText editAmountDue;
    private EditText editAmountPaid;
    private Spinner spinnerDueCurrency;
    private Spinner spinnerPaidCurrency;
    private TextView textDueCurrency;
    private TextView textPaidCurrency;
    private TextView textResultBGN;
    private TextView textResultEUR;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        setupSpinners();
        setupTextWatchers();
        setupClearButton();
    }
    
    private void initializeViews() {
        editAmountDue = findViewById(R.id.editAmountDue);
        editAmountPaid = findViewById(R.id.editAmountPaid);
        spinnerDueCurrency = findViewById(R.id.spinnerDueCurrency);
        spinnerPaidCurrency = findViewById(R.id.spinnerPaidCurrency);
        textDueCurrency = findViewById(R.id.textDueCurrency);
        textPaidCurrency = findViewById(R.id.textPaidCurrency);
        textResultBGN = findViewById(R.id.textResultBGN);
        textResultEUR = findViewById(R.id.textResultEUR);
        
        // Initialize currency displays
        updateCurrencyDisplay();
    }
    
    private void setupSpinners() {
        String[] currencies = {"BGN", "EUR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinnerDueCurrency.setAdapter(adapter);
        spinnerPaidCurrency.setAdapter(adapter);
        spinnerDueCurrency.setSelection(0); // Default to BGN
        spinnerPaidCurrency.setSelection(0); // Default to BGN
        
        // Add listeners for automatic calculation
        spinnerDueCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCurrencyDisplay();
                calculateChange();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinnerPaidCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCurrencyDisplay();
                calculateChange();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    private void setupTextWatchers() {
        editAmountDue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                calculateChange();
            }
        });
        
        editAmountPaid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                calculateChange();
            }
        });
    }
    
    private void setupClearButton() {
        Button buttonClear = findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFields();
            }
        });
    }
    
    private void clearAllFields() {
        editAmountDue.setText("");
        editAmountPaid.setText("");
        textResultBGN.setText("0.00 BGN");
        textResultEUR.setText("0.00 EUR");
        spinnerDueCurrency.setSelection(0); // Reset to BGN
        spinnerPaidCurrency.setSelection(0); // Reset to BGN
        updateCurrencyDisplay();
    }
    
    private void calculateChange() {
        try {
            String dueText = editAmountDue.getText().toString();
            String paidText = editAmountPaid.getText().toString();
            
            if (dueText.isEmpty() || paidText.isEmpty()) {
                textResultBGN.setText("0.00 BGN");
                textResultEUR.setText("0.00 EUR");
                return;
            }
            
            double amountDue = Double.parseDouble(dueText);
            double amountPaid = Double.parseDouble(paidText);
            
            String dueCurrency = spinnerDueCurrency.getSelectedItem().toString();
            String paidCurrency = spinnerPaidCurrency.getSelectedItem().toString();
            
            // Convert both amounts to BGN
            double amountDueBGN = convertToBGN(amountDue, dueCurrency);
            double amountPaidBGN = convertToBGN(amountPaid, paidCurrency);
            
            // Calculate change in BGN
            double changeBGN = amountPaidBGN - amountDueBGN;
            
            // Convert change to EUR
            double changeEUR = convertToEUR(changeBGN);
            
            // Format and display results
            String formattedBGN = formatCurrency(changeBGN, "BGN");
            String formattedEUR = formatCurrency(changeEUR, "EUR");
            
            textResultBGN.setText(formattedBGN);
            textResultEUR.setText(formattedEUR);
            
        } catch (NumberFormatException e) {
            textResultBGN.setText("0.00 BGN");
            textResultEUR.setText("0.00 EUR");
        }
    }
    
    private double convertToBGN(double amount, String currency) {
        if ("EUR".equals(currency)) {
            return amount * EUR_TO_BGN_RATE;
        }
        return amount; // Already in BGN
    }
    
    private double convertToEUR(double amountBGN) {
        return amountBGN / EUR_TO_BGN_RATE;
    }
    
    private String formatCurrency(double amount, String currency) {
        BigDecimal bd = BigDecimal.valueOf(amount);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return String.format("%.2f %s", bd.doubleValue(), currency);
    }
    
    private void updateCurrencyDisplay() {
        if (spinnerDueCurrency.getSelectedItem() != null) {
            String currency = spinnerDueCurrency.getSelectedItem().toString();
            textDueCurrency.setText(currency);
        }
        
        if (spinnerPaidCurrency.getSelectedItem() != null) {
            String currency = spinnerPaidCurrency.getSelectedItem().toString();
            textPaidCurrency.setText(currency);
        }
    }
}
