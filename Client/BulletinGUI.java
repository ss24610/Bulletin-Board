import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class BulletinGUI {

    private JFrame frame;
    private JTextField hostField, portField;
    private JButton connectButton, disconnectButton;

    private JTextArea outputArea;
    private JTextField commandField;
    private JButton sendButton;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

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
        disconnectButton.setEnabled(false);

        topPanel.add(new JLabel("Host:"));
        topPanel.add(hostField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(connectButton);
        topPanel.add(disconnectButton);

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
        sendButton.addActionListener(e -> sendCommand());
        commandField.addActionListener(e -> sendCommand());

        frame.setVisible(true);
    }

    private void connect() {
        try {
            String host = hostField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());

            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read initial server message (board + note dimensions + colors)
            String initMessage = in.readLine();
            outputArea.append("CONNECTED\n");
            outputArea.append(initMessage + "\n\n");

            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            sendButton.setEnabled(true);

        } catch (Exception ex) {
            showError("Unable to connect to server.");
        }
    }

    private void disconnect() {
        try {
            if (out != null) {
                out.println("DISCONNECT");
            }
        } catch (Exception ignored) {}

        cleanup();
        outputArea.append("DISCONNECTED\n");

        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
        sendButton.setEnabled(false);
    }

    private void sendCommand() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) return;

        try {
            out.println(command);

            String response = in.readLine();
            if (response == null) {
                showError("Server closed connection.");
                cleanup();
                return;
            }

            outputArea.append(">> " + command + "\n");
            outputArea.append(response + "\n");

            // Handle multi-line OK responses (OK <n>)
            if (response.startsWith("OK ")) {
                String[] parts = response.split(" ");
                if (parts.length == 2) {
                    int lines = Integer.parseInt(parts[1]);
                    for (int i = 0; i < lines; i++) {
                        outputArea.append(in.readLine() + "\n");
                    }
                }
            }

            outputArea.append("\n");
            commandField.setText("");

        } catch (IOException e) {
            showError("Connection error.");
            cleanup();
        }
    }

    private void cleanup() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BulletinGUI::new);
    }
}
