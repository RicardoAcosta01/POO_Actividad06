package Contactos;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class GestorContactos {
    private static final String FILE_NAME = "friendsContact.txt";

    // 1. CREATE: Crear / Añadir un nuevo contacto
    public static boolean crearContacto(String newName, String newNumber) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }

        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        boolean found = false;

        while (raf.getFilePointer() < raf.length()) {
            String nameNumberString = raf.readLine();
            if (nameNumberString == null || nameNumberString.trim().isEmpty()) {
                continue;
            }
            String[] lineSplit = nameNumberString.split("!");
            if (lineSplit.length >= 2) {
                String name = lineSplit[0];
                if (name.equalsIgnoreCase(newName.trim())) {
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            String nameNumberString = newName.trim() + "!" + newNumber.trim();
            raf.seek(raf.length());
            raf.writeBytes(nameNumberString + System.lineSeparator());
            raf.close();
            return true; // Creado exitosamente
        } else {
            raf.close();
            return false; // El contacto ya existe
        }
    }

    // 2. READ: Buscar un contacto específico por nombre
    public static String buscarContacto(String searchName) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return null;
        }

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        String resultNumber = null;

        while (raf.getFilePointer() < raf.length()) {
            String nameNumberString = raf.readLine();
            if (nameNumberString == null || nameNumberString.trim().isEmpty()) {
                continue;
            }
            String[] lineSplit = nameNumberString.split("!");
            if (lineSplit.length >= 2) {
                String name = lineSplit[0];
                String number = lineSplit[1];
                if (name.equalsIgnoreCase(searchName.trim())) {
                    resultNumber = number;
                    break;
                }
            }
        }

        raf.close();
        return resultNumber;
    }

    // READ ALL: Obtener lista completa de contactos
    public static List<Contacto> obtenerTodos() throws IOException {
        List<Contacto> lista = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return lista;
        }

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        while (raf.getFilePointer() < raf.length()) {
            String nameNumberString = raf.readLine();
            if (nameNumberString == null || nameNumberString.trim().isEmpty()) {
                continue;
            }
            String[] lineSplit = nameNumberString.split("!");
            if (lineSplit.length >= 2) {
                lista.add(new Contacto(lineSplit[0], lineSplit[1]));
            }
        }
        raf.close();
        return lista;
    }

    // 3. UPDATE: Actualizar el número de un contacto existente
    public static boolean actualizarContacto(String newName, String newNumber) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return false;
        }

        File tmpFile = new File("temp.txt");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        RandomAccessFile tmpraf = new RandomAccessFile(tmpFile, "rw");
        boolean updated = false;

        raf.seek(0);
        while (raf.getFilePointer() < raf.length()) {
            String nameNumberString = raf.readLine();
            if (nameNumberString == null || nameNumberString.trim().isEmpty()) {
                continue;
            }
            String[] lineSplit = nameNumberString.split("!");
            if (lineSplit.length >= 2) {
                String name = lineSplit[0];
                if (name.equalsIgnoreCase(newName.trim())) {
                    nameNumberString = newName.trim() + "!" + newNumber.trim();
                    updated = true;
                }
            }
            tmpraf.writeBytes(nameNumberString + System.lineSeparator());
        }

        raf.seek(0);
        tmpraf.seek(0);

        while (tmpraf.getFilePointer() < tmpraf.length()) {
            raf.writeBytes(tmpraf.readLine() + System.lineSeparator());
        }

        raf.setLength(tmpraf.length());
        tmpraf.close();
        raf.close();
        tmpFile.delete();

        return updated;
    }

    // 4. DELETE: Eliminar un contacto por nombre
    public static boolean eliminarContacto(String deleteName) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return false;
        }

        File tmpFile = new File("temp.txt");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        RandomAccessFile tmpraf = new RandomAccessFile(tmpFile, "rw");
        boolean deleted = false;

        raf.seek(0);
        while (raf.getFilePointer() < raf.length()) {
            String nameNumberString = raf.readLine();
            if (nameNumberString == null || nameNumberString.trim().isEmpty()) {
                continue;
            }
            String[] lineSplit = nameNumberString.split("!");
            if (lineSplit.length >= 2) {
                String name = lineSplit[0];
                if (name.equalsIgnoreCase(deleteName.trim())) {
                    deleted = true;
                    continue; // Omite escribirlo en el archivo temporal
                }
            }
            tmpraf.writeBytes(nameNumberString + System.lineSeparator());
        }

        raf.seek(0);
        tmpraf.seek(0);

        while (tmpraf.getFilePointer() < tmpraf.length()) {
            raf.writeBytes(tmpraf.readLine() + System.lineSeparator());
        }

        raf.setLength(tmpraf.length());
        tmpraf.close();
        raf.close();
        tmpFile.delete();

        return deleted;
    }
}
