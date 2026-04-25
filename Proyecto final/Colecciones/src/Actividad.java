import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Actividad implements Comparable<Actividad> {

    private static int totalActividadesCreadas = 0;

    private final int id;
    private String nombre;
    private LocalDate fecha;
    private int cupo;
    private final Set<Integer> asistentesIds;

    public Actividad(int id, String nombre, LocalDate fecha, int cupo) {
        this.id = id;
        this.asistentesIds = new HashSet<>();
        setNombre(nombre);
        setFecha(fecha);
        setCupo(cupo);
        totalActividadesCreadas++;
    }

    public static int getTotalActividadesCreadas() {
        return totalActividadesCreadas;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de actividad no puede estar vacio.");
        }
        this.nombre = nombre.trim();
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }
        this.fecha = fecha;
    }

    public int getCupo() {
        return cupo;
    }

    public void setCupo(int cupo) {
        if (cupo <= 0) {
            throw new IllegalArgumentException("El cupo debe ser mayor a 0.");
        }
        if (cupo < asistentesIdsSize()) {
            throw new IllegalArgumentException("El cupo no puede ser menor que los asistentes actuales.");
        }
        this.cupo = cupo;
    }

    public Set<Integer> getAsistentesIds() {
        return Collections.unmodifiableSet(asistentesIds);
    }

    public int asistentesIdsSize() {
        return asistentesIds.size();
    }

    public boolean registrarAsistencia(int personaId) throws CupoLlenoException {
        if (asistentesIds.contains(personaId)) {
            return false;
        }
        if (asistentesIds.size() >= cupo) {
            throw new CupoLlenoException("La actividad " + nombre + " ya alcanzo su cupo maximo.");
        }
        asistentesIds.add(personaId);
        return true;
    }

    public void retirarAsistencia(int personaId) {
        asistentesIds.remove(personaId);
    }

    public double getPorcentajeOcupacion() {
        if (cupo == 0) {
            return 0;
        }
        return (asistentesIds.size() * 100.0) / cupo;
    }

    @Override
    public int compareTo(Actividad otra) {
        int porFecha = this.fecha.compareTo(otra.fecha);
        if (porFecha != 0) {
            return porFecha;
        }
        int porNombre = this.nombre.compareToIgnoreCase(otra.nombre);
        if (porNombre != 0) {
            return porNombre;
        }
        return Integer.compare(this.id, otra.id);
    }

    @Override
    public String toString() {
        return String.format(
                "Actividad{id=%d, nombre='%s', fecha=%s, cupo=%d, ocupados=%d, ocupacion=%.2f%%}",
                id,
                nombre,
                fecha,
                cupo,
                asistentesIds.size(),
                getPorcentajeOcupacion());
    }
}