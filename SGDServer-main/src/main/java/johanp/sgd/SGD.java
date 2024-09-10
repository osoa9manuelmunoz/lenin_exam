package johanp.sgd;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import johanp.Domain.Models.User;
import johanp.Domain.Services.FileF.FIleService;
import johanp.Domain.Services.FileF.IFileService;

public class SGD {

    private static final int PUERTO = 8083;
    private static final String SERVICIO_NOMBRE = "fileService";
    private static final String FILE_DIRECTORY = "/home/cliente/share/";

    // Mapa para almacenar los usuarios y sus hilos
    private static ConcurrentHashMap<User, Thread> usuariosActivos = new ConcurrentHashMap<>();
    private static ExecutorService poolHilos = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "192.168.1.15");

            Registry registryFileService = LocateRegistry.createRegistry(PUERTO);
            IFileService fileService = new FIleService();
            registryFileService.rebind(SERVICIO_NOMBRE, fileService);

            System.out.println("FileService corriendo en el puerto: " + PUERTO);

        } catch (RemoteException e) {
            System.out.println("Error de RMI: " + e.getMessage());
        }
    }

    // Método para enviar mensajes a todos los usuarios conectados
    public static void enviarBroadcast(String mensaje) {
        usuariosActivos.forEach((usuario, hilo) -> {
            System.out.println("Enviando mensaje a " + usuario.getName() + ": " + mensaje);
        });
    }

    // Método para agregar un usuario y asignarle un hilo
    public static void agregarUsuario(User user) {
        if (!usuariosActivos.containsKey(user)) {
            Thread hiloUsuario = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        System.out.println("Hilo para el usuario " + user.getName() + " está activo.");
                        Thread.sleep(5000);  // Simula actividad
                    }
                } catch (InterruptedException e) {
                    System.out.println("Hilo del usuario " + user.getName() + " ha sido detenido.");
                }
            });
            poolHilos.execute(hiloUsuario);

            usuariosActivos.put(user, hiloUsuario);
            System.out.println("Usuario " + user.getName() + " ha sido añadido con el hilo " + hiloUsuario.getId());
        } else {
            System.out.println("Usuario " + user.getName() + " ya está activo.");
        }
    }

    // Método para eliminar un usuario y detener su hilo
    public static void eliminarUsuario(User user) {
        if (usuariosActivos.containsKey(user)) {
            Thread hilo = usuariosActivos.get(user);
            hilo.interrupt();
            usuariosActivos.remove(user);
            System.out.println("Usuario " + user.getName() + " ha sido removido.");
        } else {
            System.out.println("Usuario " + user.getName() + " no está activo.");
        }
    }

    // Método para reactivar un hilo de usuario inactivo
    public static void reactivarUsuario(User user) {
        if (usuariosActivos.containsKey(user)) {
            Thread hilo = usuariosActivos.get(user);
            if (!hilo.isAlive()) {
                agregarUsuario(user);  // Si el hilo está muerto, lo reactiva
            } else {
                System.out.println("El hilo del usuario " + user.getName() + " ya está activo.");
            }
        } else {
            agregarUsuario(user);  // Si no existe el usuario, lo agrega
        }
    }
}