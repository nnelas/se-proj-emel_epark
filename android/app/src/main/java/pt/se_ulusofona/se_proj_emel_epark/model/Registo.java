package pt.se_ulusofona.se_proj_emel_epark.model;

import java.io.Serializable;

/**
 * Created by nunonelas on 13/06/17.
 */

public class Registo implements Serializable {

    private String matricula;
    private String data;
    private String rua;
    private String zona;

    public Registo(){
        // Default constructor required for calls to DataSnapshot.getValue(Registo.class)
    }

    public Registo(String matricula, String data, String rua, String zona){
        this.matricula = matricula;
        this.data = data;
        this.rua = rua;
        this.zona = zona;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }
}
