public class Lider extends Persona {

    private String especialidad;

    public Lider(int id, String nombre, int edad, String rolPrincipal, String especialidad) {
        super(id, nombre, edad, rolPrincipal);
        setEspecialidad(especialidad);
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad no puede estar vacia.");
        }
        this.especialidad = especialidad.trim();
    }

    @Override
    public void notificar(String mensaje) {
        System.out.println("[ALERTA LIDER] " + getNombre() + ": " + mensaje);
    }

    @Override
    public String getTipo() {
        return "LIDER";
    }

    @Override
    public String toString() {
        return super.toString().replace("}", ", especialidad='" + especialidad + "'}");
    }
}