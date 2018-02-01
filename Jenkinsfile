node('maven'){

  stage('Checkout') {
    echo "Checking out source"
    checkout scm
  }

  stage('Test') {
    echo "Testing: ${BUILD_ID}"
    sh "./gradlew test"
  }
  
  stage('Build') {
    echo "Build: ${BUILD_ID}"
    sh "./gradlew build"
  }

}
