package johanp.sgd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import johanp.Domain.Models.User;
import johanp.Domain.Services.FileF.FIleService;
import johanp.Domain.Services.FileF.IFileService;
import johanp.Domain.Services.UserF.IUserService;
import johanp.Domain.Services.UserF.UserService;

public class SGD {

    private static final int PUERTO = 8083;
    private static final int PUERTO_SOCKET = 5000;  // Puerto del servidor de sockets para el broadcast
    private static final String SERVICIO_NOMBRE = "fileService";
    private static final String FILE_DIRECTORY = "/home/cliente/share/";

    private static ConcurrentHashMap<User, Socket> usuariosActivosSockets = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<User, Thread> usuariosActivosHilos = new ConcurrentHashMap<>();
    private static ExecutorService poolHilos = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try {
            IUserService userService = new UserService();
   
            System.setProperty("java.rmi.server.hostname", "192.168.1.15");

            Registry registryFileService = LocateRegistry.createRegistry(PUERTO);
            IFileService fileService = new FIleService();
            registryFileService.rebind(SERVICIO_NOMBRE, fileService);

            System.out.println("FileService corriendo en el puerto: " + PUERTO);

            iniciarServidorBroadcast();

        } catch (RemoteException e) {
            System.out.println("Error de RMI: " + e.getMessage());
        }
    }

    // Método para iniciar el servidor de sockets para el broadcast
    public static void iniciarServidorBroadcast() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PUERTO_SOCKET)) {
                // Mensaje indicando que el servidor de sockets está corriendo
                System.out.println("Servidor de sockets corriendo en el puerto: " + PUERTO_SOCKET);
                
                while (true) {
                    // Aceptar conexiones de clientes
                    Socket socket = serverSocket.accept();
                    
                    // Suponemos que el cliente envía primero su nombre al conectarse
                    ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                    
                    User nuevoUsuario = new User(0, "Usuario");
    
                    usuariosActivosSockets.put(nuevoUsuario, socket);
                    System.out.println("Usuario " + nuevoUsuario.getName() + " conectado al servidor de broadcast.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para enviar mensajes a todos los usuarios conectados
    public static void enviarBroadcast(String mensaje) {
        usuariosActivosSockets.forEach((usuario, socket) -> {
            try {
                ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                salida.writeObject("Mensaje para " + usuario.getName() + ": " + mensaje);
                salida.flush();
            } catch (IOException e) {
                e.printStackTrace();
                // Si falla la comunicación con un usuario, lo removemos de la lista
                usuariosActivosSockets.remove(usuario);
            }
        });
    }

    // Método para agregar un usuario y asignarle un hilo
    public static void agregarUsuario(User user) {
        if (!usuariosActivosHilos.containsKey(user)) {
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

            usuariosActivosHilos.put(user, hiloUsuario);
            System.out.println("Usuario " + user.getName() + " ha sido añadido con el hilo " + hiloUsuario.getId());
        } else {
            System.out.println("Usuario " + user.getName() + " ya está activo.");
        }
    }

    // Método para eliminar un usuario y detener su hilo
    public static void eliminarUsuario(User user) {
        if (usuariosActivosHilos.containsKey(user)) {
            Thread hilo = usuariosActivosHilos.get(user);
            hilo.interrupt();
            usuariosActivosHilos.remove(user);
            System.out.println("Usuario " + user.getName() + " ha sido removido.");
        } else {
            System.out.println("Usuario " + user.getName() + " no está activo.");
        }
    }

    // Método para reactivar un hilo de usuario inactivo
    public static void reactivarUsuario(User user) {
        if (usuariosActivosHilos.containsKey(user)) {
            Thread hilo = usuariosActivosHilos.get(user);
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
