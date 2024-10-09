library(RJDBC)
library(DBI)
library(config) # Ensure you have the config library loaded

# Load JDBC driver
driver <- JDBC(driverClass = "oracle.jdbc.OracleDriver", "C:/drivers/oracle/ojdbc11.jar")

# SQL query to fetch data from the ASEGURADO table
consulta_asegurado <- "SELECT * FROM ASEGURADO;"

# Define a function to establish a database connection
database_get_connect <- function() {
  # Load configuration from config.yml
  xedb <- config::get(file = "config.yml") # Ensure correct path to config file
  
  # Ensure that xedb is a list and has the required fields
  if (is.list(xedb) && all(c("host", "port", "sid", "user", "pass") %in% names(xedb))) {
    dbCon <- dbConnect(driver,
                       paste0("jdbc:oracle:thin:@", xedb$host, ":", xedb$port, ":", xedb$sid), # Connection string
                       xedb$user, # Username
                       xedb$pass) # Password
    return(dbCon) # Return the database connection
  } else {
    stop("Invalid configuration format. Ensure the config file is properly structured.")
  }
}

# Establish connection to the database
dbCon <- database_get_connect()

# Run the SQL query
sql.query <- dbGetQuery(dbCon, consulta_asegurado)

# Print the query results
print(sql.query)

# Close the connection
dbDisconnect(dbCon)
