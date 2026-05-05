package reservasajedrez.model;

public class Mesa {

    private String id;
    private String nom;
    private boolean disponibilidad;

    public Mesa(String id, String nom, boolean disponibilidad) {
        this.id = id;
        this.nom = nom;
        this.disponibilidad = disponibilidad;
    }

    public Mesa() {
    }
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
    
}
