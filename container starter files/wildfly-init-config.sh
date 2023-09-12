#! /bin/sh

#start wildfly
/opt/jboss/wildfly/bin/standalone.sh -c standalone-full.xml -b 0.0.0.0 -bmanagement 0.0.0.0 &

#install the driver and the data source
#the challenge is to have the server ready---it may take a while
RC=1
count=0
while [ $RC -ne 0 ] && [ $count -lt 20 ]
do
    sleep 5
    /opt/jboss/wildfly/bin/jboss-cli.sh --connect ""
    RC=$?
    let count++
done

if [ $RC -eq 0 ]
then
    /opt/jboss/wildfly/bin/jboss-cli.sh --connect --file=/opt/jboss/wildfly/bin/configure.cli
    #keep running forever
    tail -f /dev/null
fi

