mvn clean package

VERSION=`grep '<version' pom.xml | cut -f2 -d">"|cut -f1 -d"<"|head -n1`
GROUP_ID=`grep '<groupId' pom.xml | cut -f2 -d">"|cut -f1 -d"<"|head -n1`
NAME=`grep '<name' pom.xml | cut -f2 -d">"|cut -f1 -d"<"|head -n1`

CLASSPATH=""
for f in `find target/$NAME-$VERSION/$NAME/lib/ -name '*jar'`; do CLASSPATH=$CLASSPATH":"$f; done

java -cp target/$NAME-$VERSION.jar$CLASSPATH $GROUP_ID.Main
