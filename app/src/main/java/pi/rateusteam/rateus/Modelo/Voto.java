package pi.rateusteam.rateus.Modelo;

public class Voto {

    private float votoCreatividad;
    private float votoViabilidad;
    private float votoComunicacion;
    private String idVotante;

    public Voto(){};

    public Voto(float votoCreatividad, float votoViabilidad, float votoComunicacion, String idVotante) {
        this.votoCreatividad = votoCreatividad;
        this.votoViabilidad = votoViabilidad;
        this.votoComunicacion = votoComunicacion;
        this.idVotante = idVotante;
    }

    public double getVotoCreatividad() {
        return votoCreatividad;
    }

    public void setVotoCreatividad(float votoCreatividad) {
        this.votoCreatividad = votoCreatividad;
    }

    public double getVotoViabilidad() {
        return votoViabilidad;
    }

    public void setVotoViabilidad(float votoViabilidad) {
        this.votoViabilidad = votoViabilidad;
    }

    public double getVotoComunicacion() {
        return votoComunicacion;
    }

    public void setVotoComunicacion(float votoComunicacion) {
        this.votoComunicacion = votoComunicacion;
    }

    public String getIdVotante() {
        return idVotante;
    }

    public void setIdVotante(String idVotante) {
        this.idVotante = idVotante;
    }
}
