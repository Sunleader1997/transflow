#!/bin/sh

APP_NAME="transflow-*.jar"
APP_HOME="/opt/transflow"

start() {
    nohup java -jar $APP_HOME/$APP_NAME > /dev/null 2>&1 &
    echo "$APP_NAME started with PID $!"
}

stop() {
    pid=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')
    [ -n "$pid" ] && kill -9 $pid
    echo "$APP_NAME stopped"
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        start
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
esac