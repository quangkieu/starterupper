#!/bin/bash

source web.sh
source github.sh

PrintIndex() {
    local request="$1"
    
    echo "$(Request_payload "$request")" >&2
#    printf "$(Request_query "$request")" >&2
    
    Request_postFormData "$request" | while read parameter; do
        local key="$(Parameter_key "$parameter")"
        local value="$(Parameter_value "$parameter")"
        case "$key" in
            "user.name" )
                User_setFullName "$value"
                ;;
            "user.email" )
                User_setEmail "$value"
#                echo "the email is $value" >&2 ;;
                ;;
            
        esac
    done
    
    local githubLoggedIn=$(Utility_asTrueFalse $(Github_loggedIn))

    sed -e "s/REPOSITORY/$REPO/g" \
    -e "s/EMAIL/$(User_getEmail)/g" \
    -e "s/FULL_NAME/$(User_getFullName)/g" \
    -e "s/GITHUB_LOGIN/$(Host_getUsername github)/g" \
    -e "s/INSTRUCTOR_GITHUB/$INSTRUCTOR_GITHUB/g" \
    -e "s/GITHUB_LOGGED_IN/$githubLoggedIn/g" \
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

Utility_fileOpen http://localhost:8080
WebServer_start "MyRouter"

# if [[ "$(Utility_fileOpen http://localhost:8080)" ]]; then
    # echo -e "Opened web browser to http://localhost:8080                                [\e[1;32mOK\e[0m]" >&2
# else
    # echo -e "Please open web browser to http://localhost:8080              [\e[1;32mACTION REQUIRED\e[0m]" >&2
# fi

