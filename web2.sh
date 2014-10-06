#!/bin/bash

source web.sh

PrintIndex() {
    sed -e "s/REPOSITORY/$REPO/g" \
    -e "s/EMAIL/$(User_getEmail)/g" \
    -e "s/FULL_NAME/$(User_getFullName)/g" \
    -e "s/GITHUB_LOGIN/$(Host_getUsername github)/g" \
    -e "s/INSTRUCTOR_GITHUB/$INSTRUCTOR_GITHUB/g" \
    index.html > temp.html

    WebServer_sendFile "temp.html"
    rm temp.html
}

MyRouter() {
    local request="$1"
    local target="$(Request_file "$request")"
    case "$target" in
        "/" ) PrintIndex "$request" ;;
        * )   WebServer_sendFile "$request"
    esac
}

WebServer_start "MyRouter"
# if [[ "$(Utility_fileOpen http://localhost:8080)" ]]; then
    # echo -e "Opened web browser to http://localhost:8080                                [\e[1;32mOK\e[0m]" >&2
# else
    # echo -e "Please open web browser to http://localhost:8080              [\e[1;32mACTION REQUIRED\e[0m]" >&2
# fi

