package Clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main {
    public static void main (String [] args){
        /**String json = "{'nombre':'Marco', 'apellido':'Fuentes', 'edad': 19 }";
        Gson gson = new Gson();
        Algo empleado = gson.fromJson(json, Algo.class);
        System.out.println(empleado.toString());**/
        //Primero se hace la conexion con la base de datos para que se puedan obtener los datos.
        Connection c = null;
        Statement stmt = null;

        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres","");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            //String query = "SELECT USUARIO, CONTRASENA FROM USUARIOS WHERE "
            //Se define una lista que contiene todos los jugadores de la base de datos
            //Y otra que unicamente contiene los jugadres que jugaran esta partida
            //ArrayList<Usuario> usuarios = new ArrayList();
            ArrayList<Usuario> jugadores = new ArrayList();
            //Estos usuarios sirvIERONen de prueba.
            //usuarios.add(new Usuario("Marco","Cinco","hola@gmail.com"));
            //usuarios.add(new Usuario("Chus","Seis","adios@gmail.com"));
            Scanner input = new Scanner(System.in);
            Scanner input1= new Scanner(System.in);
            Scanner input2 = new Scanner(System.in);
            for (int a = 0;a <2;a++){
                //Se hace el inicio de sesion para ambos usuarios, verificando usuarios y contraseñas
                Boolean datos = false;
                while (datos==false){
                    System.out.println("Ingrese su usuario "+(a+1)+":");
                    String usuario = input.nextLine();
                    System.out.println("Intrese su contraseña "+(a+1)+": ");
                    String contrasena = input1.nextLine();
                    String query = "SELECT USUARIO, CONTRASENA, EMAIL, DECK, PERFIL FROM USUARIOS WHERE Usuario = '"+usuario+"' AND  Contrasena = '"+contrasena+"'";
                    try{
                        ResultSet rs = stmt.executeQuery(query);
                        //if (rs.next()){
                        datos = true;
                        String user = "";
                        String password = "";
                        String email = "";
                        String deck = "";
                        String perfil = "";
                        Perfil temporal;
                        Deck deckt;
                        Gson gson = new Gson();
                        //Este ciclo solo se implementara una vez. Puesto que el resultSet solo contiene un Elemento
                        try{
                            System.out.println(rs.next());
                            user = rs.getString("usuario");
                            password = rs.getString("contrasena");
                            email = rs.getString("email");
                            deck = rs.getString("deck");
                            String perfil0 = rs.getString("perfil");
                            perfil = perfil0.replace("\"","'");
                            Type tipoListaCartas = new TypeToken<ArrayList<Carta>>(){}.getType();
                            ArrayList<Carta> temp = gson.fromJson(deck, tipoListaCartas);
                            deckt = new Deck(temp);
                            temporal = gson.fromJson(perfil, Perfil.class);
                            jugadores.add(new Usuario(user,password,email,deckt, temporal));
                        }catch(Exception e){
                            //System.out.println("JAJA SALU2");
                            e.printStackTrace();
                        }
                        rs.close();
                    }catch(Exception e){
                        System.out.println("No se pudo ejecutar la instruccion del query");
                        e.printStackTrace();
                    }
                }
            }
            System.out.println(jugadores.size());
            for(int i=0;i<jugadores.size();i++){
                System.out.println(jugadores.get(i).getUsuario()+"\n"+jugadores.get(i).getPerfil().toString()+"\n");
            }
            //TODO AQUI SE CIERRA LA CONEXION A BD
            stmt.close();
            c.close();
        if (jugadores.size()!=2){
            System.out.println("Hay datos incorrectos!");
        }else{
            System.out.println("Bienvenidos ambos");

            Scanner sc = new Scanner (System.in);
            Boolean hayModo = true;
            String modoDeJuego = "";
            while (hayModo){
                System.out.println("¿Qué modo de juego quieren jugar? 1/2");
                String m = sc.nextLine().toString();

                if (m.equals("1")){
                    modoDeJuego = "modo1";
                    hayModo = false;
                }else if (m.equals("2")){
                    modoDeJuego = "modo2";
                    hayModo = false;
                }else{
                    System.out.println("Ese valor es incorrecto...");
                }
            }

            //Aqui comienza la lógica del juego
            ModoDeJuego modo = new ModoDeJuego(modoDeJuego);
            Arena arena = new Arena(jugadores, modo);

            Boolean sigue = true;
            while (sigue){
                ArrayList<Carta> cartasEnJuego = new ArrayList<>();
                for (int a  = 0; a<2;a++){
                    System.out.println("Jugador "+(a+1));
                    System.out.println(jugadores.get(a).toString());
                    System.out.print("Ingrese el numero de su elección de carta: ");
                    try{
                        int respuesta = input2.nextInt()-1;
                        cartasEnJuego.add(jugadores.get(a).getDeck().getCartasVisibles().get(respuesta));
                        jugadores.get(a).getDeck().usarCarta(cartasEnJuego.get(a));
                    }catch(Exception e){
                        System.out.println("Ese no es u dato valido...");
                    }
                }
                Boolean juez = arena.getModoDeJuego().encuentro(cartasEnJuego.get(0),cartasEnJuego.get(1));
                //TODO el metodo encuentro debe considerar el elemento de las cartas
                System.out.println("El ganador es: "+ juez);
                //En esta parte se agregan las cartas ganadas por los jugadores a sus respectivas matrices con el método add()
                try{
                    if (juez){
                        System.out.println("Carta 1: "+cartasEnJuego.get(0).getNumero());
                        System.out.println("Carta 2: "+cartasEnJuego.get(1).getNumero());
                        jugadores.get(0).cartasGanadasPartida.add(cartasEnJuego.get(0));
                        jugadores.get(0).add(cartasEnJuego.get(0));
                    }else{
                        jugadores.get(1).cartasGanadasPartida.add(cartasEnJuego.get(1));
                        jugadores.get(1).add(cartasEnJuego.get(1));
                    }
                }catch(Exception e0) {
                    System.out.println("Empate");
                }

                //Aqui se verifica si ya hay un ganador
                if (arena.getModoDeJuego().juez(jugadores.get(0),jugadores.get(1)) == null){
                    sigue = true;
                }else {
                    if (arena.getModoDeJuego().juez(jugadores.get(0),jugadores.get(1)) == true){
                        System.out.println("Jugador 1 ha ganado");
                    }else{
                        System.out.println("Jugador 2 ha ganado");
                    }
                    sigue = false;
                }
                modo.pasoRonda();
            }
        }
        }catch (Exception e){
            System.out.println("No se pudo conectar con la BD");
        }
    }

}