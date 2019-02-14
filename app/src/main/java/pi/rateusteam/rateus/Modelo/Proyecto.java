package pi.rateusteam.rateus.Modelo;

import android.net.Uri;

public class Proyecto {

    private String titulo;
    private String descripcion;
    private String idUsuario;
    private String ciclo;

    public Proyecto(){}

    public Proyecto(String titulo, String descripcion, String idUsuario, String ciclo) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.ciclo = ciclo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }
}
