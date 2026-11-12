package com.calculator.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tvInput, tvResult;
    private String input = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInput = findViewById(R.id.tvInput);
        tvResult = findViewById(R.id.tvResult);

        setListeners();
    }

    private void setListeners() {
        int[] ids = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnDot
        };

        View.OnClickListener numListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                input += b.getText().toString();
                tvInput.setText(input);
            }
        };

        for (int id : ids) {
            findViewById(id).setOnClickListener(numListener);
        }

        View.OnClickListener opListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                input += b.getText().toString();
                tvInput.setText(input);
            }
        };

        findViewById(R.id.btnPlus).setOnClickListener(opListener);
        findViewById(R.id.btnMinus).setOnClickListener(opListener);
        findViewById(R.id.btnMultiply).setOnClickListener(opListener);
        findViewById(R.id.btnDivide).setOnClickListener(opListener);
        findViewById(R.id.btnPercent).setOnClickListener(opListener);

        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = "";
                tvInput.setText("");
                tvResult.setText("0");
            }
        });

        findViewById(R.id.btnBackspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.length() > 0) {
                    input = input.substring(0, input.length() - 1);
                    tvInput.setText(input);
                }
            }
        });

        findViewById(R.id.btnEquals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String formattedExp = input.replace('×', '*').replace('÷', '/');
                    double res = eval(formattedExp);
                    DecimalFormat df = new DecimalFormat("#.##########");
                    tvResult.setText(df.format(res));
                } catch (Exception e) {
                    tvResult.setText("خطأ");
                }
            }
        });
    }

    private double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                return x;
            }
        }.parse();
    }
}
