#!/bin/bash

dirs=(server controller todoist-tasks todoist-user todoist-chat todoist-reports)
for d in "${dirs[@]}"
do
    cd $d
    mvn install -DskipTests
    cd ..
    # echo "$d is a registered user"
done
