public class Miembro extends Persona {

    public Miembro(int id, String nombre, int edad, String rolPrincipal) {
        super(id, nombre, edad, rolPrincipal);
    }

    @Override
    public String getTipo() {
        return "MIEMBRO";
    }
}