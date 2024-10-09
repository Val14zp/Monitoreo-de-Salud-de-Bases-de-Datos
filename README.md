
# Aplicación de Monitoreo en Tiempo Real de la Salud de Bases de Datos

## Objetivo General:
Desarrollar una pequeña aplicación que permita monitorear en tiempo real la salud de una base de datos Oracle o SQL Server, presentando información relevante sobre el rendimiento y uso de recursos a través de gráficos y otros elementos visuales.

## Estructura del Proyecto:

1. **Base de Datos**: Gestionar la interacción con la base de datos SQL Server.
2. **Lógica**: Procesar los datos y métricas recogidas de la base de datos.
3. **Servidor**: Backend encargado de manejar las solicitudes y servir los datos.
4. **Interfaz de Usuario (UI)**: Frontend para presentar los datos mediante visualizaciones.

## Objetivos Específicos:

1. Implementar un sistema que recoja y visualice datos sobre el uso de recursos del sistema operativo (RAM, CPU, SWAP) y métricas específicas de la base de datos (espacio en archivos de datos, crecimiento de datos, consultas que más consumen recursos, etc.).
2. Ofrecer una visión clara y accesible del estado actual de la base de datos, permitiendo identificar posibles problemas de rendimiento y capacidad.
3. Utilizar técnicas de programación y herramientas gráficas para desarrollar una interfaz de usuario sencilla y útil para el monitoreo continuo.

## Equipo y Responsabilidades

### Recolección de Datos

| Tema                          | Persona a Cargo  |
| ------------------------------ | ---------------- |
| Uso del CPU                    | Christopher      |
| Uso de RAM                     | Christopher      |
| Uso de SWAP                    | Diana            |
| Espacio en Disco               | Diana            |
| Conexiones Activas             | Diana            |
| Tiempo de Respuesta a Consultas| Kenneth          |
| Validez de Input/Output        | Valery           |
| Estado de los Backups          | Valery           |
| Alertas y Eventos Críticos     | Valery           |
| Consultas que más Consumen Recursos | Kenneth     |

### Visualización de Datos

| Tema                          | Persona a Cargo  |
| ------------------------------ | ---------------- |
| Gráficos en tiempo real        | Fabricio         |
| Paneles de control             | Fabricio         |
| Alertas visuales               | Christopher, Diana |
| Historial de métricas          | Valery           |
| Consultas pesadas              | Kenneth          |

### Pruebas y Documentación

| Tema                          | Persona a Cargo  |
| ------------------------------ | ---------------- |
| Pruebas de Carga               | Fabricio         |
| Documentación                  | Todos            |

## Tecnologías Utilizadas:

- **Lenguaje de Programación**: R
- **Base de Datos**: SQL Server

## Configuración e Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Val14zp/AdminBases.git
   ```
2. Configurar la conexión a la base de datos (SQL Server).
3. Instalar los paquetes requeridos en R:
   ```R
   install.packages("DBI")
   install.packages("ggplot2")
   install.packages("shiny")
   ```
4. Ejecutar la aplicación:
   ```R
   shiny::runApp('ruta_aplicación')
   ```

## Funcionamiento:

- **Recolección de Datos**: Se recogen métricas de rendimiento en tiempo real como el uso de CPU, RAM, SWAP, espacio en disco, y datos específicos de la base de datos.
- **Visualización de Datos**: Se presentan gráficos en tiempo real, paneles de control, alertas visuales para eventos críticos y un historial de métricas.
- **Monitoreo**: El sistema permite el monitoreo continuo de la salud de la base de datos, facilitando la identificación temprana de problemas de rendimiento.

## Contribuciones:

Todos los miembros del equipo deberán contribuir tanto en las pruebas como en la documentación del proyecto.

---
