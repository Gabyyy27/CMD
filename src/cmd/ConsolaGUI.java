/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd;

/**
 *
 * @author Lourdes
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ConsolaGUI extends JFrame {
    private JTextArea outputTextArea;
    private JTextField commandTextField;
    private String currentFolder;

    public ConsolaGUI() {
        super("Consola de Comandos");

        currentFolder = System.getProperty("user.home");

        outputTextArea = new JTextArea(20, 50);
        outputTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        commandTextField = new JTextField();
        
        JLabel commandLabel = new JLabel("Comando:");

        JButton executeButton = new JButton("Ejecutar");
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarComando(commandTextField.getText());
            }
        });

        JButton clearButton = new JButton("Limpiar\n Consola");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputTextArea.setText("");
            }
        });

       
        executeButton.setPreferredSize(new Dimension(100, 25));
        clearButton.setPreferredSize(new Dimension(130, 25));
        commandTextField.setPreferredSize(new Dimension(400, 25));
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 0, 5); // Agregar espacio arriba y abajo del panel
        panel.add(commandLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 0, 10); 
        panel.add(commandTextField, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 10); 
        panel.add(executeButton, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        panel.add(clearButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 10, 10, 10); 
        panel.add(scrollPane, gbc);

        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); 

        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }



    private void ejecutarComando(String comando) {
        String[] partes = comando.split(" ");
        String comandoPrincipal = partes[0];

        switch (comandoPrincipal.toLowerCase()) {
            case "mkdir":
                crearCarpeta(partes[1]);
                break;
            case "mfile":
                crearArchivo(partes[1]);
                break;
            case "rm":
                eliminarCarpetaOArchivo(partes[1]);
                break;
            case "cd":
                cambiarCarpeta(partes[1]);
                break;
            case "dir":
                listarContenido();
                break;
            case "date":
                mostrarFecha();
                break;
            case "time":
                mostrarHora();
                break;
            case "Escribir<wr":
                if (partes.length > 1) {
                    escribirEnArchivo(partes[1]);
                }
                break;
            case "Leer<rd":
                if (partes.length > 1) {
                    leerArchivo(partes[1]);
                }
                break;
            default:
                mostrarMensaje("Ingrese un comando válido");
        }

        commandTextField.setText("");
    }

    private void crearCarpeta(String nombreCarpeta) {
        File carpeta = new File(currentFolder, nombreCarpeta);
        if (carpeta.mkdir()) {
            mostrarMensaje("Carpeta creada: " + carpeta.getAbsolutePath());
        } else {
            mostrarMensaje("Error: Esta carpeta ya existe");
        }
    }

    private void crearArchivo(String nombreArchivo) {
        File archivo = new File(currentFolder, nombreArchivo);
        try {
            if (archivo.createNewFile()) {
                mostrarMensaje("Archivo creado: " + archivo.getAbsolutePath());
            } else {
                mostrarMensaje("Error: Este archivo ya existe");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eliminarCarpetaOArchivo(String nombre) {
        File archivoOcarpeta = new File(currentFolder, nombre);
        if (archivoOcarpeta.exists()) {
            if (archivoOcarpeta.isDirectory()) {
                eliminarCarpeta(archivoOcarpeta);
            } else {
                if (archivoOcarpeta.delete()) {
                    mostrarMensaje("Archivo eliminado: " + archivoOcarpeta.getAbsolutePath());
                } else {
                    mostrarMensaje("Error al eliminar el archivo");
                }
            }
        } else {
            mostrarMensaje("El archivo o carpeta no existe");
        }
    }

    private void eliminarCarpeta(File carpeta) {
        File[] archivos = carpeta.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                eliminarCarpeta(archivo);
            }
        }
        if (carpeta.delete()) {
            mostrarMensaje("Carpeta eliminada: " + carpeta.getAbsolutePath());
        } else {
            mostrarMensaje("Error al eliminar la carpeta");
        }
    }

    private void cambiarCarpeta(String nombreCarpeta) {
        File nuevaCarpeta = new File(currentFolder, nombreCarpeta);
        if (nuevaCarpeta.isDirectory()) {
            currentFolder = nuevaCarpeta.getAbsolutePath();
            mostrarMensaje("Carpeta actual cambiada a: " + currentFolder);
        } else {
            mostrarMensaje("La carpeta no existe");
        }
    }

    private void listarContenido() {
        File carpetaActual = new File(currentFolder);
        File[] archivos = carpetaActual.listFiles();
        if (archivos != null) {
            mostrarMensaje("Contenido de " + currentFolder + ":");
            for (File archivo : archivos) {
                mostrarMensaje(archivo.getName());
            }
        } else {
            mostrarMensaje("La carpeta está vacía");
        }
    }

    private void mostrarFecha() {
        mostrarMensaje("Fecha actual: " + java.time.LocalDate.now());
    }

    private void mostrarHora() {
        mostrarMensaje("Hora actual: " + java.time.LocalTime.now());
    }

    private void escribirEnArchivo(String nombreArchivo) {
        try {
            FileWriter escritor = new FileWriter(new File(currentFolder, nombreArchivo), true);
            escritor.write(commandTextField.getText().substring(11)); 
            escritor.close();
            mostrarMensaje("Texto escrito en el archivo");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al escribir en el archivo");
        }
    }

    private void leerArchivo(String nombreArchivo) {
        try {
            Path path = Paths.get(currentFolder, nombreArchivo);
            List<String> lineas = Files.readAllLines(path);
            mostrarMensaje("Contenido de " + nombreArchivo + ":");
            for (String linea : lineas) {
                mostrarMensaje(linea);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al leer el archivo");
        }
    }

    private void mostrarMensaje(String mensaje) {
        outputTextArea.append(mensaje + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConsolaGUI();
            }
        });
    }
}

