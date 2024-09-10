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

            // Guardar permisos en la base de datos
            asignarPermisos(file, userName, 2);  // Asignamos permiso de escritura como ejemplo

            return "Archivo añadido correctamente: " + file.getName();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error añadiendo el archivo: " + e.getMessage();
        }
    }
    
    @Override
    public void asignarPermisos(File file, String userName, int permiso) {
        // Aquí implementas la lógica para guardar los permisos en la base de datos.
        // Permiso 1 = Lector, Permiso 2 = Escritor.
        System.out.println("Permisos asignados para el archivo: " + file.getName() + " al usuario: " + userName);
    }

    @Override
    public String deleteFile(String fileName) throws RemoteException {
        java.io.File fileToDelete = new java.io.File(FILE_DIRECTORY + fileName);
        if (fileToDelete.delete()) {
            return "Archivo eliminado correctamente: " + fileName;
        } else {
            return "Error al eliminar el archivo: " + fileName;
        }
    }

    @Override
    public boolean searchFile(String fileName) throws RemoteException {
        java.io.File file = new java.io.File(FILE_DIRECTORY + fileName);
        return file.exists();
    }
}