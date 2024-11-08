library(shiny)
library(DBI)

shinyServer(function(input, output, session) {
  # Conexión a la base de datos
  dbCon <- database_get_connect()
  
  # CPU & RAM Data
  output$cpu_plot <- renderPlot({
    query <- "
      SELECT 
          (SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'CPU used by this session') / 
          (SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'DB time') * 100 AS cpu_utilizado,
          (SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'NUM_CPUS') AS cpu_total
      FROM DUAL"
    data <- dbGetQuery(dbCon, query)
    barplot(data$cpu_utilizado, names.arg = "CPU Utilization", col = "blue", main = "CPU Usage (%)")
  })
  
  output$ram_plot <- renderPlot({
    query <- "
      SELECT 
          (SELECT SUM(VALUE) FROM V$SGA) / (1024 * 1024 * 1024) AS ram_utilizada_db,
          (SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'PHYSICAL_MEMORY_BYTES') / (1024 * 1024 * 1024) AS ram_total
      FROM DUAL"
    data <- dbGetQuery(dbCon, query)
    barplot(data$ram_utilizada_db, names.arg = "RAM Usage", col = "green", main = "RAM Usage (GB)")
  })
  
  # Tablespace Usage
  output$tablespace_table <- renderTable({
    query <- "
      SELECT 
          df.tablespace_name,
          ROUND((df.total_space - NVL(fs.free_space, 0)) / df.total_space * 100, 2) AS porcentaje_utilizado,
          ROUND(NVL(fs.free_space, 0) / df.total_space * 100, 2) AS porcentaje_libre
      FROM 
          (SELECT tablespace_name, SUM(bytes) / (1024 * 1024 * 1024) AS total_space
           FROM dba_data_files GROUP BY tablespace_name) df
      LEFT JOIN 
          (SELECT tablespace_name, SUM(bytes) / (1024 * 1024 * 1024) AS free_space
           FROM dba_free_space GROUP BY tablespace_name) fs
      ON df.tablespace_name = fs.tablespace_name"
    dbGetQuery(dbCon, query)
  })
  
  # Top Queries
  output$queries_table <- renderTable({
    query <- "
      SELECT 
          SQL_ID,
          EXECUTIONS,
          ROUND(ELAPSED_TIME / 1000000, 2) AS tiempo_total,
          ROUND(CPU_TIME / 1000000, 2) AS cpu_total,
          DISK_READS,
          BUFFER_GETS
      FROM V$SQL
      ORDER BY ELAPSED_TIME DESC
      FETCH FIRST 10 ROWS ONLY"
    dbGetQuery(dbCon, query)
  })
  
  # Critical Alerts
  output$alerts_table <- renderTable({
    query <- "
      SELECT 
          TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS') AS fecha,
          MESSAGE_TEXT AS descripcion,
          COUNT(*) AS conteo
      FROM V$DIAG_ALERT_EXT
      WHERE 
          TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD') = TO_CHAR(SYSDATE, 'YYYY-MM-DD')
      GROUP BY TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS'), MESSAGE_TEXT
      ORDER BY fecha DESC"
    dbGetQuery(dbCon, query)
  })
  
  # Cerrar conexión cuando se detiene la aplicación
  session$onSessionEnded(function() {
    dbDisconnect(dbCon)
  })
})
