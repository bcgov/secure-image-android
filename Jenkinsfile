// See https://github.com/jenkinsci/kubernetes-plugin
podTemplate(label: 'android-build', name: 'android-build', serviceAccount: 'jenkins', cloud: 'openshift', containers: [
  containerTemplate(
    name: 'jnlp',
    image: '172.50.0.2:5000/devex-mpf-secure-tools/jenkins-slave-android-rhel7:latest',
    resourceRequestCpu: '1500m',
    resourceLimitCpu: '2000m',
    resourceRequestMemory: '6Gi',
    resourceLimitMemory: '8Gi',
    workingDir: '/tmp',
    command: '',
    args: '${computer.jnlpmac} ${computer.name}',
    envVars: [
      secretEnvVar(key: 'ANDROID_DECRYPT_KEY', secretName: 'android-decrypt-key', secretKey: 'password'),
      envVar(key: '_JAVA_OPTIONS', value: '-Xmx4g')
    ],
    alwaysPullImage: true
  )
]) {
  node('android-build') {

    stage('Checkout') {
      echo "Checking out source"
      checkout scm
    }

    stage('Setup') {
      echo "Build setup"
      // Decrypt the Android keystore properties file.
      sh "/usr/bin/openssl aes-256-cbc -d -a -in keystore.properties.enc -out keystore.properties -pass env:ANDROID_DECRYPT_KEY"
      // Use the following two lines to disable the gradle daemon. This can make builds faster if the intermediary
      // artifacts (download, etc) are *not* preserved. If they are, keep the daemon running. Overwrite other options
      // because the will likley prevent this param from beng applied and the daemon will start.
      // sh "mkdir -p $HOME/.gradle"
      // sh "echo 'org.gradle.daemon=false' >$HOME/.gradle/gradle.properties"
      // Help cut down on warning messages from the Android SDK
      sh "mkdir -p $HOME/.android"
      sh "touch $HOME/.android/repositories.cfg"
      // The JVM / Kotlin Daemon will quite often fail in memory constraind environments. Givng the JVM
      // 4g allows for the maxumum reqested / recommended by gradle. This is passed in the pod spec as a
      // global: _JAVA_OPTIONS="-Xmx4g"
    }
    
    stage('Build') {
      echo "Build: ${BUILD_ID}"
      sh """
        JAVA_HOME=\$(dirname \$( readlink -f \$(which java) )) && \
        JAVA_HOME=\$(realpath "$JAVA_HOME"/../) && \
        export JAVA_HOME && \
        export ANDROID_HOME=/opt/android && \
        ./gradlew build -x test
      """
    }
  }
}
