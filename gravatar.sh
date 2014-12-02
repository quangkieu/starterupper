#!/bin/bash

Gravatar_valid() {
    local hash="$(printf "$1" | md5sum | tr -d ' *-')"
    local gravatar="$(curl -I "http://www.gravatar.com/avatar/$hash?d=404" 2> /dev/null)" 
    # If the user has no Gravatar, ...
    if [[ -z $(echo "$gravatar" | grep "HTTP/... 2.." ) ]]; then
        utility::fail
    else
        utility::succeess
    fi
}

