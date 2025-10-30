# Configuración de SonarQube Cloud para vg-ms-claims-incidents

Esta guía explica cómo configurar SonarQube Cloud para el proyecto `vg-ms-claims-incidents`.

## Paso 1: Crear cuenta en SonarQube Cloud

1. Visita [https://sonarcloud.io](https://sonarcloud.io)
2. Regístrate o inicia sesión con tu cuenta de GitHub, Bitbucket o Azure DevOps
3. Crea una nueva organización o únete a una existente

## Paso 2: Crear un nuevo proyecto

1. En el dashboard de SonarQube Cloud, haz clic en "Create Project"
2. Selecciona la opción "Analyze new project"
3. Elige el repositorio correspondiente o ingresa manualmente la información:
   - Project Key: `vg-ms-claims-incidents`
   - Project Name: `VG MS Claims Incidents`
   - Organization: Tu organización en SonarQube Cloud

## Paso 3: Obtener el token de análisis

1. Una vez creado el proyecto, ve a "Administration" > "Analysis Method"
2. Genera un nuevo token en "Tokens"
3. Guarda este token de forma segura, lo necesitarás para la configuración de Jenkins

## Paso 4: Configurar Jenkins

1. En Jenkins, ve a "Manage Jenkins" > "Manage Credentials"
2. Agrega una nueva credencial del tipo "Secret text"
3. Pega el token de SonarQube Cloud en el campo "Secret"
4. Asigna el ID `sonar-token` a la credencial

## Paso 5: Actualizar configuraciones

### Archivo sonar-project.properties

Asegúrate de que el archivo contiene las siguientes propiedades:

```properties
# Clave única del proyecto en SonarQube Cloud
sonar.projectKey=vg-ms-claims-incidents

# Organización en SonarQube Cloud (reemplazar con tu organización real)
sonar.organization=tu-organizacion

# Nombre y versión del proyecto
sonar.projectName=VG MS Claims Incidents
sonar.projectVersion=1.0

# Rutas a fuentes y tests
sonar.sources=src/main/java
sonar.tests=src/test/java

# Rutas a clases compiladas
sonar.java.binaries=target/classes
sonar.java.test.binaries=target/test-classes

# Codificación
sonar.sourceEncoding=UTF-8

# Reporte de cobertura JaCoCo
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

# Exclusiones
sonar.exclusions=**/target/**,**/pom.xml
```

### Archivo pom.xml

El plugin JaCoCo ya está configurado para generar reportes de cobertura:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Jenkinsfile

El pipeline de Jenkins ya está configurado para ejecutar el análisis de SonarQube Cloud:

```groovy
stage('SonarQube Cloud Analysis') {
    steps {
        sh """
            mvn sonar:sonar \
            -Dsonar.projectKey=vg-ms-claims-incidents \
            -Dsonar.organization=${SONAR_ORGANIZATION} \
            -Dsonar.host.url=https://sonarcloud.io \
            -Dsonar.login=${SONAR_TOKEN}
        """
    }
}
```

## Paso 6: Ejecutar el pipeline

1. Ejecuta el pipeline de Jenkins
2. Verifica que el análisis se complete correctamente
3. Revisa los resultados en el dashboard de SonarQube Cloud

## Solución de problemas comunes

1. **Token inválido**: Asegúrate de que el token esté correctamente configurado en Jenkins
2. **Organización incorrecta**: Verifica que la organización coincida con la de tu cuenta de SonarQube Cloud
3. **Problemas de cobertura**: Confirma que las pruebas unitarias se ejecuten antes del análisis de SonarQube
4. **Problemas de permisos**: Verifica que el token tenga los permisos adecuados en SonarQube Cloud

## Mejores prácticas

1. Mantén tus pruebas unitarias actualizadas para obtener métricas precisas
2. Revisa regularmente las recomendaciones de SonarQube Cloud
3. Establece Quality Gates apropiados para tu proyecto
4. Monitorea la evolución de la calidad del código a lo largo del tiempo