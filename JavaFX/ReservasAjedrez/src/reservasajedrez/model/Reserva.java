package reservasajedrez.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {

    private String id;
    private Mesa mesa;
    private LocalTime hora; // HH:MM
    private LocalDate fecha; // AAAA-MM-DD
    private Usuari jugador;
    private Partida partida;

    public Reserva(String id, Mesa mesa, LocalTime hora, LocalDate fecha, Usuari jugador, Partida partida) {
        this.id = id;
        this.mesa = mesa;
        this.hora = hora;
        this.fecha = fecha;
        this.jugador = jugador;
        this.partida = partida;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Usuari getJugador() {
        return jugador;
    }

    public void setJugador(Usuari jugador) {
        this.jugador = jugador;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }
    
    
}
