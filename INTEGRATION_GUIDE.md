# Guía de Integración: Jenkins, SonarQube, JMeter y Slack

## 1. Pruebas Unitarias

Se han implementado 3 pruebas unitarias en el archivo `src/test/java/pe/edu/vallegrande/vg_ms_claims_incidents/application/services/IncidentServiceTest.java`:

1. `testSaveIncident_Success` - Prueba la creación de un incidente
2. `testFindAllIncidents_Success` - Prueba la obtención de todos los incidentes
3. `testDeleteIncident_Success` - Prueba la eliminación lógica de un incidente

Para ejecutar las pruebas:
```bash
./mvnw test
```

## 2. Configuración de Jenkins

### 2.1. Requisitos previos
- Jenkins instalado
- Maven y JDK configurados en Jenkins
- Plugin de SonarQube instalado en Jenkins

### 2.2. Crear un nuevo pipeline en Jenkins
1. Accede a la interfaz web de Jenkins
2. Crea un nuevo ítem de tipo "Pipeline"
3. En la sección "Pipeline", selecciona "Pipeline script from SCM"
4. Configura el repositorio Git con tu proyecto

### 2.3. Credenciales
- Configura las credenciales de SonarQube como "sonar-token"
- Configura el webhook de Slack

## 3. Integración con SonarQube

### 3.1. Configuración en Jenkins
1. Ve a "Manage Jenkins" > "Configure System"
2. Busca la sección "SonarQube servers"
3. Agrega una nueva configuración de servidor:
   - Name: SonarQube
   - Server URL: http://sonarqube:9000
   - Server authentication token: (usa el token creado en SonarQube)

### 3.2. Ejecutar análisis
El análisis se ejecuta automáticamente en el pipeline de Jenkins en el stage "SonarQube Analysis".

## 4. Pruebas de Carga con JMeter

### 4.1. Plan de pruebas
El archivo `jmeter-test-plan.jmx` contiene un plan de pruebas básico que:
- Realiza 10 iteraciones con 50 hilos concurrentes
- Prueba los endpoints GET y POST de incidentes
- Tiene un tiempo de rampa de 60 segundos

### 4.2. Ejecutar pruebas
```bash
# Usando JMeter en línea de comandos
jmeter -n -t jmeter-test-plan.jmx -l results.jtl

# Generar reporte HTML
jmeter -g results.jtl -o report/
```

## 5. Notificaciones con Slack

### 5.1. Configuración de webhook en Slack
1. En Slack, crea una nueva app
2. Configura un "Incoming Webhook"
3. Copia la URL del webhook

### 5.2. Configuración en Jenkins
1. Instala el plugin "Slack Notification Plugin"
2. Ve a "Manage Jenkins" > "Configure System"
3. Busca la sección "Slack"
4. Configura:
   - Slack compatible app URL: https://hooks.slack.com/services/
   - Integration Token Credential ID: (crea una credencial con el token de Slack)
   - Default channel name: #jenkins-notifications

### 5.3. Script de notificaciones
El archivo `slack-notifications.sh` permite enviar notificaciones personalizadas a Slack:
```bash
./slack-notifications.sh "Mensaje de prueba" "good"
```

## 6. Pruebas funcionales con Selenium

### 6.1. Requisitos
- ChromeDriver instalado y en el PATH
- Navegador Chrome instalado

### 6.2. Ejecutar pruebas
```bash
./mvnw test -Dtest=SeleniumTest
```

## 7. Ejecutar entorno completo con Docker

### 7.1. Levantar todos los servicios
```bash
docker-compose up -d
```

Esto levantará:
- La aplicación en http://localhost:8080
- SonarQube en http://localhost:9000
- Jenkins en http://localhost:8081

### 7.2. Acceso inicial
- **Jenkins**: 
  - Usuario inicial: admin
  - Contraseña: (obtener con `docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword`)
  
- **SonarQube**:
  - Usuario: admin
  - Contraseña: admin

## 8. Monitoreo y métricas

### 8.1. Endpoints de actuator
La aplicación expone endpoints de salud y métricas en:
- `/actuator/health` - Estado de la aplicación
- `/actuator/metrics` - Métricas del sistema
- `/actuator/prometheus` - Métricas en formato Prometheus

### 8.2. Logs
Los logs se almacenan en el directorio `logs/` y se montan como volumen en Docker.