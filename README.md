# Proyecto-final
Proyecto final. Comunidad
## Diagrama (ASCII)

```text
			 +-----------------------+
			 |      Notificable      |
			 |-----------------------|
			 | + notificar(mensaje)  |
			 +-----------^-----------+
						 |
			             |
						 |
				 ------------------
				 |   Persona      |
				 |----------------|
				 | id:int         |
				 | nombre:String  |
				 | edad:int       |
				 | rolPrincipal   |
				 ------------------
					 |        |
			   -----------  ----------
			   | Miembro |  |  Lider  |
			   -----------  ------------

 +--------------------+      +------------------------+
 |      Actividad     |      |         Equipo         |
 |--------------------|      |------------------------|
 | id, nombre, fecha  |      | nombre                 |
 | cupo, asistentes   |      | lista de Persona       |
 +--------------------+      +------------------------+

# DECISIONES DE NEGOCIO - Gestor de Comunidad

1. Cada persona tiene un `id` unico y un `rolPrincipal`.
2. No se permite crear ni editar personas con nombre vacio o edad menor o igual a 0.
3. No se permite crear ni editar actividades con nombre vacio, fecha nula o cupo <= 0.
4. Una actividad no permite asistencia duplicada de la misma persona.
5. Si una actividad ya lleno su cupo, da error.
6. Si no existe una persona,actividad o equipo, da error
7. Para promover a una persona de `Miembro` a `Lider` debe cumplir:

- Edad minima: 21 anios.
- Asistencias minimas registradas: 3.

Si no cumple, se bloquea la promocion

## Colecciones:

1. `HashMap<Integer, Persona>` como indice principal por id para busqueda O(1) promedio.
2. `ArrayList<Actividad>` para CRUD de actividades y recorrido simple.
3. `HashSet<String>` para roles registrados sin duplicados.
4. `TreeMap<String, Persona>` para listado de personas ordenadas por nombre.
5. `TreeSet<Actividad>` para listado de actividades ordenadas por fecha.
6. `HashMap<Integer, Integer>` adicional para conteo rapido de asistencias por persona.

## Validaciones

1. Se usa menu numerico.
2. Todas las entradas de usuario tienen validaciones de formato (numero/fecha).
3. Se precargan datos de ejemplo para facilitar demostracion de reportes.
