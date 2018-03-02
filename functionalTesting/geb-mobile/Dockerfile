FROM registry.access.redhat.com/openshift3/jenkins-slave-base-rhel7

EXPOSE 8080
   
ENV SUMMARY="Jenkins slave with Java openjdk installed for mobile BDDStack." \
    DESCRIPTION="Jenkins pipeline slave with mobile app testing with mobile-BDDStack"

LABEL summary="$SUMMARY" \
      description="$DESCRIPTION" \
      io.k8s.description="$DESCRIPTION" \
      io.k8s.display-name="Jenkins-Pipeline-Mobile-BDDStack" \
      io.openshift.expose-services="8080:http" \
      io.openshift.tags="builder,jenkins-jnlp" \
      release="1"

RUN yum repolist > /dev/null && \
    yum install -y yum-utils && \
    yum-config-manager --disable \* &> /dev/null && \
    yum-config-manager --enable rhel-server-rhscl-7-rpms && \
    yum-config-manager --enable rhel-7-server-rpms && \
    yum-config-manager --enable rhel-7-server-optional-rpms && \
    yum-config-manager --enable rhel-7-server-fastrack-rpms && \
    INSTALL_PKGS="java-1.8.0-openjdk" && \
    yum install -y --setopt=tsflags=nodocs $INSTALL_PKGS && \
    rpm -V $INSTALL_PKGS && \
    yum clean all -y && \
    mkdir /opt/app

# Build stage will set JAVA_HOME, this might not be needed?
ENV JAVA_HOME "/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.161-0.b14.el7_4.x86_64/jre"
ENV PATH=$JAVA_HOME/bin:$PATH

USER 1001