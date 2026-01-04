/*
* هذا اول مشروع لي  بجافا فلو فيه اخطا صححو لي و بس والله
* (┬┬﹏┬┬)(┬┬﹏┬┬)*/
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.io.*;

class Task {
    private String title;
    private boolean done;

    public Task(String title, boolean done) {
        this.title = title;
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public void toggleDone() {
        done = !done;
    }

    public String toStorageLine() {
        return (done ? "1" : "0") + "|" + title;
    }

    public static Task fromStorageLine(String line) {
        String[] parts = line.split("\\|", 2);
        if (parts.length != 2) return null;

        boolean done = "1".equals(parts[0].trim());
        String title = parts[1];
        return new Task(title, done);
    }

    @Override
    public String toString() {
        return (done ? "[x] " : "[ ] ") + title;
    }
}

public class TodoGUI {
    private static final String STORAGE_FILE = "tasks.txt";

    private final DefaultListModel<Task> model = new DefaultListModel<>();
    private final JList<Task> list = new JList<>(model);
    private final JTextField input = new JTextField();


    private final Color purple = new Color(98, 0, 234);
    private final Color gold   = new Color(255, 193, 7);
    private final Color bg     = new Color(245, 245, 245);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TodoGUI().show());
    }

    private void show() {
        loadTasks();

        JFrame frame = new JFrame("WIN To-Do");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(620, 460);
        frame.setLocationRelativeTo(null);

        JLabel logoLabel = new JLabel(loadScaledLogo("img/win.png", 120, 120));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton addBtn = new JButton("Add");

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(bg);
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        styleButton(addBtn, purple, Color.WHITE);

        top.add(logoLabel, BorderLayout.NORTH);
        top.add(input, BorderLayout.CENTER);
        top.add(addBtn, BorderLayout.EAST);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        list.setBackground(Color.WHITE);

        JScrollPane center = new JScrollPane(list);
        center.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JButton toggleBtn = new JButton("Toggle Done");
        JButton deleteBtn = new JButton("Delete");
        JButton saveBtn   = new JButton("Save");

        styleButton(toggleBtn, gold, Color.BLACK);
        styleButton(deleteBtn, new Color(220, 53, 69), Color.WHITE);
        styleButton(saveBtn, purple, Color.WHITE);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        bottom.setBackground(bg);
        bottom.add(toggleBtn);
        bottom.add(deleteBtn);
        bottom.add(saveBtn);


        addBtn.addActionListener(e -> addTask());
        input.addActionListener(e -> addTask());
        toggleBtn.addActionListener(e -> toggleDone());
        deleteBtn.addActionListener(e -> deleteTask());
        saveBtn.addActionListener(e -> saveTasks());


        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) toggleDone();
            }
        });


        frame.getContentPane().setBackground(bg);
        frame.setLayout(new BorderLayout());
        frame.add(top, BorderLayout.NORTH);
        frame.add(center, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void addTask() {
        String title = input.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Title can't be empty.");
            return;
        }
        model.addElement(new Task(title, false));
        input.setText("");
        saveTasks();
    }

    private void toggleDone() {
        int idx = list.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Select a task first.");
            return;
        }
        Task t = model.get(idx);
        t.toggleDone();
        model.set(idx, t);
        saveTasks();
    }

    private void deleteTask() {
        int idx = list.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Select a task first.");
            return;
        }
        model.remove(idx);
        saveTasks();
    }

    private void loadTasks() {
        File file = new File(STORAGE_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                Task t = Task.fromStorageLine(line);
                if (t != null) model.addElement(t);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading tasks: " + e.getMessage());
        }
    }

    private void saveTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) tasks.add(model.get(i));

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(STORAGE_FILE), StandardCharsets.UTF_8))) {

            for (Task t : tasks) {
                bw.write(t.toStorageLine());
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving tasks: " + e.getMessage());
        }
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private ImageIcon loadScaledLogo(String path, int w, int h) {
        File f = new File(path);
        if (!f.exists()) {
            return new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        }
        Image img = new ImageIcon(path).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
