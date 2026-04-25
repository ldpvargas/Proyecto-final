import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class GestorComunidad {

    private static int siguienteIdPersona = 1;
    private static int siguienteIdActividad = 1;

    private final HashMap<Integer, Persona> personasPorId;
    private final ArrayList<Actividad> actividades;
    private final HashSet<String> rolesRegistrados;
    private final TreeMap<String, Persona> personasOrdenadasPorNombre;
    private final TreeSet<Actividad> actividadesOrdenadasPorFecha;
    private final HashMap<Integer, Integer> asistenciasPorPersona;
    private final HashMap<String, Equipo> equiposPorNombre;

    public GestorComunidad() {
        this.personasPorId = new HashMap<>();
        this.actividades = new ArrayList<>();
        this.rolesRegistrados = new HashSet<>();
        this.personasOrdenadasPorNombre = new TreeMap<>();
        this.actividadesOrdenadasPorFecha = new TreeSet<>();
        this.asistenciasPorPersona = new HashMap<>();
        this.equiposPorNombre = new HashMap<>();
    }

    public Miembro crearMiembro(String nombre, int edad, String rolPrincipal) {
        Miembro miembro = new Miembro(siguienteIdPersona++, nombre, edad, rolPrincipal);
        guardarPersona(miembro);
        registrarRol(rolPrincipal);
        return miembro;
    }

    public Persona buscarPersonaPorId(int id) throws EntidadNoEncontradaException {
        Persona persona = personasPorId.get(id);
        if (persona == null) {
            throw new EntidadNoEncontradaException("No existe persona con id=" + id);
        }
        return persona;
    }

    public void editarPersona(int id, String nombre, int edad, String rolPrincipal) throws EntidadNoEncontradaException {
        Persona persona = buscarPersonaPorId(id);
        eliminarClaveOrdenadaPersona(persona);
        persona.setNombre(nombre);
        persona.setEdad(edad);
        persona.setRolPrincipal(rolPrincipal);
        registrarRol(rolPrincipal);
        indexarPersonaPorNombre(persona);
    }

    public void eliminarPersona(int id) throws EntidadNoEncontradaException {
        Persona persona = buscarPersonaPorId(id);
        personasPorId.remove(id);
        eliminarClaveOrdenadaPersona(persona);
        asistenciasPorPersona.remove(id);

        for (Actividad actividad : actividades) {
            actividad.retirarAsistencia(id);
        }

        for (Equipo equipo : equiposPorNombre.values()) {
            List<Persona> actuales = new ArrayList<>(equipo.getMiembros());
            for (Persona miembro : actuales) {
                if (miembro.getId() == id) {
                    // Equipo expone lista inmutable, se reconstruye al crear nuevo equipo.
                    // Para mantener la solucion simple, se vuelve a crear sin la persona.
                    recrearEquipoSinPersona(equipo.getNombre(), id);
                    break;
                }
            }
        }
    }

    public List<Persona> listarPersonasOrdenadas() {
        return new ArrayList<>(personasOrdenadasPorNombre.values());
    }

    public Actividad crearActividad(String nombre, LocalDate fecha, int cupo) {
        Actividad actividad = new Actividad(siguienteIdActividad++, nombre, fecha, cupo);
        actividades.add(actividad);
        actividadesOrdenadasPorFecha.add(actividad);
        return actividad;
    }

    public Actividad buscarActividadPorId(int id) throws EntidadNoEncontradaException {
        for (Actividad actividad : actividades) {
            if (actividad.getId() == id) {
                return actividad;
            }
        }
        throw new EntidadNoEncontradaException("No existe actividad con id=" + id);
    }

    public void editarActividad(int id, String nombre, LocalDate fecha, int cupo) throws EntidadNoEncontradaException {
        Actividad actividad = buscarActividadPorId(id);
        actividadesOrdenadasPorFecha.remove(actividad);
        actividad.setNombre(nombre);
        actividad.setFecha(fecha);
        actividad.setCupo(cupo);
        actividadesOrdenadasPorFecha.add(actividad);
    }

    public void eliminarActividad(int id) throws EntidadNoEncontradaException {
        Actividad actividad = buscarActividadPorId(id);
        actividades.remove(actividad);
        actividadesOrdenadasPorFecha.remove(actividad);
    }

    public List<Actividad> listarActividadesOrdenadasPorFecha() {
        return new ArrayList<>(actividadesOrdenadasPorFecha);
    }

    public boolean registrarAsistencia(int personaId, int actividadId)
            throws EntidadNoEncontradaException, CupoLlenoException {
        Persona persona = buscarPersonaPorId(personaId);
        Actividad actividad = buscarActividadPorId(actividadId);

        boolean registrada = actividad.registrarAsistencia(personaId);
        if (registrada) {
            asistenciasPorPersona.merge(personaId, 1, Integer::sum);
            persona.incrementarAsistencias();
            persona.notificar("Asistencia registrada en: " + actividad.getNombre());
        }
        return registrada;
    }

    public List<Persona> obtenerTop5Asistencias() {
        List<Persona> personas = new ArrayList<>(personasPorId.values());
        personas.sort(Comparator
                .comparingInt(Persona::getAsistenciasRegistradas)
                .reversed()
                .thenComparing(Persona::getNombre, String.CASE_INSENSITIVE_ORDER));
        return personas.subList(0, Math.min(5, personas.size()));
    }

    public Optional<Actividad> actividadMayorOcupacion() {
        return actividades.stream()
                .max(Comparator.comparingDouble(Actividad::getPorcentajeOcupacion));
    }

    public List<Persona> buscarPersonasConFiltros(String termino, Integer edadMin, Integer edadMax, String tipo) {
        String terminoNormalizado = termino == null ? "" : termino.trim().toLowerCase(Locale.ROOT);
        String tipoNormalizado = tipo == null ? "" : tipo.trim().toUpperCase(Locale.ROOT);

        List<Persona> resultado = new ArrayList<>();
        for (Persona persona : personasOrdenadasPorNombre.values()) {
            boolean coincideNombre = terminoNormalizado.isEmpty()
                    || persona.getNombre().toLowerCase(Locale.ROOT).contains(terminoNormalizado);
            boolean coincideEdadMin = edadMin == null || persona.getEdad() >= edadMin;
            boolean coincideEdadMax = edadMax == null || persona.getEdad() <= edadMax;
            boolean coincideTipo = tipoNormalizado.isEmpty() || persona.getTipo().equalsIgnoreCase(tipoNormalizado);

            if (coincideNombre && coincideEdadMin && coincideEdadMax && coincideTipo) {
                resultado.add(persona);
            }
        }
        return resultado;
    }

    // Reglas de elegibilidad extra: edad >= 21 y al menos 3 asistencias.
    public boolean esElegibleParaLider(Persona persona) {
        return persona.getEdad() >= 21 && persona.getAsistenciasRegistradas() >= 3;
    }

    public Lider promoverALider(int personaId, String especialidad)
            throws EntidadNoEncontradaException {
        Persona persona = buscarPersonaPorId(personaId);
        if (persona instanceof Lider) {
            throw new IllegalArgumentException("La persona ya es lider.");
        }
        if (!esElegibleParaLider(persona)) {
            throw new IllegalArgumentException(
                    "No cumple elegibilidad para lider (edad >= 21 y asistencias >= 3).");
        }

        Lider lider = new Lider(
                persona.getId(),
                persona.getNombre(),
                persona.getEdad(),
                persona.getRolPrincipal(),
                especialidad);
        lider.setAsistenciasRegistradas(persona.getAsistenciasRegistradas());

        personasPorId.put(lider.getId(), lider);
        eliminarClaveOrdenadaPersona(persona);
        indexarPersonaPorNombre(lider);
        return lider;
    }

    public boolean crearEquipo(String nombreEquipo) {
        String clave = normalizar(nombreEquipo);
        if (equiposPorNombre.containsKey(clave)) {
            return false;
        }
        equiposPorNombre.put(clave, new Equipo(nombreEquipo));
        return true;
    }

    public void agregarPersonaAEquipo(String nombreEquipo, int personaId) throws EntidadNoEncontradaException {
        Equipo equipo = equiposPorNombre.get(normalizar(nombreEquipo));
        if (equipo == null) {
            throw new EntidadNoEncontradaException("No existe equipo con nombre='" + nombreEquipo + "'");
        }
        Persona persona = buscarPersonaPorId(personaId);
        equipo.agregarMiembro(persona);
    }

    public List<Equipo> listarEquipos() {
        return new ArrayList<>(equiposPorNombre.values());
    }

    public Set<String> getRolesRegistrados() {
        return new HashSet<>(rolesRegistrados);
    }

    private void guardarPersona(Persona persona) {
        personasPorId.put(persona.getId(), persona);
        asistenciasPorPersona.putIfAbsent(persona.getId(), 0);
        indexarPersonaPorNombre(persona);
    }

    private void registrarRol(String rolPrincipal) {
        rolesRegistrados.add(normalizar(rolPrincipal));
    }

    private void indexarPersonaPorNombre(Persona persona) {
        personasOrdenadasPorNombre.put(claveOrdenNombre(persona), persona);
    }

    private void eliminarClaveOrdenadaPersona(Persona persona) {
        personasOrdenadasPorNombre.remove(claveOrdenNombre(persona));
    }

    private String claveOrdenNombre(Persona persona) {
        return normalizar(persona.getNombre()) + "#" + persona.getId();
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
    }

    private void recrearEquipoSinPersona(String nombreEquipo, int personaIdExcluir) {
        Equipo original = equiposPorNombre.get(normalizar(nombreEquipo));
        if (original == null) {
            return;
        }
        Equipo nuevo = new Equipo(original.getNombre());
        for (Persona persona : original.getMiembros()) {
            if (persona.getId() != personaIdExcluir) {
                nuevo.agregarMiembro(persona);
            }
        }
        equiposPorNombre.put(normalizar(nombreEquipo), nuevo);
    }
}