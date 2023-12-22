package ru.tinkoff.semenov.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import ru.tinkoff.semenov.Network;

public class RoomController {

    @FXML
    private Text firstPlayer;
    @FXML
    private Text secondPlayer;

    private Network network;

    public void setFirstPlayer(String nickname) {
        firstPlayer.setText(nickname);
    }

    public void setSecondPlayer(String enemyNickname) {
        secondPlayer.setText(enemyNickname);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
    @FXML
    private void buttonPressed(ActionEvent event) {
        network.buttonPressed(firstPlayer.getText());
    }

    public void sendSkipTurn() {
        network.skipTurn();
    }
}
