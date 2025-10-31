pipeline {
    agent any
    
    tools {
        maven 'M3'
        jdk 'JDK17'
    }
    
    environment {
        JAVA_OPTS = '-Xmx1024m'
        // SonarQube Cloud token - configure this in Jenkins credentials
        SONAR_TOKEN = credentials('sonar-token')
        // Organization key for SonarQube Cloud
        SONAR_ORGANIZATION = 'santiagoprada'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/SantiagoPrada3/ms-incidents-pruebas.git', credentialsId: 'github-token'
            }
        }
        
        stage('Build') {
            steps {
                bat 'mvn clean compile -DskipTests'
            }
        }
        
        stage('Run Unit Tests with Coverage') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('SonarQube Cloud Analysis') {
            steps {
                // For SonarQube Cloud, we don't need to specify the host URL
                // The token and organization are passed as properties
                bat """
                    mvn sonar:sonar ^
                    -Dsonar.projectKey=SantiagoPrada3_ms-incidents-pruebas ^
                    -Dsonar.organization=${SONAR_ORGANIZATION} ^
                    -Dsonar.host.url=https://sonarcloud.io ^
                    -Dsonar.login=${SONAR_TOKEN}
                """
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline ejecutado correctamente'
            slackSend(
                channel: '#notificaciones-dev',
                color: '#36a64f',
                message: "✅ Build exitoso en ${env.JOB_NAME}",
                teamDomain: 'jenkins-vz85724',
                tokenCredentialId: 'slack-token'
            )
        }
        failure {
            echo 'Pipeline fallido'
            slackSend(
                channel: '#notificaciones-dev',
                color: '#ff0000',
                message: "❌ Build fallido en ${env.JOB_NAME}",
                teamDomain: 'jenkins-vz85724',
                tokenCredentialId: 'slack-token'
            )
        }
        unstable {
            echo 'Pipeline inestable'
            slackSend(
                channel: '#notificaciones-dev',
                color: '#ffff00',
                message: "⚠️ Build inestable en ${env.JOB_NAME}",
                teamDomain: 'jenkins-vz85724',
                tokenCredentialId: 'slack-token'
            )
        }
    }
}