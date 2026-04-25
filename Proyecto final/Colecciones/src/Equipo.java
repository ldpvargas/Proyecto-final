import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Equipo {

    private final String nombre;
    private final List<Persona> miembros;

    public Equipo(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo no puede estar vacio.");
        }
        this.nombre = nombre.trim();
        this.miembros = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Persona> getMiembros() {
        return Collections.unmodifiableList(miembros);
    }

    public boolean agregarMiembro(Persona persona) {
        for (Persona existente : miembros) {
            if (existente.getId() == persona.getId()) {
                return false;
            }
        }
        return miembros.add(persona);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Equipo{nombre='" + nombre + "', miembros=[");
        for (int i = 0; i < miembros.size(); i++) {
            Persona p = miembros.get(i);
            sb.append(p.getNombre()).append("#").append(p.getId());
            if (i < miembros.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}