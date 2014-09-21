#!/bin/bash

# Setup Gravatar
Gravatar_setup() {
    local hash="$(printf "$1" | md5sum | tr -d ' *-')"
    local gravatar="$(curl -I "http://www.gravatar.com/avatar/$hash?d=404" 2> /dev/null)" 
    # If the user has no Gravatar, ...
    if [[ -z $(echo "$gravatar" | grep "HTTP/... 2.." ) ]]; then
        # Nag them to join
        curl -L "http://www.gravatar.com/avatar/$hash?d=retro&s=200" 2> /dev/null > ~/.gravatar.png
        echo "Help your instructor learn your name: create a Gravatar (profile picture)."
        echo "Press enter to see your default profile picture."
        Interactive_fileOpen ~/.gravatar.png
        rm -f ~/.gravatar.png
        
        echo "Press enter to sign up for Gravatar with your school email address."
        echo "Take a picture with your webcam or upload a recent photo of yourself."
        echo "$(Interactive_paste "$(User_getEmail)" "your email address")"
        Interactive_fileOpen "https://en.gravatar.com/gravatars/new"
    fi
}

