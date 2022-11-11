pipeline {
    agent any
    tools{
        maven 'localMaven'
        jdk 'localJdk'
    }
    stages {
        stage('Git checkout') {
            steps {
                echo 'Cloning the application code...'
                git branch: 'main', url: 'https://github.com/Frankhill-che/Jenkins-CI-CD-Pipeline-Project.git'
            }
        }
        stage('Build') {
            steps {
                sh 'java -version'
                sh 'mvn clean package'
            }
            post {
                success {
                    echo 'archiving....'
                    archiveArtifacts artifacts: '**/*.war', followSymlinks: false
                }
            }
        }
    stage('Unit Test'){
        steps {
            sh 'mvn test'
        }
    }
    stage('Integration Test'){
        steps {
          sh 'mvn verify -DskipUnitTests'
        }
    }
    stage ('Checkstyle Code Analysis'){
        steps {
            sh 'mvn checkstyle:checkstyle'
        }
        post {
            success {
                echo 'Generated Analysis Result'
            }
        }
    }
    stage ('SonarQube scanning'){
        steps {
            withSonarQubeEnv('SonarQube') {
            sh """
            mvn sonar:sonar \
          -Dsonar.projectKey=JavaWebApp \
          -Dsonar.host.url=http://172.31.89.106:9000 \
          -Dsonar.login=eb71cc32022d0478b157b601133934c966fd8a04
            """
            }
        }
    }
  stage("Quality Gate"){
      steps{
       waitForQualityGate abortPipeline: true
         }
    }
    }
}