#!/bin/bash

echo "Implantando nova versao do SEMPRODB"

DIR=/home/semprodb

## Postgres >=12
systemctl status postgresql  | head -5
echo

## JDK >= 17
JDK=$DIR/openjdk
if [ -d $JDK ]
then
    echo "=> JDK 21 em $JDK OK...."
else
    mkdir -p $JDK && \
    wget -qO- https://download.java.net/java/GA/jdk21/fd2272bbf8e04c3dbaee13770090416c/35/GPL/openjdk-21_linux-x64_bin.tar.gz | \
    tar xz -C $JDK
    if [ "21" -eq "0`$JDK/jdk-21/bin/javac --version | grep -so 21`" ]
    then
        echo "=> JDK 21 OK...."
    else
        echo "precisa instalar o JDK 21"
        exit
    fi
fi

## ******** ---------------------SERVICO---------------------------------------

## verificar se o servico esta instalado
if [ ! -f /etc/systemd/system/semprotdb.service ]
then
    echo "Instale o servico: /etc/systemd/system/semprotdb.service"
    ## systemctl daemon-reload
    echo "habilite o servico: sytemctl enable semprotdb"
    exit
else
    [ "`systemctl status semprotdb | head -5 | grep -so '\(running\)'`" ] && echo "=> Servico OK....."
fi

########################################
#/etc/systemd/system/semprotdb.service
########################################
#[Unit]
#Description=Semprotdb application
#After=syslog.target
#
#[Service]
#User=semprodb
#Restart=on-failure
#RestartSec=5
#ExecStart=/usr/local/bin/semprotdb.jar SuccessExitStatus=143
##ExecStart=java -Xmx6g -jar /usr/local/bin/semprotdb.jar SuccessExitStatus=143
##ExecStart=java -Dspring.datasource.url=jdbc:postgresql://localhost:5432/recov -jar /usr/local/bin/semprotdb.jar
#
#[Install]
#WantedBy=multi-user.target
########################################
#cp /home/semprodb/semprotdb/target/semprotdb-0.0.1-SNAPSHOT.jar /usr/local/bin/semprotdb.jar
#chown semprodb /usr/local/bin/semprotdb.jar

## ******** ------------------------PROXY------------------------------------
## verificar se o proxy esta conficurado
if [ ! -f /etc/httpd/conf.d/semprotdb.conf ]
then
    echo "Configure o proxy do apache /etc/httpd/conf.d/semprotdb.conf"
    exit
else
    [ "`getsebool httpd_can_network_connect | grep -so 'on' `" ] && echo "=> Proxy OK ...."
fi

########################################
#/etc/httpd/conf.d/semprotdb.conf
########################################
#ProxyPass /semprotdb http://localhost:8099/semprotdb
#ProxyPassReverse /semprotdb http://localhost:8099/semprotdb
########################################


## ******** ------------------------APLICACAO------------------------------------
## verficar o repositorio

## guardar versao antiga
ANTIGO_SALVO=$DIR/semprotdb_`date +d%dmes%m_%Hh%Mmin`
[ -d $DIR/semprotdb/ ] && \
   cp -r $DIR/semprotdb/ $ANTIGO_SALVO && \
   echo salvo em $ANTIGO_SALVO && \
   rm -rf $DIR/semprotdb/src/main/resources/config/application-prod.ym && \
   git -C $DIR/semprotdb pull

## clonar repo
[ ! -d $DIR/semprotdb/ ] && \
   cd $DIR && git clone git@github.com:seminalplasma/semprotdb.git $DIR/semprotdb && \
   echo "=> Repositorio clonado OK ...."


## atribuir variaveis de ambiente
if [ ! -f $DIR/application-prod.yml ]
then
    cp $DIR/semprotdb/src/main/resources/config/application-prod.yml $DIR/application-prod.yml
    echo "!!! Configure $DIR/application-prod.yml e tente novamente"
    exit
fi

cp $DIR/application-prod.yml $DIR/semprotdb/src/main/resources/config/application-prod.yml
[ ! -d $DIR/semprotdb/ ] && \
   mkdir /home/semprodb/logs

sed -i 's/Users\/miqueias\/ARQUIVOS/home\/semprodb\/logs/' $DIR/semprotdb/src/main/resources/logback-spring.xml

## compilar
rm -rf $DIR/semprotdb/target
cd $DIR/semprotdb && JAVA_HOME=$JDK/jdk-21 ./mvnw -Pprod clean verify -DskipTests=true
rm -rf $DIR/semprotdb/src/main/resources/config/application-prod.yml

## subir
cp $DIR/semprotdb/target/semprotdb-*-SNAPSHOT.jar /usr/local/bin/semprotdb.jar && \
   echo "REINICIAR O SERVICO:" && sudo systemctl restart semprotdb

echo "... OK ..."
echo
echo
sudo journalctl -u semprotdb.service | tail -50
echo
echo
systemctl status semprotdb
