package Contactos;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class VentanaContactos extends JFrame implements ActionListener {
    private Container contenedor;
    private JLabel lblNombre, lblNumero, lblLista;
    private JTextField campoNombre, campoNumero;
    private JButton btnCreate, btnRead, btnUpdate, btnDelete, btnClear, btnListAll;
    private JTextArea areaSalida;
    private JScrollPane scrollSalida;

    public VentanaContactos() {
        inicio();
        setTitle("Gestión de Contactos - CRUD (RandomAccessFile)");
        setSize(480, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void inicio() {
        contenedor = getContentPane();
        contenedor.setLayout(null);

        // Etiquetas y Campos
        lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 20, 100, 23);
        campoNombre = new JTextField();
        campoNombre.setBounds(140, 20, 290, 23);

        lblNumero = new JLabel("Teléfono / Número:");
        lblNumero.setBounds(30, 60, 120, 23);
        campoNumero = new JTextField();
        campoNumero.setBounds(140, 60, 290, 23);

        // Botones CRUD
        btnCreate = new JButton("Create (Añadir)");
        btnCreate.setBounds(30, 100, 130, 28);
        btnCreate.addActionListener(this);

        btnRead = new JButton("Read (Buscar)");
        btnRead.setBounds(175, 100, 130, 28);
        btnRead.addActionListener(this);

        btnUpdate = new JButton("Update (Actualizar)");
        btnUpdate.setBounds(320, 100, 130, 28);
        btnUpdate.addActionListener(this);

        btnDelete = new JButton("Delete (Eliminar)");
        btnDelete.setBounds(30, 140, 130, 28);
        btnDelete.addActionListener(this);

        btnListAll = new JButton("Listar Todos");
        btnListAll.setBounds(175, 140, 130, 28);
        btnListAll.addActionListener(this);

        btnClear = new JButton("Limpiar Campos");
        btnClear.setBounds(320, 140, 130, 28);
        btnClear.addActionListener(this);

        // Área de salida de datos / registros
        lblLista = new JLabel("Registros en friendsContact.txt:");
        lblLista.setBounds(30, 185, 250, 23);

        areaSalida = new JTextArea();
        areaSalida.setEditable(false);
        scrollSalida = new JScrollPane(areaSalida);
        scrollSalida.setBounds(30, 215, 420, 230);

        contenedor.add(lblNombre);
        contenedor.add(campoNombre);
        contenedor.add(lblNumero);
        contenedor.add(campoNumero);

        contenedor.add(btnCreate);
        contenedor.add(btnRead);
        contenedor.add(btnUpdate);
        contenedor.add(btnDelete);
        contenedor.add(btnListAll);
        contenedor.add(btnClear);

        contenedor.add(lblLista);
        contenedor.add(scrollSalida);

        actualizarListaVisual();
    }

    private void actualizarListaVisual() {
        try {
            List<Contacto> lista = GestorContactos.obtenerTodos();
            areaSalida.setText("");
            if (lista.isEmpty()) {
                areaSalida.append("El archivo 'friendsContact.txt' está vacío o no ha sido creado.\n");
            } else {
                for (Contacto c : lista) {
                    areaSalida.append(c.toString() + "\n");
                }
            }
        } catch (IOException e) {
            areaSalida.setText("Error al leer el archivo: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        campoNombre.setText("");
        campoNumero.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String nombre = campoNombre.getText().trim();
        String numero = campoNumero.getText().trim();

        if (e.getSource() == btnCreate) {
            if (nombre.isEmpty() || numero.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar tanto el Nombre como el Teléfono para crear un contacto.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                boolean exito = GestorContactos.crearContacto(nombre, numero);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Contacto '" + nombre + "' creado exitosamente en friendsContact.txt.", "Éxito (Create)", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    actualizarListaVisual();
                } else {
                    JOptionPane.showMessageDialog(this, "El contacto '" + nombre + "' ya existe en el archivo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de E/S al crear contacto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == btnRead) {
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el Nombre del contacto a buscar.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String numEncontrado = GestorContactos.buscarContacto(nombre);
                if (numEncontrado != null) {
                    campoNumero.setText(numEncontrado);
                    JOptionPane.showMessageDialog(this, "Contacto encontrado:\nNombre: " + nombre + "\nTeléfono: " + numEncontrado, "Resultado (Read)", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "El contacto '" + nombre + "' no fue encontrado en friendsContact.txt.", "No Encontrado", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de E/S al buscar contacto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == btnUpdate) {
            if (nombre.isEmpty() || numero.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el Nombre y el nuevo Teléfono para actualizar.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                boolean exito = GestorContactos.actualizarContacto(nombre, numero);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Contacto '" + nombre + "' actualizado exitosamente con el nuevo teléfono: " + numero, "Éxito (Update)", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    actualizarListaVisual();
                } else {
                    JOptionPane.showMessageDialog(this, "El contacto '" + nombre + "' no existe para ser actualizado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de E/S al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == btnDelete) {
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el Nombre del contacto que desea eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                boolean exito = GestorContactos.eliminarContacto(nombre);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Contacto '" + nombre + "' eliminado exitosamente de friendsContact.txt.", "Éxito (Delete)", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    actualizarListaVisual();
                } else {
                    JOptionPane.showMessageDialog(this, "El contacto '" + nombre + "' no existe en el archivo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de E/S al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == btnListAll) {
            actualizarListaVisual();
        }

        if (e.getSource() == btnClear) {
            limpiarCampos();
        }
    }
}
