CREATE DATABASE company_db;
USE company_db;

-- Create the REGIONS table
CREATE TABLE regions (
    region_id INT NOT NULL,
    region_name VARCHAR(25),
    PRIMARY KEY (region_id)
);

-- Create the COUNTRIES table
CREATE TABLE countries (
    country_id CHAR(2) NOT NULL,
    country_name VARCHAR(40),
    region_id INT,
    PRIMARY KEY (country_id),
    FOREIGN KEY (region_id) REFERENCES regions(region_id)
);

-- Create the LOCATIONS table
CREATE TABLE locations (
    location_id INT NOT NULL,
    street_address VARCHAR(40),
    postal_code VARCHAR(12),
    city VARCHAR(30) NOT NULL,
    state_province VARCHAR(25),
    country_id CHAR(2),
    PRIMARY KEY (location_id),
    FOREIGN KEY (country_id) REFERENCES countries(country_id)
);

-- Create the SEQUENCE for LOCATIONS (only works in certain SQL systems like PostgreSQL)
CREATE SEQUENCE locations_seq START WITH 3300 INCREMENT BY 100;

-- Create the DEPARTMENTS table
CREATE TABLE departments (
    department_id INT NOT NULL,
    department_name VARCHAR(30) NOT NULL,
    manager_id INT,
    location_id INT,
    PRIMARY KEY (department_id),
    FOREIGN KEY (location_id) REFERENCES locations(location_id)
);

-- Create the SEQUENCE for DEPARTMENTS
CREATE SEQUENCE departments_seq START WITH 280 INCREMENT BY 10;

-- Create the JOBS table
CREATE TABLE jobs (
    job_id VARCHAR(10) NOT NULL,
    job_title VARCHAR(35) NOT NULL,
    min_salary DECIMAL(6,2),
    max_salary DECIMAL(6,2),
    PRIMARY KEY (job_id)
);

-- Create the EMPLOYEES table
CREATE TABLE employees (
    employee_id INT NOT NULL,
    first_name VARCHAR(20),
    last_name VARCHAR(25) NOT NULL,
    email VARCHAR(25) NOT NULL,
    phone_number VARCHAR(20),
    hire_date DATE NOT NULL,
    job_id VARCHAR(10) NOT NULL,
    salary DECIMAL(8,2),
    commission_pct DECIMAL(2,2),
    manager_id INT,
    department_id INT,
    PRIMARY KEY (employee_id),
    UNIQUE (email),
    CHECK (salary > 0),
    FOREIGN KEY (department_id) REFERENCES departments(department_id),
    FOREIGN KEY (job_id) REFERENCES jobs(job_id),
    FOREIGN KEY (manager_id) REFERENCES employees(employee_id)
);

-- Create the SEQUENCE for EMPLOYEES
CREATE SEQUENCE employees_seq START WITH 207 INCREMENT BY 1;

-- Create the JOB_HISTORY table
CREATE TABLE job_history (
    employee_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    job_id VARCHAR(10) NOT NULL,
    department_id INT,
    PRIMARY KEY (employee_id, start_date),
    CHECK (end_date > start_date),
    FOREIGN KEY (job_id) REFERENCES jobs(job_id),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);


-- Insert data into the REGIONS table
INSERT INTO regions (region_id, region_name) VALUES 
   (1, 'Europe');

INSERT INTO regions (region_id, region_name) VALUES 
   (2, 'Americas');

INSERT INTO regions (region_id, region_name) VALUES 
   (3, 'Asia');

INSERT INTO regions (region_id, region_name) VALUES 
   (4, 'Middle East and Africa');

-- Insert data into the COUNTRIES table
INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('IT', 'Italy', 1);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('JP', 'Japan', 3);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('US', 'United States of America', 2);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('CA', 'Canada', 2);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('CN', 'China', 3);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('IN', 'India', 3);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('AU', 'Australia', 3);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('ZW', 'Zimbabwe', 4);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('SG', 'Singapore', 3);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('UK', 'United Kingdom', 1);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('FR', 'France', 1);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('DE', 'Germany', 1);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('ZM', 'Zambia', 4);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('EG', 'Egypt', 4);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('BR', 'Brazil', 2);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('CH', 'Switzerland', 1);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('NL', 'Netherlands', 1);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('MX', 'Mexico', 2);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('KW', 'Kuwait', 4);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('IL', 'Israel', 4);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('DK', 'Denmark', 1);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('HK', 'HongKong', 3);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('NG', 'Nigeria', 4);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('AR', 'Argentina', 2);

INSERT INTO countries (country_id, country_name, region_id) VALUES 
   ('BE', 'Belgium', 1);

   -- Insert data into the LOCATIONS table
INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (1000, '1297 Via Cola di Rie', '00989', 'Roma', NULL, 'IT');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (1100, '93091 Calle della Testa', '10934', 'Venice', NULL, 'IT');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (1200, '2017 Shinjuku-ku', '1689', 'Tokyo', 'Tokyo Prefecture', 'JP');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (1300, '9450 Kamiya-cho', '6823', 'Hiroshima', NULL, 'JP');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (1400, '2014 Jabberwocky Rd', '26192', 'Southlake', 'Texas', 'US');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (1500, '2011 Interiors Blvd', '99236', 'South San Francisco', 'California', 'US');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (1600, '2007 Zagora St', '50090', 'South Brunswick', 'New Jersey', 'US');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (1700, '2004 Charade Rd', '98199', 'Seattle', 'Washington', 'US');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (1800, '147 Spadina Ave', 'M5V 2L7', 'Toronto', 'Ontario', 'CA');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (1900, '6092 Boxwood St', 'YSW 9T2', 'Whitehorse', 'Yukon', 'CA');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (2000, '40-5-12 Laogianggen', '190518', 'Beijing', NULL, 'CN');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (2100, '1298 Vileparle (E)', '490231', 'Bombay', 'Maharashtra', 'IN');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (2200, '12-98 Victoria Street', '2901', 'Sydney', 'New South Wales', 'AU');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (2300, '198 Clementi North', '540198', 'Singapore', NULL, 'SG');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (2400, '8204 Arthur St', NULL, 'London', NULL, 'UK');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (2500, 'Magdalen Centre, The Oxford Science Park', 'OX9 9ZB', 'Oxford', 'Oxford', 'UK');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (2600, '9702 Chester Road', '09629850293', 'Stretford', 'Manchester', 'UK');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (2700, 'Schwanthalerstr. 7031', '80925', 'Munich', 'Bavaria', 'DE');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (2800, 'Rua Frei Caneca 1360', '01307-002', 'Sao Paulo', 'Sao Paulo', 'BR');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (2900, '20 Rue des Corps-Saints', '1730', 'Geneva', 'Geneve', 'CH');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (3000, 'Murtenstrasse 921', '3095', 'Bern', 'BE', 'CH');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (3100, 'Pieter Breughelstraat 837', '3029SK', 'Utrecht', 'Utrecht', 'NL');

INSERT INTO locations (location_id, street_address, postal_code, city, state_province, country_id) VALUES 
   (3200, 'Mariano Escobedo 9991', '11932', 'Mexico City', 'Distrito Federal', 'MX');


   -- Insert data into the DEPARTMENTS table
INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (10, 'Administration', 200, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (20, 'Marketing', 201, 1800);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (30, 'Purchasing', 114, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (40, 'Human Resources', 203, 2400);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (50, 'Shipping', 121, 1500);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (60, 'IT', 103, 1400);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (70, 'Public Relations', 204, 2700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (80, 'Sales', 145, 2500);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (90, 'Executive', 100, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (100, 'Finance', 108, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (110, 'Accounting', 205, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (120, 'Treasury', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (130, 'Corporate Tax', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (140, 'Control And Credit', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (150, 'Shareholder Services', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (160, 'Benefits', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (170, 'Manufacturing', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (180, 'Construction', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (190, 'Contracting', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (200, 'Operations', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (210, 'IT Support', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (220, 'NOC', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (230, 'IT Helpdesk', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (240, 'Government Sales', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (250, 'Retail Sales', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (260, 'Recruiting', NULL, 1700);

INSERT INTO departments (department_id, department_name, manager_id, location_id) VALUES 
   (270, 'Payroll', NULL, 1700);

-- Insert data into the JOBS table
INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('AD_PRES', 'President', 20000, 40000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('AD_VP', 'Administration Vice President', 15000, 30000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('AD_ASST', 'Administration Assistant', 3000, 6000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('FI_MGR', 'Finance Manager', 8200, 16000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('FI_ACCOUNT', 'Accountant', 4200, 9000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('AC_MGR', 'Accounting Manager', 8200, 16000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('AC_ACCOUNT', 'Public Accountant', 4200, 9000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('SA_MAN', 'Sales Manager', 10000, 20000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('SA_REP', 'Sales Representative', 6000, 12000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('PU_MAN', 'Purchasing Manager', 8000, 15000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('PU_CLERK', 'Purchasing Clerk', 2500, 5500);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('ST_MAN', 'Stock Manager', 5500, 8500);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('ST_CLERK', 'Stock Clerk', 2000, 5000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('SH_CLERK', 'Shipping Clerk', 2500, 5500);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('IT_PROG', 'Programmer', 4000, 10000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('MK_MAN', 'Marketing Manager', 9000, 15000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('MK_REP', 'Marketing Representative', 4000, 9000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('HR_REP', 'Human Resources Representative', 4000, 9000);

INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES 
   ('PR_REP', 'Public Relations Representative', 4500, 10500);