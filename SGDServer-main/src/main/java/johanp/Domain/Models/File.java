/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package johanp.Domain.Models;

/**
 *
 * @author johan
 */
import java.io.Serializable;

public class File implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private long size;
    private String path;
    //private String content;
    private byte[] content; 

    // Constructor
    public File(String name, long size, String path, byte[] content) {
        this.name = name;
        this.size = size;
        this.path = path;
        this.content = content;
    }
    public File(String name)
    {
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", path='" + path + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

