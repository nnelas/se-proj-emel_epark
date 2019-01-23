package pt.se_ulusofona.se_proj_emel_epark.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import pt.se_ulusofona.se_proj_emel_epark.R;

/**
 * Created by nunonelas on 01/05/17.
 */

public class FAQActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Button resposta1 = (Button) findViewById(R.id.resposta1);
        Button resposta2 = (Button) findViewById(R.id.resposta2);
        Button resposta3 = (Button) findViewById(R.id.resposta3);
        Button resposta4 = (Button) findViewById(R.id.resposta4);
        Button resposta5 = (Button) findViewById(R.id.resposta5);

        resposta1.setVisibility(View.GONE);
        resposta2.setVisibility(View.GONE);
        resposta3.setVisibility(View.GONE);
        resposta4.setVisibility(View.GONE);
        resposta5.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {

        Button pergunta1 = (Button) findViewById(R.id.pergunta1);
        Button pergunta2 = (Button) findViewById(R.id.pergunta2);
        Button pergunta3 = (Button) findViewById(R.id.pergunta3);
        Button pergunta4 = (Button) findViewById(R.id.pergunta4);
        Button pergunta5 = (Button) findViewById(R.id.pergunta5);

        Button resposta1 = (Button) findViewById(R.id.resposta1);
        Button resposta2 = (Button) findViewById(R.id.resposta2);
        Button resposta3 = (Button) findViewById(R.id.resposta3);
        Button resposta4 = (Button) findViewById(R.id.resposta4);
        Button resposta5 = (Button) findViewById(R.id.resposta5);

        Intent intent = null;
        switch (view.getId()) {
            //Handle Clicks
            case R.id.pergunta1:
                pergunta2.setVisibility(View.GONE);
                pergunta3.setVisibility(View.GONE);
                pergunta4.setVisibility(View.GONE);
                pergunta5.setVisibility(View.GONE);

                resposta1.setVisibility(View.VISIBLE);
                break;
            case R.id.pergunta2:
                pergunta1.setVisibility(View.GONE);
                pergunta3.setVisibility(View.GONE);
                pergunta4.setVisibility(View.GONE);
                pergunta5.setVisibility(View.GONE);

                resposta2.setVisibility(View.VISIBLE);
                break;
            case R.id.pergunta3:
                pergunta1.setVisibility(View.GONE);
                pergunta2.setVisibility(View.GONE);
                pergunta4.setVisibility(View.GONE);
                pergunta5.setVisibility(View.GONE);

                resposta3.setVisibility(View.VISIBLE);
                break;
            case R.id.pergunta4:
                pergunta1.setVisibility(View.GONE);
                pergunta2.setVisibility(View.GONE);
                pergunta3.setVisibility(View.GONE);
                pergunta5.setVisibility(View.GONE);

                resposta4.setVisibility(View.VISIBLE);
                break;
            case R.id.pergunta5:
                pergunta1.setVisibility(View.GONE);
                pergunta2.setVisibility(View.GONE);
                pergunta3.setVisibility(View.GONE);
                pergunta4.setVisibility(View.GONE);

                resposta5.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_faq:
                pergunta1.setVisibility(View.VISIBLE);
                pergunta2.setVisibility(View.VISIBLE);
                pergunta3.setVisibility(View.VISIBLE);
                pergunta4.setVisibility(View.VISIBLE);
                pergunta5.setVisibility(View.VISIBLE);

                resposta1.setVisibility(View.GONE);
                resposta2.setVisibility(View.GONE);
                resposta3.setVisibility(View.GONE);
                resposta4.setVisibility(View.GONE);
                resposta5.setVisibility(View.GONE);
                break;
            case R.id.btn_fechaFAQ:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        if(intent != null) {
            startActivity(intent);
        }
    }
}
