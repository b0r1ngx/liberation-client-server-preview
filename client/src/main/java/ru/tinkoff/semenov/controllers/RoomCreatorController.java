package ru.tinkoff.semenov.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import ru.tinkoff.semenov.Network;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class RoomCreatorController implements Initializable {

    private static long ROOM_COUNTER = 1;

    private Network network;
    private Map<String, Set<String>> lobby;
    private String newRoomName;

    @FXML
    private TextField newRoomField;
    @FXML
    private Text invalidMessage;
    private String nickname;

    @FXML
    private void onCreateNewRoom() {
        String newRoom = newRoomField.getText();

        if (!lobby.containsKey(newRoom)) {
            this.newRoomName = newRoom;
            network.createNewRoom(newRoom, nickname);

            closeCreator(newRoomField.getScene().getWindow());
        } else {
            invalidMessage.setText("Такая комната уже существует");
            invalidMessage.setVisible(true);
        }
    }

    @FXML
    private void cancelCreateNewRoom(ActionEvent event) {
        Button cancelButton = ((Button) event.getSource());
        closeCreator(cancelButton.getScene().getWindow());
    }

    private void closeCreator(Window window) {
        Platform.runLater(() ->
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST))
        );
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public String getNewRoomName() {
        return newRoomName;
    }

    public void setLobby(Map<String, Set<String>> lobby) {
        this.lobby = lobby;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newRoomField.setText("Комната #" + ROOM_COUNTER);
        ROOM_COUNTER++;
    }
}
