package pi.rateusteam.rateus.Modelo;

import android.net.Uri;

public class Proyecto {

    private String titulo;
    private String descripcion;
    private String idUsuario;
    private String ciclo;
    private int axisXProyecto;
    private float media;
    private int numVotos;

    public Proyecto(){}

    public Proyecto(String titulo, String descripcion, String idUsuario, String ciclo, float media, int numVotos) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.ciclo = ciclo;
        axisXProyecto = 0;
        this.media = media;
        this.numVotos = numVotos;
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

    public int getAxisXProyecto() {
        return axisXProyecto;
    }

    public void setAxisXProyecto(int axisXProyecto) {
        this.axisXProyecto = axisXProyecto;
    }

    public float getMedia() {
        return media;
    }

    public void setMedia(float media) {
        this.media = media;
    }

    public int getNumVotos() {
        return numVotos;
    }

    public void setNumVotos(int numVotos) {
        this.numVotos = numVotos;
    }
}
