package reservasajedrez;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import reservasajedrez.model.Mesa;

public class MesaTableController implements Initializable {

    @FXML private TableView<Mesa> tablaMesas;
    @FXML private TableColumn<Mesa, String> colId, colNom;
    @FXML private TableColumn<Mesa, Boolean> colDisponibilidad;

    private ObservableList<Mesa> listaMesas = FXCollections.observableArrayList();
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        configurarColumnaDisponibilidad();

        obtenirTaulesAPI();
    }

    private void configurarColumnaDisponibilidad() {
        colDisponibilidad.setCellValueFactory(new PropertyValueFactory<>("disponibilidad"));

        colDisponibilidad.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean disponible, boolean empty) {
                super.updateItem(disponible, empty);
                if (empty || disponible == null) {
                    setGraphic(null);
                } else {
                    Button btn = new Button(disponible ? "Disponible" : "Ocupada");
                    btn.setStyle(disponible ? "-fx-background-color: #2ecc71; -fx-text-fill: white;" 
                                           : "-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    
                    btn.setOnAction(event -> {
                        Mesa mesa = getTableView().getItems().get(getIndex());
                        canviEstatTaula(mesa);
                    });
                    setGraphic(btn);
                }
            }
        });
    }

    private void obtenirTaulesAPI() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/mesas/cerca"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
                        listaMesas.clear();
                        for (JsonElement el : array) {
                            JsonObject obj = el.getAsJsonObject();
                            Mesa m = new Mesa();
                            m.setId(obj.get("id").getAsString());
                            m.setNom(obj.get("nom").getAsString());
                            m.setDisponibilidad(obj.get("disponibilidad").getAsBoolean());
                            listaMesas.add(m);
                        }
                        Platform.runLater(() -> tablaMesas.setItems(listaMesas));
                    }
                });
    }

    private void canviEstatTaula(Mesa mesa) {
        boolean nuevoEstado = !mesa.isDisponibilidad();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/mesas/" + mesa.getId() + "/disponibilidad?disponible=" + nuevoEstado))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> {
                            mesa.setDisponibilidad(nuevoEstado);
                            tablaMesas.refresh();
                        });
                    }
                });
    }
}