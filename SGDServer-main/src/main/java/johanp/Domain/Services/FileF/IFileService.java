/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package johanp.Domain.Services.FileF;
import java.rmi.Remote;
import java.rmi.RemoteException;

import johanp.Domain.Models.File;

/**
 *
 * @author johan
 */
public interface IFileService extends Remote{
    byte[] getFile(String fileName) throws RemoteException;
    String addFile(File file, String userName) throws RemoteException;
    String deleteFile(String fileName) throws RemoteException;
    boolean searchFile(String fileName) throws RemoteException;
    // Permisos de archivos
    void asignarPermisos(File file, String userName, int permiso) throws RemoteException;
}




