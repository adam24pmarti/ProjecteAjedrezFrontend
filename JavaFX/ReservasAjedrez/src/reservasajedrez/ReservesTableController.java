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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import reservasajedrez.model.Reserva;

public class ReservesTableController implements Initializable {

    @FXML
    private TableView<Reserva> tablaReserves;
    @FXML
    private TableColumn<Reserva, String> colId;
    @FXML
    private TableColumn<Reserva, String> colJugador; // Sacaremos el nombre del objeto Usuari
    @FXML
    private TableColumn<Reserva, String> colMesa;    // Sacaremos el ID/Número de la Mesa
    @FXML
    private TableColumn<Reserva, String> colFecha;
    @FXML
    private TableColumn<Reserva, String> colHora;

    private ObservableList<Reserva> listaReserves = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));

        colJugador.setCellValueFactory(cellData -> {
            Reserva r = cellData.getValue();
            if (r.getJugador() != null && r.getJugador().getUsername() != null) {
                return new SimpleStringProperty(r.getJugador().getUsername());
            } else {
                return new SimpleStringProperty("Sin asignar");
            }
        });

        colMesa.setCellValueFactory(cellData -> {
            Reserva r = cellData.getValue();
            if (r.getMesa() != null && r.getMesa().getNom() != null) {
                return new SimpleStringProperty(r.getMesa().getNom());
            } else {
                return new SimpleStringProperty("Sense Taula");
            }
        });

        obtenerReservesDesdeAPI();
    }

    private void obtenerReservesDesdeAPI() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/reservas/totes"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
                    listaReserves.clear();

                    for (JsonElement elemento : jsonArray) {
                        JsonObject obj = elemento.getAsJsonObject();

                        listaReserves.add(new Reserva(
                                obj.get("id").getAsString(),
                                null,
                                LocalTime.parse(obj.get("hora").getAsString()),
                                LocalDate.parse(obj.get("fecha").getAsString()),
                                null,
                                null
                        ));
                    }
                    Platform.runLater(() -> tablaReserves.setItems(listaReserves));
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    @FXML
    private void cancelarReserva() {
        Reserva seleccionada = tablaReserves.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atenció", "Selecciona una reserva per cancel·lar.");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/reservas/delete/" + seleccionada.getId()))
                .DELETE()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        listaReserves.remove(seleccionada);
                        mostrarAlerta("Èxit", "Reserva cancel·lada pel administrador.");
                    });
                });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
}
