/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package johanp.Domain.Services.UserF;

import java.rmi.Remote;
import java.rmi.RemoteException;

import johanp.Domain.Models.User;

/**
 *
 * @author johan
 */
public interface IUserService extends Remote{
    User login(String userName, String pass) throws RemoteException;

        // Métodos para manejar administradores
        User loginAdmin(String adminName) throws RemoteException;
        String registrarAdmin(String adminName) throws RemoteException;
        
        // Métodos para usuarios normales
        String registrarUsuario(String userName, String password, int role, long adminId) throws RemoteException;
        String eliminarUsuario(String userName) throws RemoteException;

}
