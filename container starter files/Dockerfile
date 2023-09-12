FROM jboss/base:latest

ARG USER_NAME="main"
ARG USER_PASSWORD="main"

ENV USER_NAME $USER_NAME
ENV USER_PASSWORD $USER_PASSWORD
ENV CONTAINER_IMAGE_VER=v1.0.0

RUN echo $USER_NAME
RUN echo $USER_PASSWORD
RUN echo $CONTAINER_IMAGE_VER

USER root

RUN yum install -y epel-release \
    && yum install -y java-latest-openjdk \
    curl \
    git-core \
    less \
    passwd \
    sudo \
    vim \
    wget \
    zsh \
    # add a user (--disabled-password: the user won't be able to use the account until the password is set)
    && adduser -p ${USER_PASSWORD} --shell /bin/zsh --home /home/$USER_NAME -c "User" ${USER_NAME} \
    # update the password
    && echo ${USER_NAME}:${USER_PASSWORD} | chpasswd \
    && usermod -aG wheel ${USER_NAME}
  
# install WildFly
RUN curl -LO https://download.jboss.org/wildfly/24.0.1.Final/wildfly-24.0.1.Final.tar.gz \
 && tar zxvf wildfly-24.0.1.Final.tar.gz \
 && mv wildfly-24.0.1.Final /opt/jboss/wildfly \
 && rm wildfly-24.0.1.Final.tar.gz
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin#7rules --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u ejbclient 'ejb01acceS' --silent -g 'guest'
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u john '!1secret' --silent -g 'guest'
ADD --chmod=0755 wildfly-init-config.sh /opt/jboss/wildfly/bin
ADD --chmod=0755 configure.cli /opt/jboss/wildfly/bin

USER jboss

CMD [ "/opt/jboss/wildfly/bin/wildfly-init-config.sh" ]  
