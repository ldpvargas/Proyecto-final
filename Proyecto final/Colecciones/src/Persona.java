public abstract class Persona implements Notificable {

    private static int totalPersonasCreadas = 0;

    private final int id;
    private String nombre;
    private int edad;
    private String rolPrincipal;
    private int asistenciasRegistradas;

    protected Persona(int id, String nombre, int edad, String rolPrincipal) {
        this.id = id;
        setNombre(nombre);
        setEdad(edad);
        setRolPrincipal(rolPrincipal);
        totalPersonasCreadas++;
    }

    public abstract String getTipo();

    public static int getTotalPersonasCreadas() {
        return totalPersonasCreadas;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        this.nombre = nombre.trim();
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad <= 0) {
            throw new IllegalArgumentException("La edad debe ser mayor a 0.");
        }
        this.edad = edad;
    }

    public String getRolPrincipal() {
        return rolPrincipal;
    }

    public void setRolPrincipal(String rolPrincipal) {
        if (rolPrincipal == null || rolPrincipal.trim().isEmpty()) {
            throw new IllegalArgumentException("El rol principal no puede estar vacio.");
        }
        this.rolPrincipal = rolPrincipal.trim();
    }

    public int getAsistenciasRegistradas() {
        return asistenciasRegistradas;
    }

    public void incrementarAsistencias() {
        asistenciasRegistradas++;
    }

    protected void setAsistenciasRegistradas(int asistenciasRegistradas) {
        if (asistenciasRegistradas < 0) {
            throw new IllegalArgumentException("Las asistencias no pueden ser negativas.");
        }
        this.asistenciasRegistradas = asistenciasRegistradas;
    }

    @Override
    public void notificar(String mensaje) {
        System.out.println("Notificacion para " + nombre + ": " + mensaje);
    }

    @Override
    public String toString() {
        return String.format(
                "%s{id=%d, nombre='%s', edad=%d, rol='%s', asistencias=%d}",
                getTipo(),
                id,
                nombre,
                edad,
                rolPrincipal,
                asistenciasRegistradas);
    }
}