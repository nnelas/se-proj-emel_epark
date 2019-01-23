package pt.se_ulusofona.se_proj_emel_epark.controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import pt.se_ulusofona.se_proj_emel_epark.R;
import pt.se_ulusofona.se_proj_emel_epark.model.Registo;
import pt.se_ulusofona.se_proj_emel_epark.utils.ListViewAdapter;

import static android.content.ContentValues.TAG;
import static pt.se_ulusofona.se_proj_emel_epark.utils.Constants.estadoPagamento;
import static pt.se_ulusofona.se_proj_emel_epark.utils.Constants.matricula;

/**
 * Created by nunonelas on 05/06/17.
 */

public class RuaFragment extends Fragment{

    private ArrayList<HashMap<String, String>> list;

    private DatabaseReference mDatabaseRegistos;

    private ListView listView;

    private EditText mTextLocation;

    private Registo registo;

    private TextView str_nexpirados;

    SwipeRefreshLayout mSwipeRefreshLayout;

    private ImageButton sadImg;

    private int nRegistos;

    public RuaFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rua, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);

        listView = (ListView)view.findViewById(R.id.carros_list);

        mTextLocation = (EditText) getActivity().findViewById(R.id.place_autocomplete_search_input);

        mDatabaseRegistos = FirebaseDatabase.getInstance().getReference("registos");

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         refreshContent();
                                                     }

                                                 });

        str_nexpirados = (TextView) view.findViewById(R.id.str_nexpirados);
        str_nexpirados.setText("Insira uma rua...");

        sadImg = (ImageButton) view.findViewById(R.id.btn_sad);

        sadImg.setVisibility(View.GONE);

        return view;
    }

    public void refreshContent(){
        mDatabaseRegistos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list=new ArrayList<HashMap<String,String>>();
                nRegistos = 0;

                for (DataSnapshot registosSnapshot: dataSnapshot.getChildren()) {
                    registo = registosSnapshot.getValue(Registo.class);
                    if (registo.getRua().equals(mTextLocation.getText().toString())) {
                        nRegistos++;
                        insertIntoHashMap(registo);
                    }
                }

                if (nRegistos == 0) {
                    setVisibleError();
                } else {
                    setHideError();
                }

                ListViewAdapter adapter=new ListViewAdapter(getActivity(), list);
                listView.setAdapter(adapter);

                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void setVisibleError(){
        str_nexpirados.setText("Não existem carros listados nesta rua");
        sadImg.setVisibility(View.VISIBLE);
        Log.d(TAG, "registo vazio");
    }

    public void setHideError(){
        str_nexpirados.setText(getString(R.string.str_nexpirados, nRegistos));
        sadImg.setVisibility(View.GONE);
    }
    public void insertIntoHashMap(Registo registo){
        HashMap<String,String> temp=new HashMap<String, String>();
        temp.put(matricula, registo.getMatricula());
        temp.put(estadoPagamento, registo.getData());
        list.add(temp);
    }
}