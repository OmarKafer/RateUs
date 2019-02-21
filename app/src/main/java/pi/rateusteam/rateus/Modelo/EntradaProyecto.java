package pi.rateusteam.rateus.Modelo;

public class EntradaProyecto {
    private String titulo;
    private int axisX;
    private float media;

    public EntradaProyecto() {}

    public EntradaProyecto(String titulo, int axisX, float media) {
        this.titulo = titulo;
        this.axisX = axisX;
        this.media = media;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAxisX() {
        return axisX;
    }

    public void setAxisX(int axisX) {
        this.axisX = axisX;
    }

    public float getMedia() {
        return media;
    }

    public void setMedia(float media) {
        this.media = media;
    }
}
