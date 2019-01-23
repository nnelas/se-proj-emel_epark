package pt.se_ulusofona.se_proj_emel_epark.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by jorgeloureiro on 22/06/17.
 */

@IgnoreExtraProperties
public class Utilizador{

    private String nome;
    private String email;

    public Utilizador(){
        //**//
    }

    public Utilizador(String nome, String email){
        this.nome = nome;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
