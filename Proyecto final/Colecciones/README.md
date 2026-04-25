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

