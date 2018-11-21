package com.example.andres.lectorsocket;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText etCodigo;
    Button btnEscaner;
    String message = "";
    private static String ip = "192.168.1.112";
    private static Socket s;
    private static PrintWriter printWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCodigo = (EditText)findViewById(R.id.etCodigo);
        btnEscaner = (Button)findViewById(R.id.btnEscaner);

        /* LA ACCION PARA EL BOTON DE ESCANER */

        btnEscaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanear();
            }
        });
    }

    /* FUNCION PARA ESCANEAR */

    public void escanear() {
        IntentIntegrator intent = new IntentIntegrator(this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);

        intent.setPrompt("Escanear codigo");
        intent.setCameraId(0);
        intent.setBeepEnabled(false);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelaste el escaneo", Toast.LENGTH_LONG).show();
            }else {
                /* RESULTADO DEL CODIGO ESCANEADO */

                etCodigo.setText(result.getContents().toString());

                message = etCodigo.getText().toString();

                send_text(message);
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void send_text(String message) {
        message = etCodigo.getText().toString();
        myTask mt = new myTask();
        mt.execute();

        Toast.makeText(getApplicationContext(), "Datos enviados", Toast.LENGTH_LONG).show();
    }

    class myTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                s = new Socket(ip,5000);

                printWriter = new PrintWriter(s.getOutputStream());

                printWriter.write(message);

                printWriter.flush();

                printWriter.close();

                s.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}