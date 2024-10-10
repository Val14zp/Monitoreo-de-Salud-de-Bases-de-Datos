library(RJDBC)
library(DBI)


# Load JDBC driver
driver <- JDBC(driverClass = "oracle.jdbc.OracleDriver", "C:/drivers/oracle/ojdbc11.jar")

# SQL query to fetch data from the ASEGURADO table
consulta_lectura_disco <- "SELECT 
    name, 
    value 
FROM 
    v$sysstat 
WHERE 
    name IN ('physical reads', 'physical writes')
"

database_get_connect <- function() {
    dbCon <- dbConnect(driver,
                       "jdbc:oracle:thin:@localhost:1521:xe",
                       "sys as sysdba", # Username con rol
                       "root") # Password
    return(dbCon) # Return the database connection
}

# Establish connection to the database
dbCon <- database_get_connect()

# Run the SQL query
sql.query <- dbGetQuery(dbCon, consulta_asegurado)

# Print the query results
print(sql.query)

# Close the connection
dbDisconnect(dbCon)
