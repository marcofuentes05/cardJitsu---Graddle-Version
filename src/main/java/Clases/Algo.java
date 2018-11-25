package Clases;

public class Algo {
    private String nombre;
    private String apellido;
    private int edad;

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public int getEdad() {
        return edad;
    }

    public String toString(){
    String r = "";
    r = nombre+" "+apellido+"  \nEdad: "+edad;
    return r;
}
}
