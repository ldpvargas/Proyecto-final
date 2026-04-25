import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {

    private static final Scanner scanner = new Scanner(System.in);
    private static final GestorComunidad gestor = new GestorComunidad();

    public static void main(String[] args) {
        precargarDatos(); //Se agregan personas y actividades predeterminadas
        menuPrincipal(); 
    }

    private static void menuPrincipal() {
        int opcion;
        do {
            System.out.println("\n=== GESTOR DE COMUNIDAD ===");
            System.out.println("1. CRUD Personas");
            System.out.println("2. CRUD Actividades");
            System.out.println("3. Registrar asistencia");
            System.out.println("4. Reportes");
            System.out.println("5. Equipos");
            System.out.println("6. Busqueda y filtros");
            System.out.println("0. Salir");
            opcion = leerEntero("Elige una opcion: ");

            switch (opcion) {
                case 1:
                    menuPersonas();
                    break;
                case 2:
                    menuActividades();
                    break;
                case 3:
                    registrarAsistencia();
                    break;
                case 4:
                    menuReportes();
                    break;
                case 5:
                    menuEquipos();
                    break;
                case 6:
                    menuBusqueda();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private static void menuPersonas() {
        int opcion;
        do {
            System.out.println("\n--- CRUD PERSONAS ---");
            System.out.println("1. Crear miembro");
            System.out.println("2. Editar persona");
            System.out.println("3. Eliminar persona");
            System.out.println("4. Listar personas (ordenadas)");
            System.out.println("5. Promover miembro a lider");
            System.out.println("0. Volver");
            opcion = leerEntero("Elige una opcion: ");

            switch (opcion) {
                case 1:
                    crearMiembro();
                    break;
                case 2:
                    editarPersona();
                    break;
                case 3:
                    eliminarPersona();
                    break;
                case 4:
                    listarPersonas();
                    break;
                case 5:
                    promoverALider();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private static void menuActividades() {
        int opcion;
        do {
            System.out.println("\n--- CRUD ACTIVIDADES ---");
            System.out.println("1. Crear actividad");
            System.out.println("2. Editar actividad");
            System.out.println("3. Eliminar actividad");
            System.out.println("4. Listar actividades (por fecha)");
            System.out.println("0. Volver");
            opcion = leerEntero("Elige una opcion: ");

            switch (opcion) {
                case 1:
                    crearActividad();
                    break;
                case 2:
                    editarActividad();
                    break;
                case 3:
                    eliminarActividad();
                    break;
                case 4:
                    listarActividades();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private static void menuReportes() {
        System.out.println("\n--- REPORTES ---");

        System.out.println("Top 5 personas con mas asistencias:");
        List<Persona> top = gestor.obtenerTop5Asistencias();
        if (top.isEmpty()) {
            System.out.println("Sin asistencias registradas.");
        } else {
            for (int i = 0; i < top.size(); i++) {
                Persona p = top.get(i);
                System.out.println((i + 1) + ". " + p.getNombre() + " (id=" + p.getId() + ") -> "
                        + p.getAsistenciasRegistradas() + " asistencias");
            }
        }

        Optional<Actividad> mayor = gestor.actividadMayorOcupacion();
        if (mayor.isPresent()) {
            Actividad a = mayor.get();
            System.out.printf("Actividad con mayor ocupacion: %s (%.2f%%)%n", a.getNombre(), a.getPorcentajeOcupacion());
        } else {
            System.out.println("No hay actividades para calcular ocupacion.");
        }

        System.out.println("\nListado ordenado por nombre (TreeMap):");
        listarPersonas();
    }

    private static void menuEquipos() {
        int opcion;
        do {
            System.out.println("\n--- EQUIPOS ---");
            System.out.println("1. Crear equipo");
            System.out.println("2. Agregar persona a equipo");
            System.out.println("3. Listar equipos");
            System.out.println("0. Volver");
            opcion = leerEntero("Elige una opcion: ");

            switch (opcion) {
                case 1:
                    String nombreEquipo = leerTexto("Nombre del equipo: ");
                    if (gestor.crearEquipo(nombreEquipo)) {
                        System.out.println("Equipo creado.");
                    } else {
                        System.out.println("Ya existe un equipo con ese nombre.");
                    }
                    break;
                case 2:
                    String equipo = leerTexto("Nombre del equipo: ");
                    int idPersona = leerEntero("ID de la persona: ");
                    try {
                        gestor.agregarPersonaAEquipo(equipo, idPersona);
                        System.out.println("Persona agregada al equipo.");
                    } catch (EntidadNoEncontradaException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 3:
                    gestor.listarEquipos().forEach(System.out::println);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private static void menuBusqueda() {
        System.out.println("\n--- BUSQUEDA PARCIAL Y FILTROS ---");
        String termino = leerTexto("Nombre parcial (vacío = cualquiera): ");
        Integer edadMin = leerEnteroOpcional("Edad minima (vacío = sin filtro): ");
        Integer edadMax = leerEnteroOpcional("Edad maxima (vacío = sin filtro): ");
        String tipo = leerTexto("Tipo (MIEMBRO/LIDER, vacío = cualquiera): ").trim();
        if (tipo.isEmpty()) {
            tipo = null;
        }

        List<Persona> filtradas = gestor.buscarPersonasConFiltros(termino, edadMin, edadMax, tipo);
        if (filtradas.isEmpty()) {
            System.out.println("No se encontraron personas con esos criterios.");
            return;
        }

        for (Persona persona : filtradas) {
            System.out.println(persona);
        }
    }

    private static void crearMiembro() {
        String nombre = leerTexto("Nombre: ");
        int edad = leerEntero("Edad: ");
        String rol = leerTexto("Rol principal (ej: Musica, Logistica): ");
        Miembro miembro = gestor.crearMiembro(nombre, edad, rol);
        System.out.println("Miembro creado con id: " + miembro.getId());
    }

    private static void editarPersona() {
        int id = leerEntero("ID de la persona a editar: ");
        try {
            Persona actual = gestor.buscarPersonaPorId(id);
            System.out.println("Actual: " + actual);
            String nombre = leerTexto("Nuevo nombre: ");
            int edad = leerEntero("Nueva edad: ");
            String rol = leerTexto("Nuevo rol principal: ");
            gestor.editarPersona(id, nombre, edad, rol);
            System.out.println("Persona actualizada.");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void eliminarPersona() {
        int id = leerEntero("ID de la persona a eliminar: ");
        try {
            gestor.eliminarPersona(id);
            System.out.println("Persona eliminada.");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listarPersonas() {
        List<Persona> personas = gestor.listarPersonasOrdenadas();
        if (personas.isEmpty()) {
            System.out.println("No hay personas registradas.");
            return;
        }
        for (Persona p : personas) {
            System.out.println(p);
        }
    }

    private static void promoverALider() {
        int id = leerEntero("ID del miembro a promover: ");
        String especialidad = leerTexto("Especialidad del lider: ");
        try {
            Lider lider = gestor.promoverALider(id, especialidad);
            System.out.println("Promocion exitosa. Nuevo lider: " + lider);
        } catch (EntidadNoEncontradaException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void crearActividad() {
        String nombre = leerTexto("Nombre de actividad: ");
        LocalDate fecha = leerFecha("Fecha (yyyy-MM-dd): ");
        int cupo = leerEntero("Cupo maximo: ");
        Actividad actividad = gestor.crearActividad(nombre, fecha, cupo);
        System.out.println("Actividad creada con id: " + actividad.getId());
    }

    private static void editarActividad() {
        int id = leerEntero("ID de la actividad a editar: ");
        String nombre = leerTexto("Nuevo nombre: ");
        LocalDate fecha = leerFecha("Nueva fecha (yyyy-MM-dd): ");
        int cupo = leerEntero("Nuevo cupo: ");
        try {
            gestor.editarActividad(id, nombre, fecha, cupo);
            System.out.println("Actividad actualizada.");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void eliminarActividad() {
        int id = leerEntero("ID de la actividad a eliminar: ");
        try {
            gestor.eliminarActividad(id);
            System.out.println("Actividad eliminada.");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listarActividades() {
        List<Actividad> actividades = gestor.listarActividadesOrdenadasPorFecha();
        if (actividades.isEmpty()) {
            System.out.println("No hay actividades registradas.");
            return;
        }
        for (Actividad a : actividades) {
            System.out.println(a);
        }
    }

    private static void registrarAsistencia() {
        int personaId = leerEntero("ID persona: ");
        int actividadId = leerEntero("ID actividad: ");
        try {
            boolean registrada = gestor.registrarAsistencia(personaId, actividadId);
            if (registrada) {
                System.out.println("Asistencia registrada.");
            } else {
                System.out.println("La asistencia ya existia, no se duplico.");
            }
        } catch (EntidadNoEncontradaException | CupoLlenoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine();
            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.println("Debes ingresar un numero entero.");
            }
        }
    }

    private static Integer leerEnteroOpcional(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debes ingresar un numero entero o dejar vacio.");
            }
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private static LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return LocalDate.parse(entrada);
            } catch (DateTimeParseException e) {
                System.out.println("Fecha invalida. Formato esperado: yyyy-MM-dd");
            }
        }
    }

    //Se agregan personas y actividades predeterminadas
    private static void precargarDatos() {
        Miembro m1 = gestor.crearMiembro("Luis Parra", 23, "Logistica");
        Miembro m2 = gestor.crearMiembro("Eduardo Amador", 19, "Musica");
        Miembro m3 = gestor.crearMiembro("Jorge Ariel", 27, "Comunicacion");

        Actividad a1 = gestor.crearActividad("Curso Java", LocalDate.now().plusDays(3), 2);
        Actividad a2 = gestor.crearActividad("Progra", LocalDate.now().plusDays(7), 5);
        Actividad a3 = gestor.crearActividad("Taller redes", LocalDate.now().plusDays(2), 3);

        try {
            gestor.registrarAsistencia(m1.getId(), a1.getId());
            gestor.registrarAsistencia(m1.getId(), a2.getId());
            gestor.registrarAsistencia(m2.getId(), a2.getId());
            gestor.registrarAsistencia(m3.getId(), a3.getId());
        } catch (EntidadNoEncontradaException | CupoLlenoException e) {
            System.out.println("Error al precargar datos: " + e.getMessage());
        }
    }
}
