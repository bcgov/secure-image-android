// See https://github.com/jenkinsci/kubernetes-plugin
podTemplate(label: 'android-build', name: 'android-build', serviceAccount: 'jenkins', cloud: 'openshift', containers: [
  containerTemplate(
    name: 'jnlp',
    image: '172.50.0.2:5000/devex-mpf-secure-tools/jenkins-slave-android-rhel7',
    resourceRequestCpu: '500m',
    resourceLimitCpu: '1000m',
    resourceRequestMemory: '1Gi',
    resourceLimitMemory: '2Gi',
    workingDir: '/tmp',
    command: '',
    args: '${computer.jnlpmac} ${computer.name}'
  )
]) {
  node('android-build') {

    stage('Checkout') {
      echo "Checking out source"
      checkout scm
    }

    stage('Test') {
      echo "Testing: ${BUILD_ID}"
      // sh "javac -version"
      sh "ANDROID_HOME=/opt/android ./gradlew test"
    }
    
    stage('Build') {
      echo "Build: ${BUILD_ID}"
      sh "ANDROID_HOME=/opt/android ./gradlew build"
    }

  }
}
