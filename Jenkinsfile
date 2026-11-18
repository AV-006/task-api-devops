pipeline {
    agent any

    // Jenkins checks the configured branch every minute. Run once manually
    // after creating the Pipeline job so Jenkins registers this trigger.
    triggers { pollSCM('* * * * *') }

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '15'))
    }

    environment {
        APP_NAME = 'task-api'
    }

    stages {
        stage('Checkout and Version') {
            steps {
                checkout scm
                script {
                    env.GIT_SHORT_SHA = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.BUILD_VERSION = "${env.BUILD_NUMBER}-${env.GIT_SHORT_SHA}"
                    currentBuild.displayName = "#${env.BUILD_VERSION}"
                    echo "Building ${env.APP_NAME} ${env.BUILD_VERSION}"
                }
            }
        }

        // CI enhancement: tests run before packaging; a test failure blocks the build.
        stage('Test') {
            steps { sh 'mvn -B clean test' }
            post { always { junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml' } }
        }

        stage('Build') {
            steps {
                sh 'mvn -B package -DskipTests'
                sh 'cp target/task-api.jar target/task-api-${BUILD_VERSION}.jar'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/task-api-*.jar', fingerprint: true
            echo "SUCCESS: ${env.APP_NAME}-${env.BUILD_VERSION}.jar archived"
        }
        failure { echo 'Pipeline failed. Review the Test or Build stage console output.' }
    }
}
