package com.example.tp1_2;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void computeOperationResult(View v){
        EditText op1 =findViewById(R.id.etOperand1);
        EditText op2 =findViewById(R.id.etOperand2);
        TextView ope =findViewById(R.id.tvOperator);
        EditText res =findViewById(R.id.etResult);
        double op1u= Double.parseDouble(op1.getText().toString());
        double op2u= Double.parseDouble(op2.getText().toString());
        String opeu= ope.getText().toString();
        if (opeu.equals("+"))
            res.setText(op1u + op2u +"");
        else if (opeu.equals("-"))
            res.setText(op1u - op2u +"");
        else if (opeu.equals("*"))
            res.setText(op1u * op2u +"");
        else if (opeu.equals("/"))
            res.setText(op1u / op2u +"");
    }

    public void setOperand(View v){
        TextView ope =findViewById(R.id.tvOperator);
        if(v.getId()==R.id.btnPlus) ope.setText("+");
        else if(v.getId()==R.id.btnMinus) ope.setText("-");
        else if(v.getId()==R.id.btnStar) ope.setText("*");
        else if(v.getId()==R.id.btnDivide) ope.setText("/");
    }
}