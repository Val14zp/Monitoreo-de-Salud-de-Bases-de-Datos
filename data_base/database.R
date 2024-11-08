library(shiny)
library(shinydashboard)
library(DBI)
library(RJDBC)

# Cargar el controlador JDBC
driver <- JDBC(driverClass = "oracle.jdbc.OracleDriver", "C:/drivers/oracle/ojdbc11.jar")

# Función para conectar a la base de datos
database_get_connect <- function() {
  dbConnect(driver,
            "jdbc:oracle:thin:@localhost:1521:xe",
            "sys as sysdba",  # Usuario
            "root")           # Contraseña
}
library(shiny)
runApp("shiny_app")
