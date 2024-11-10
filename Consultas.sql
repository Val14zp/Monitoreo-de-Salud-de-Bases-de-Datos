select sys_context('USERENV','SERVER_HOST') from dual;
-- CPU total y utilizada por la base de datos
SELECT 
    (SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'CPU used by this session') / 
    (SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'DB time') * 100 AS cpu_utilizado_por_base_datos,
    (SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'NUM_CPUS') AS cpu_total
FROM DUAL;

-- RAM total y utilizada por la base de datos
SELECT
    (SELECT SUM(VALUE) FROM V$SGA) / (1024 * 1024 * 1024) AS ram_utilizada_db_gb,
    (SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'PHYSICAL_MEMORY_BYTES') / (1024 * 1024 * 1024) AS ram_total_gb   
FROM DUAL;

SELECT 
    NVL((SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'SWAP_USED'), 0) /
    NVL((SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'SWAP_TOTAL'), 1) * 100 AS porcentaje_swap_utilizado,
    100 - (NVL((SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'SWAP_USED'), 0) /
           NVL((SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'SWAP_TOTAL'), 1) * 100) AS porcentaje_swap_disponible
FROM DUAL;


-- Porcentaje de espacio utilizado y libre en cada tablespace
SELECT 
    df.tablespace_name,
    ROUND((df.total_space - NVL(fs.free_space, 0)) / df.total_space * 100, 2) AS porcentaje_espacio_utilizado,
    ROUND(NVL(fs.free_space, 0) / df.total_space * 100, 2) AS porcentaje_espacio_libre
FROM 
    (SELECT 
         tablespace_name,
         SUM(bytes) / (1024 * 1024 * 1024) AS total_space
     FROM 
         dba_data_files
     GROUP BY 
         tablespace_name) df
LEFT JOIN 
    (SELECT 
         tablespace_name,
         SUM(bytes) / (1024 * 1024 * 1024) AS free_space
     FROM 
         dba_free_space
     GROUP BY 
         tablespace_name) fs
ON 
    df.tablespace_name = fs.tablespace_name;
    

-- Histórico y tamaño actual de las tablas más grandes
SELECT 
    TABLE_NAME,
    NUM_ROWS AS filas,
    BLOCKS * 8192 / (1024 * 1024 * 1024) AS tamano_gb
FROM DBA_TABLES
WHERE NUM_ROWS IS NOT NULL
AND BLOCKS IS NOT NULL
ORDER BY tamano_gb DESC
FETCH FIRST 10 ROWS ONLY;


-- Uso de redo logs
SELECT 
    GROUP# AS grupo,
    SEQUENCE# AS secuencia,
    ARCHIVED AS archivado,
    STATUS AS estado
FROM V$LOG;

-- Número de conexiones activas y sesiones concurrentes
SELECT 
    COUNT(*) AS conexiones_activas,
    COUNT(DISTINCT SID) AS sesiones_concurrentes
FROM V$SESSION
WHERE STATUS = 'ACTIVE';

-- Latencia promedio de consultas más frecuentes
SELECT 
    SQL_ID,
    COUNT(*) AS frecuencia,
    AVG(ELAPSED_TIME / 1000000) AS latencia_promedio_seg
FROM V$SQL
WHERE ELAPSED_TIME IS NOT NULL
GROUP BY SQL_ID
ORDER BY frecuencia DESC
FETCH FIRST 10 ROWS ONLY;

-- Tasa de lectura/escritura en disco
SELECT 
    INST_ID,
    SUM(CASE WHEN NAME = 'physical read total bytes' THEN VALUE ELSE 0 END) / 1024 / 1024 AS lectura_mb,
    SUM(CASE WHEN NAME = 'physical write total bytes' THEN VALUE ELSE 0 END) / 1024 / 1024 AS escritura_mb
FROM 
    GV$SYSSTAT
GROUP BY 
    INST_ID;
    
-- Última fecha de backup realizado y espacio ocupado
SELECT 
    MAX(bp.COMPLETION_TIME) AS ultima_fecha_backup,
    SUM(bp.BYTES) / (1024 * 1024 * 1024) AS tamano_total_backup_gb
FROM 
    V$BACKUP_PIECE bp;
    
     
-- Registro de alertas o eventos críticos
SELECT 
    TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS') AS fecha,
    MESSAGE_TEXT AS descripcion_error,
    COUNT(*) AS conteo
FROM 
    V$DIAG_ALERT_EXT
WHERE 
    TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD') BETWEEN TO_CHAR(SYSDATE - 1, 'YYYY-MM-DD') AND TO_CHAR(SYSDATE, 'YYYY-MM-DD')
    AND (MESSAGE_TEXT LIKE '%ORA-%' 
         OR MESSAGE_TEXT LIKE '%Errors in file%' 
         OR MESSAGE_TEXT LIKE '%highscn criteria not met for file%'
         OR MESSAGE_TEXT LIKE '%Warning%')
       
GROUP BY 
    TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS'),
    MESSAGE_TEXT
ORDER BY 
    fecha DESC;


-- Consultas SQL que más consumen recursos
SELECT 
    SQL_ID,
    EXECUTIONS,
    ROUND(ELAPSED_TIME / 1000000, 2) AS tiempo_total_segundos,
    ROUND(CPU_TIME / 1000000, 2) AS cpu_total_segundos,
    DISK_READS,
    BUFFER_GETS
FROM V$SQL
ORDER BY ELAPSED_TIME DESC
FETCH FIRST 10 ROWS ONLY;




























