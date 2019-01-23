package pt.se_ulusofona.se_proj_emel_epark.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pt.se_ulusofona.se_proj_emel_epark.R;
import pt.se_ulusofona.se_proj_emel_epark.model.Carro;
import pt.se_ulusofona.se_proj_emel_epark.model.Registo;

import static android.content.ContentValues.TAG;

/**
 * Created by nunonelas on 05/06/17.
 */

public class MatriculaFragment extends Fragment implements View.OnClickListener {

    public MatriculaFragment(){}

    private TextView str_matricula;
    private String matricula;

    private DatabaseReference mDatabaseCarros;
    private DatabaseReference mDatabaseRegistos;

    private String lastRua = "";
    private String lastZona = "";
    private String lastData = "";

    private String lastRua2 = "";
    private String lastData2 = "";

    private String lastRua3 = "";
    private String lastData3 = "";

    private String messageRegisto;
    private String messageInfo;

    private int a;

    private Carro carro;
    private Registo registo;

    private List<Registo> registos;

    private boolean existeMatriculaBD;

    private boolean isUserIsInTheMiddleOfTyping=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matricula, container, false);

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    clearScreen();
                }
                return true;
            }
        });

        str_matricula = (TextView) view.findViewById(R.id.str_matricula);
        str_matricula.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (s.length() == 6) {
                    matricula = str_matricula.getText().toString();
                    buildMatricula();
                    clearScreen();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });

        //botões teclado
        Button btn_0 = (Button) view.findViewById(R.id.btn_0);
        Button btn_1 = (Button) view.findViewById(R.id.btn_1);
        Button btn_2 = (Button) view.findViewById(R.id.btn_2);
        Button btn_3 = (Button) view.findViewById(R.id.btn_3);
        Button btn_4 = (Button) view.findViewById(R.id.btn_4);
        Button btn_5 = (Button) view.findViewById(R.id.btn_5);
        Button btn_6 = (Button) view.findViewById(R.id.btn_6);
        Button btn_7 = (Button) view.findViewById(R.id.btn_7);
        Button btn_8 = (Button) view.findViewById(R.id.btn_8);
        Button btn_9 = (Button) view.findViewById(R.id.btn_9);
        Button btn_Q = (Button) view.findViewById(R.id.btn_Q);
        Button btn_W = (Button) view.findViewById(R.id.btn_W);
        Button btn_E = (Button) view.findViewById(R.id.btn_E);
        Button btn_R = (Button) view.findViewById(R.id.btn_R);
        Button btn_T = (Button) view.findViewById(R.id.btn_T);
        Button btn_Y = (Button) view.findViewById(R.id.btn_Y);
        Button btn_U = (Button) view.findViewById(R.id.btn_U);
        Button btn_I = (Button) view.findViewById(R.id.btn_I);
        Button btn_O = (Button) view.findViewById(R.id.btn_O);
        Button btn_P = (Button) view.findViewById(R.id.btn_P);
        Button btn_A = (Button) view.findViewById(R.id.btn_A);
        Button btn_S = (Button) view.findViewById(R.id.btn_S);
        Button btn_D = (Button) view.findViewById(R.id.btn_D);
        Button btn_F = (Button) view.findViewById(R.id.btn_F);
        Button btn_G = (Button) view.findViewById(R.id.btn_G);
        Button btn_H = (Button) view.findViewById(R.id.btn_H);
        Button btn_J = (Button) view.findViewById(R.id.btn_J);
        Button btn_K = (Button) view.findViewById(R.id.btn_K);
        Button btn_L = (Button) view.findViewById(R.id.btn_L);
        Button btn_Z = (Button) view.findViewById(R.id.btn_Z);
        Button btn_X = (Button) view.findViewById(R.id.btn_X);
        Button btn_C = (Button) view.findViewById(R.id.btn_C);
        Button btn_V = (Button) view.findViewById(R.id.btn_V);
        Button btn_B = (Button) view.findViewById(R.id.btn_B);
        Button btn_N = (Button) view.findViewById(R.id.btn_N);
        Button btn_M = (Button) view.findViewById(R.id.btn_M);
        Button btn_clear = (Button) view.findViewById(R.id.btn_clear);
        Button btn_del = (Button) view.findViewById(R.id.btn_del);

        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_Q.setOnClickListener(this);
        btn_W.setOnClickListener(this);
        btn_E.setOnClickListener(this);
        btn_R.setOnClickListener(this);
        btn_T.setOnClickListener(this);
        btn_Y.setOnClickListener(this);
        btn_U.setOnClickListener(this);
        btn_I.setOnClickListener(this);
        btn_O.setOnClickListener(this);
        btn_P.setOnClickListener(this);
        btn_A.setOnClickListener(this);
        btn_S.setOnClickListener(this);
        btn_D.setOnClickListener(this);
        btn_F.setOnClickListener(this);
        btn_G.setOnClickListener(this);
        btn_H.setOnClickListener(this);
        btn_J.setOnClickListener(this);
        btn_K.setOnClickListener(this);
        btn_L.setOnClickListener(this);
        btn_Z.setOnClickListener(this);
        btn_X.setOnClickListener(this);
        btn_C.setOnClickListener(this);
        btn_V.setOnClickListener(this);
        btn_B.setOnClickListener(this);
        btn_N.setOnClickListener(this);
        btn_M.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_del.setOnClickListener(this);

        mDatabaseCarros = FirebaseDatabase.getInstance().getReference("carros");
        mDatabaseRegistos = FirebaseDatabase.getInstance().getReference("registos");


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
            case R.id.btn_Q:
            case R.id.btn_W:
            case R.id.btn_E:
            case R.id.btn_R:
            case R.id.btn_T:
            case R.id.btn_Y:
            case R.id.btn_U:
            case R.id.btn_I:
            case R.id.btn_O:
            case R.id.btn_P:
            case R.id.btn_A:
            case R.id.btn_S:
            case R.id.btn_D:
            case R.id.btn_F:
            case R.id.btn_G:
            case R.id.btn_H:
            case R.id.btn_J:
            case R.id.btn_K:
            case R.id.btn_L:
            case R.id.btn_Z:
            case R.id.btn_X:
            case R.id.btn_C:
            case R.id.btn_V:
            case R.id.btn_B:
            case R.id.btn_N:
            case R.id.btn_M:
                touchDigit(view);
                break;
            case R.id.btn_clear:
                clearScreen();
                break;
            case R.id.btn_del:
                delLastDigit();
                break;
        }
    }

    public void touchDigit(View view) {

        Button btPressed = (Button) view;
        String digito = btPressed.getText().toString();
        if (isUserIsInTheMiddleOfTyping) {
            str_matricula.append(digito);
        }else {
            str_matricula.setText(digito);
            isUserIsInTheMiddleOfTyping = true;
        }
    }

    public void checkMatriculaFirebase(){
        mDatabaseCarros.child(matricula).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carro = dataSnapshot.getValue(Carro.class);
                checkCarroIsNull(carro);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void checkCarroIsNull(Carro carro) {
        if (carro == null) {
            Toast.makeText(getActivity(), "A matrícula "+matricula+" não foi encontrada na base de dados!",
                    Toast.LENGTH_SHORT).show();
            existeMatriculaBD = false;
        } else {
            existeMatriculaBD = true;
        }
    }

    public void checkRegistoFirebase(){
        mDatabaseRegistos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                registos = new ArrayList<>();

                for (DataSnapshot registosSnapshot: dataSnapshot.getChildren()) {
                    registo = registosSnapshot.getValue(Registo.class);
                    if (registo.getMatricula().equals(matricula)) {
                        checkRegistoIsNull(registo);
                    }
                }
                appendListToVar();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void checkRegistoIsNull(Registo registo) {
        if (registo == null) {
            Toast.makeText(getActivity(), "Não existe registo referente à matrícula "+matricula+" na base de dados!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        registos.add(new Registo(registo.getMatricula(), registo.getData(), registo.getRua(), registo.getZona()));
    }

    public void appendListToVar(){
        Iterator itr = registos.iterator();
        a = 0;

        while(itr.hasNext()){
            a++;
            Registo st=(Registo) itr.next();
            System.out.println(st.getRua()+" "+st.getZona()+" "+st.getData());

            if (a == 1){
                lastData = st.getData();
                lastRua = st.getRua();
                lastZona = st.getZona();
            } else if (a == 3){
                lastData2 = st.getData();
                lastRua2 = st.getRua();
            } else if (a == 5) {
                lastData3 = st.getData();
                lastRua3 = st.getRua();
                break;
            }

            a++;
        }

        setMessageToAlert();
    }

    public void setMessageToAlert(){
        if(a == 0){
            messageRegisto = "Não há registos nesta matrícula";
            messageInfo = "Não há registos nesta matrícula";
        } else if (a==1){
            messageRegisto = (lastRua+" , no dia "+lastData);
        } else if (a==3){
            messageRegisto = ((lastRua+" , no dia "+lastData)+"\n"+
                            (lastRua2+" , no dia "+lastData2));
            messageInfo = ("Zona: "+lastZona+"\nRua: "+lastRua+"\nParque pago até: "+lastData);
        } else if (a==5){
            messageRegisto = ((lastRua+" , no dia "+lastData)+"\n"+
                            (lastRua2+" , no dia "+lastData2)+"\n"+
                            (lastRua3+" , no dia "+lastData3));
            messageInfo = ("Zona: "+lastZona+"\nRua: "+lastRua+"\nParque pago até: "+lastData);
        }

        if(existeMatriculaBD){
            showInfoPay();
        }
    }

    public void buildMatricula(){
        String matriculaRaw = str_matricula.getText().toString();

        int numbers = 0;
        int letters = 0;

        if(!matricula.isEmpty()){
            matricula = (String.valueOf(matriculaRaw.charAt(0)) + matriculaRaw.charAt(1) +
                    "-" +
                    matriculaRaw.charAt(2) + matriculaRaw.charAt(3) +
                    "-" +
                    matriculaRaw.charAt(4) + matriculaRaw.charAt(5)).toUpperCase();

            for (int x = 0; x < matricula.length(); x++) {
                if (Character.isDigit(matricula.charAt(x))) {
                    numbers++;
                } else if (Character.isLetter(matricula.charAt(x))) {
                    letters++;
                }
            }

            if (numbers == 4 && letters == 2 &&
                    (Character.isLetter(matricula.charAt(0)) && Character.isLetter(matricula.charAt(1)) ||
                            Character.isLetter(matricula.charAt(3)) && Character.isLetter(matricula.charAt(4)) ||
                            Character.isLetter(matricula.charAt(6)) && Character.isLetter(matricula.charAt(7)))) {
                checkMatriculaFirebase();
                checkRegistoFirebase();
            } else {
                Toast.makeText(getActivity(), "Formato inválido!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Tem de inserir uma matrícula!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void showInfoPay(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true)
                .setTitle(matricula)
                .setMessage(messageInfo)
                .setNeutralButton("+", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showRegisto();
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.txt_coima), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Toast.makeText(getActivity(), "A imprimir ticket de coima para a matrícula: "+matricula,
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setPositiveButton(getActivity().getString(R.string.txt_aviso), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Toast.makeText(getActivity(), "A imprimir ticket de aviso para a matrícula: "+matricula,
                                Toast.LENGTH_LONG).show();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showRegisto(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true)
                .setTitle(getActivity().getString(R.string.txt_registo))
                .setMessage(messageRegisto)
                .setNeutralButton("+", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showInfoCar();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showInfoCar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true)
                .setTitle(getActivity().getString(R.string.txt_infoAdicional))
                .setMessage(getActivity().getString(R.string.txt_marca)+" "+carro.getMarca()+"\n"+
                            getActivity().getString(R.string.txt_modelo)+" "+carro.getModelo()+"\n"+
                            getActivity().getString(R.string.txt_tipo)+" "+carro.getTipo()+"\n"+
                            getActivity().getString(R.string.txt_data)+" "+carro.getDataRegistoAutomovel()+"\n"+
                            getActivity().getString(R.string.txt_cor)+" "+carro.getCor());

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void clearScreen (){
        str_matricula.setText("");
    }

    public void delLastDigit (){
        String str = str_matricula.getText().toString();
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        str_matricula.setText(str);
    }
}