package com.pdm.recycle.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.pdm.recycle.control.ConfiguracaoFirebase;

import java.text.SimpleDateFormat;
import java.util.Collection;

public class Descarte {
    private String idDescarte;
    private Double latitude;
    private Double longitude;
    private String tipoResiduo;
    private String status;
    private String userEmail;
    private String dataDescarte;
    private String UsuarioAutenticado;
    private Usuario usuario;

    public Descarte(){
    }

    public void salvarDescarte(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference descarte =  firebaseRef.child("descartes").child(idDescarte);

        descarte.setValue(this);
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Exclude
    public String getidDescarte() {
        return idDescarte;
    }

    public void setidDescarte(String idDescarte) {
        this.idDescarte = idDescarte;
    }

    public String getTipoResiduo() {
        return tipoResiduo;
    }

    public void setTipoResiduo(String tipoResiduo) {
        this.tipoResiduo = tipoResiduo;
    }

    public Usuario getUsuario(String emailUserAutenticado) {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDataDescarte() {
        return dataDescarte;
    }

    public void setDataDescarte(String dataDescarte) {
        this.dataDescarte = dataDescarte;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsuarioAutenticado() {
        return UsuarioAutenticado;
    }

    public void setUsuarioAutenticado(String usuarioAutenticado) {
        UsuarioAutenticado = usuarioAutenticado;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
