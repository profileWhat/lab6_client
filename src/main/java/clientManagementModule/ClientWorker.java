package clientManagementModule;

import commands.ReceivedCommand;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;


/**
 * Class of working with the client
 */
public class ClientWorker {
    private final Socket client;
    public ClientWorker(Socket client) {
        this.client = client;
    }
    public void start() {
        InputDeviceWorker.getInputDevice().readCommands(this, System.in);
    }

    public void sendCommand(ReceivedCommand receivedCommand) {
        try {
            try(ByteArrayOutputStream byteArrayOStream = new ByteArrayOutputStream()) {
                try (ObjectOutputStream out = new ObjectOutputStream(byteArrayOStream)) {
                    out.writeObject(receivedCommand);
                    ByteBuffer buffer = ByteBuffer.wrap(byteArrayOStream.toByteArray());
                    client.getChannel().write(buffer);
                }
            }
        } catch (IOException e) {
            OutputDeviceWorker.getDescriber().describeException(e);
        }
    }

    public String getMessage() {
        boolean EndScriptExFlag = false;
        StringBuilder message = new StringBuilder();
        try {
            InputStream in = client.getInputStream();
            byte[] buf = new byte[1];
            while(in.read(buf) != -1){
                if (buf[0] == 1) EndScriptExFlag = true;
                message.append(new String(buf));
                if (in.available() <= 0) break;
            }
        } catch (IOException e) {
            OutputDeviceWorker.getDescriber().describeException(e);
        }
        if (EndScriptExFlag) return "EndScriptExFlag";
        return message.toString();
    }
}