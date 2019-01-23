package pt.se_ulusofona.se_proj_emel_epark.utils;

import static android.content.ContentValues.TAG;
import static pt.se_ulusofona.se_proj_emel_epark.utils.Constants.matricula;
import static pt.se_ulusofona.se_proj_emel_epark.utils.Constants.estadoPagamento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import pt.se_ulusofona.se_proj_emel_epark.R;

/**
 * Created by nunonelas on 05/06/17.
 */

public class ListViewAdapter extends BaseAdapter{

    private ArrayList<HashMap<String, String>> list;
    private Activity activity;

    private TextView txtMatricula;
    private TextView txtEstado;
    private ImageButton image;

    public ListViewAdapter(Activity activity,ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){

            convertView = inflater.inflate(R.layout.activity_listcarros, null);

            image = (ImageButton) convertView.findViewById(R.id.btn_corpagamento);
            txtMatricula = (TextView) convertView.findViewById(R.id.str_matricula);
            txtEstado = (TextView) convertView.findViewById(R.id.str_estado);

        }

        HashMap<String, String> map=list.get(position);
        txtMatricula.setText(map.get(matricula));
        txtEstado.setText(map.get(estadoPagamento));

        Calendar rightNow = Calendar.getInstance();
        int dia = rightNow.get(Calendar.DAY_OF_MONTH);
        int mes = rightNow.get(Calendar.MONTH);
        int ano = rightNow.get(Calendar.YEAR);

        String data = Integer.toString(dia) +
                "-" +
                Integer.toString(mes) +
                "-" +
                Integer.toString(ano);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date date1 = format.parse(data);
            Date date2 = format.parse(map.get(estadoPagamento));

            //date2 is earlier than date1
            if (date2.compareTo(date1) <= 0) {
                image.setBackgroundResource(R.drawable.ic_radio_button_red);
            } else if (date1.compareTo(date2) <= 0){
                image.setBackgroundResource(R.drawable.ic_radio_button_green);
            }
        } catch (ParseException e){
            Log.e(TAG, "Erro ao comparar datas");
        }

        return convertView;
    }

}
