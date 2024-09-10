/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package johanp.Domain.Services.FileF;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import johanp.Domain.Models.File;
import johanp.sgd.SGD;  // Para usar el método enviarBroadcast

public class FIleService extends UnicastRemoteObject implements IFileService {

    private static final String FILE_DIRECTORY = "/home/cliente/share/";

    public FIleService() throws RemoteException {
        super();
    }

    @Override
    public byte[] getFile(String fileName) throws RemoteException {
        java.io.File fileToRead = new java.io.File(FILE_DIRECTORY + fileName);
        try (FileInputStream fileInput = new FileInputStream(fileToRead)) {
            byte[] fileData = new byte[(int) fileToRead.length()];
            fileInput.read(fileData);
            return fileData;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Error al leer el archivo: " + e.getMessage());
        }
    }

    @Override
    public String addFile(File file, String userName) throws RemoteException {
        String userDirectoryPath = FILE_DIRECTORY + userName + "/";
        java.io.File userDirectory = new java.io.File(userDirectoryPath);
        if (!userDirectory.exists()) {
            if (!userDirectory.mkdirs()) {
                return "Error: No se pudo crear el directorio del usuario " + userName;
            }
        }
        java.io.File fileToSave = new java.io.File(userDirectoryPath + file.getName());

        try (FileOutputStream fileOutput = new FileOutputStream(fileToSave)) {
            byte[] fileData = file.getContent();
            fileOutput.write(fileData);

            // Notificar a los usuarios que un archivo ha sido subido
            String mensaje = "El archivo " + file.getName() + " ha sido subido por " + userName;
            SGD.enviarBroadcast(mensaje);  // Broadcast a todos los usuarios

            return "Archivo añadido correctamente: " + file.getName();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error añadiendo el archivo: " + e.getMessage();
        }
    }

    public String deleteFile(String fileName, String userName) throws RemoteException {
        java.io.File fileToDelete = new java.io.File(FILE_DIRECTORY + fileName);
        if (fileToDelete.delete()) {
            // Notificar a los usuarios que un archivo ha sido eliminado
            String mensaje = "El archivo " + fileName + " ha sido eliminado por " + userName;
            SGD.enviarBroadcast(mensaje);  // Broadcast a todos los usuarios

            return "Archivo eliminado correctamente: " + fileName;
        } else {
            return "Error al eliminar el archivo: " + fileName;
        }
    }

    // Método para modificar archivo (agregado como ejemplo)
    public String modifyFile(File file, String userName) throws RemoteException {
        String filePath = FILE_DIRECTORY + userName + "/" + file.getName();
        java.io.File fileToModify = new java.io.File(filePath);

        if (fileToModify.exists()) {
            try (FileOutputStream fileOutput = new FileOutputStream(fileToModify)) {
                fileOutput.write(file.getContent());

                // Notificar a los usuarios que un archivo ha sido modificado
                String mensaje = "El archivo " + file.getName() + " ha sido modificado por " + userName;
                SGD.enviarBroadcast(mensaje);  // Broadcast a todos los usuarios

                return "Archivo modificado correctamente: " + file.getName();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error al modificar el archivo: " + e.getMessage();
            }
        } else {
            return "El archivo no existe: " + file.getName();
        }
    }

    @Override
    public boolean searchFile(String fileName) throws RemoteException {
        java.io.File file = new java.io.File(FILE_DIRECTORY + fileName);
        return file.exists();
    }

    @Override
    public void asignarPermisos(File file, String userName, int permiso) {
        // Aquí implementas la lógica para guardar los permisos en la base de datos.
        // Permiso 1 = Lector, Permiso 2 = Escritor.
        System.out.println("Permisos asignados para el archivo: " + file.getName() + " al usuario: " + userName);
    }
}
