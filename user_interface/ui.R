library(shiny)
library(shinydashboard)

dashboardPage(
  dashboardHeader(title = "Oracle DB Dashboard"),
  dashboardSidebar(
    sidebarMenu(
      menuItem("CPU & RAM", tabName = "cpu_ram", icon = icon("microchip")),
      menuItem("Tablespaces", tabName = "tablespaces", icon = icon("database")),
      menuItem("Queries & Logs", tabName = "queries_logs", icon = icon("file-alt")),
      menuItem("Alerts", tabName = "alerts", icon = icon("exclamation-triangle"))
    )
  ),
  dashboardBody(
    tabItems(
      tabItem(tabName = "cpu_ram",
              fluidRow(
                box(title = "CPU Usage", plotOutput("cpu_plot"), width = 6),
                box(title = "RAM Usage", plotOutput("ram_plot"), width = 6)
              )),
      tabItem(tabName = "tablespaces",
              fluidRow(
                box(title = "Tablespace Usage", tableOutput("tablespace_table"), width = 12)
              )),
      tabItem(tabName = "queries_logs",
              fluidRow(
                box(title = "Top Queries", tableOutput("queries_table"), width = 6),
                box(title = "Logs", tableOutput("logs_table"), width = 6)
              )),
      tabItem(tabName = "alerts",
              fluidRow(
                box(title = "Critical Alerts", tableOutput("alerts_table"), width = 12)
              ))
    )
  )
)
