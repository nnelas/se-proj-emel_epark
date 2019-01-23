package pt.se_ulusofona.se_proj_emel_epark.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by nunonelas on 05/06/17.
 */

@IgnoreExtraProperties
public class Carro {

    private String matricula; //definido como AA-AA-AA
    private String modelo;
    private String marca;
    private String cor;
    private String tipo; //particular ou comercial
    private String dataRegistoAutomovel; //definido como MM-AA

    public Carro (){
        // Default constructor required for calls to DataSnapshot.getValue(Carro.class)
    }

    public Carro(String matricula, String modelo, String marca, String cor, String tipo, String dataRegistoAutomovel){
        this.matricula = matricula;
        this.modelo = modelo;
        this.marca = marca;
        this.cor = cor;
        this.tipo = tipo;
        this.dataRegistoAutomovel = dataRegistoAutomovel;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDataRegistoAutomovel() {
        return dataRegistoAutomovel;
    }

    public void setDataRegistoAutomovel(String dataRegistoAutomovel) {
        this.dataRegistoAutomovel = dataRegistoAutomovel;
    }
}
