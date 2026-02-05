import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class BulletinGUI {

    private JFrame frame;
    private JTextField hostField, portField;
    private JButton connectButton, disconnectButton, shakeButton, clearButton;

    private JTextArea outputArea;
    private JTextField commandField;
    private JButton sendButton;



    private BulletinClient client;

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 4444;

    public BulletinGUI() {
        buildGUI();
    }

    private void buildGUI() {
        frame = new JFrame("Bulletin Board Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        // === Connection Panel ===
        JPanel topPanel = new JPanel(new FlowLayout());

        hostField = new JTextField("localhost", 10);
        portField = new JTextField("4444", 5);

        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        shakeButton = new JButton("Shake");
        clearButton = new JButton("Clear");
        disconnectButton.setEnabled(false);
        clearButton.setEnabled(false);
        shakeButton.setEnabled(false);

        topPanel.add(new JLabel("Host:"));
        topPanel.add(hostField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(connectButton);
        topPanel.add(disconnectButton);
        topPanel.add(shakeButton);
        topPanel.add(clearButton);

        // === Output Area ===
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // === Command Panel ===
        JPanel bottomPanel = new JPanel(new BorderLayout());
        commandField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);

        bottomPanel.add(commandField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // === Event Handlers ===
        connectButton.addActionListener(e -> connect());
        disconnectButton.addActionListener(e -> disconnect());
        shakeButton.addActionListener(e -> sendCommand("SHAKE"));
        clearButton.addActionListener(e -> sendCommand("CLEAR"));
        sendButton.addActionListener(e -> sendCommand());
        commandField.addActionListener(e -> sendCommand());

        frame.setVisible(true);
    }

    private void connect() {

        try {

            int port = Integer.parseInt(portField.getText().trim());
            String host = hostField.getText().trim();

            client = new BulletinClient(host, port);
            String initialMessage = client.get_initial_message();

            outputArea.append("CONNECTED\n");
            outputArea.append(initialMessage + "\n\n");

            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            shakeButton.setEnabled(true);
            clearButton.setEnabled(true);
            sendButton.setEnabled(true);

        }

        catch(Exception e){
            showError("UNABLE TO CONNECT TO SERVER.");
        }
    
    }
        
    private void disconnect() {

        outputArea.append("DISCONNECTED\n");
        try {
            if (client != null) {
                client.disconnect();
                
            }
        } catch (Exception ignored) {}
    
        cleanup();
        
    }

    private void sendCommand(String command){
        if (command.isEmpty()) return;

        try{
            String response = client.send_request(command);

            outputArea.append(">> " + command + "\n");
            outputArea.append(response + "\n");
            commandField.setText("");
        }

        catch (IOException e) {
            showError("CONNECTION ERROR.");
            cleanup();
        }

        catch(Exception e){
            showError("ERROR SENDING COMMAND.");
        }

    }

    private void sendCommand(){
        String command = commandField.getText().trim();
        if (command.isEmpty()) return;

        try{
            String response = client.send_request(command);

            outputArea.append(">> " + command + "\n");
            outputArea.append(response + "\n");
            commandField.setText("");
        }

        catch (IOException e) {
            showError("CONNECTION ERROR.");
            cleanup();
        }

        catch(Exception e){
            showError("ERROR SENDING COMMAND.");
        }

    }

    private void cleanup() {
        client = null;
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
        shakeButton.setEnabled(false);
        clearButton.setEnabled(false);
        sendButton.setEnabled(false);
        
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BulletinGUI::new);
    }
}
