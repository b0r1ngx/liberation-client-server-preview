package ru.tinkoff.semenov;

import static ru.tinkoff.semenov.MainHandler.SEPARATOR;

public class Utils {

    public static String getStatusInDefaultMessages(String message) {
        int index = message.indexOf(SEPARATOR);
        if (index == -1) {
            return message;
        }
        return message.substring(0, index);
    }

    public static String getCommandInRoomMessages(String message) {
        int index = message.indexOf(SEPARATOR);
        if (index == -1) {
            return "";
        }
        return message.substring(index + 1).split("\\|")[0];
    }

    public static String getPlayerInRoomMessages(String message) {
        int index = message.indexOf(SEPARATOR);
        if (index == -1) {
            return message;
        }
        return message.substring(0, index);
    }

    public static String[] getArgs(String message) {
        int index = message.indexOf(SEPARATOR);
        if (index == -1) {
            return new String[0];
        }
        return message.substring(index + 1).split("\\|");
    }

    public static String getArgsAsStringInRoom(String message) {
        String[] msgArray = message.split("\\|");
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < msgArray.length; i++) {
            sb.append(msgArray[i]);
        }
        return sb.toString();
    }
}
