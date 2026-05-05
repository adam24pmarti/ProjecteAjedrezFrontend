
package reservasajedrez.model;


public class Usuari {
    private String id;
    private String username;
    private String role;
    private String email;

    public Usuari(String id, String username, String role, String email) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
}