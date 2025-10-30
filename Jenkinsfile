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
        SONAR_ORGANIZATION = 'your-organization-key'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/tu-organizacion/vg-ms-claims-incidents.git'
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile -DskipTests'
            }
        }
        
        stage('Run Unit Tests with Coverage') {
            steps {
                sh 'mvn test'
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
                sh """
                    mvn sonar:sonar \
                    -Dsonar.projectKey=vg-ms-claims-incidents \
                    -Dsonar.organization=${SONAR_ORGANIZATION} \
                    -Dsonar.host.url=https://sonarcloud.io \
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
                sh 'mvn package -DskipTests'
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline ejecutado correctamente'
            slackSend channel: '#jenkins-notifications', message: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' completed successfully"
        }
        failure {
            echo 'Pipeline fallido'
            slackSend channel: '#jenkins-notifications', message: "FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' failed"
        }
    }
}