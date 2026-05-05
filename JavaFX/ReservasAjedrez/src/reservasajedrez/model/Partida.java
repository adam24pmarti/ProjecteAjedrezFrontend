package reservasajedrez.model;

public class Partida {

    private String id;
    private String player1Id;
    private String player2Id;
    private String winnerId;
    private Mesa table;
    private boolean finalitzada;

    public Partida(String id, String player1Id, String player2Id, String winnerId, Mesa table, boolean finalitzada) {
        this.id = id;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.winnerId = winnerId;
        this.table = table;
        this.finalitzada = finalitzada;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public Mesa getTable() {
        return table;
    }

    public void setTable(Mesa table) {
        this.table = table;
    }

    public boolean isFinalitzada() {
        return finalitzada;
    }

    public void setFinalitzada(boolean finalitzada) {
        this.finalitzada = finalitzada;
    }
    
    
}
