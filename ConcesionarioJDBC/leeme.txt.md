##### **Utilizar JDBC**



Ventajas: 



- Estandarización, la misma API te permite conectarte a varias bases de datos como SQLite y MySQL

- Tiene un fácil control de uso con Connection, Statement, PreparedStatements y ResultSet



Desventajas:



- Tienes que mapear los objetos a mano

- Control constante de SQLExceptions

- Mucho código repetido para recorrer ResultSet y cerrar recursos







### **Base de datos embebidas (SQLite)**



Ventajas:



- Configuración nula, no requiere instalar nada ni configurar

- Fácil portabilidad, es un fichero .db/.SQLite	

- Bajo consumo



Desventajas:



- No está pensada para varios usuarios 

- Carece de seguridad

### **Base de datos de servidor (Mysql)**

Ventajas:

- Diseñada para soportar muchas conexiones

- Seguridad y control
- Soporta grandes cantidades de datos

Desventajas:

- Mayor complejidad de despliegue
- Mayor consumo de recursos

