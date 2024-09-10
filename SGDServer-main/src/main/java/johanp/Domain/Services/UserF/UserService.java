/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package johanp.Domain.Services.UserF;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import johanp.Domain.Models.DatabaseConnection;
import johanp.Domain.Models.User;

public class UserService extends UnicastRemoteObject implements IUserService {

    public UserService() throws RemoteException {
        super();
    }

    // Registro de administrador solo con el nombre
    public String registrarAdmin(String adminName) throws RemoteException {
        String sql = "INSERT INTO admins (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, adminName);
            stmt.executeUpdate();
            return "Administrador registrado exitosamente con el nombre: " + adminName;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error al registrar administrador: " + e.getMessage());
        }
    }

    // Login para administradores
    public User loginAdmin(String adminName) throws RemoteException {
        String sql = "SELECT id, name FROM admins WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, adminName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int adminId = rs.getInt("id");
                    String adminNameDb = rs.getString("name");
                    return new User(adminId, adminNameDb);  // Retorna objeto User
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error durante login del administrador: " + e.getMessage());
        }
        return null;  // Retorna null si el login falla
    }

    @Override
    public User login(String userName, String pass) throws RemoteException {
        String sql = "SELECT id, name FROM normal_users WHERE name = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            stmt.setString(2, pass);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String username = rs.getString("name");
                    return new User(userId, username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error durante login: " + e.getMessage());
        }
        return null;
    }

    // Registro de usuarios normales por el administrador
    public String registrarUsuario(String userName, String password, int role, long adminId) throws RemoteException {
        String sql = "INSERT INTO normal_users (name, password, role, admin_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            stmt.setString(2, password);
            stmt.setInt(3, role);  // Role: 1 -> Lector, 2 -> Escritor
            stmt.setLong(4, adminId);
            stmt.executeUpdate();
            return "Usuario registrado exitosamente.";
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error al registrar usuario: " + e.getMessage());
        }
    }

    // Eliminar usuario normal
    public String eliminarUsuario(String userName) throws RemoteException {
        String sql = "DELETE FROM normal_users WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Usuario " + userName + " eliminado correctamente.";
            } else {
                return "Usuario " + userName + " no encontrado.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error al eliminar usuario: " + e.getMessage());
        }
    }
}