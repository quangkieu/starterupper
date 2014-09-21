#!/bin/bash
# Gitlab functions
# ---------------------------------------------------------------------

gitlab_configure() {
    local_setup
    echo "Join GitLab."
    sleep 2
    file_open "https://gitlab.com/users/sign_up"
}

gitlab_authenticate() {
    if [[ -n "$(git config --global gitlab.token)" ]]; then
        return 0
    fi

    echo "Copy your private token from GitLab"
    sleep 2
    file_open "https://gitlab.com/profile/account"
    
    read -p "Paste your private token here: " token < /dev/tty
    
    while [[ ! -z "$(curl https://gitlab.com/api/v3/user?private_token=$token | grep '401 Unauthorized')" ]]; do
        echo "ERROR: Invalid private token."
        read -p "Paste your private token here: " token < /dev/tty    
    done
    git config --global gitlab.token "$token"
}

gitlab_setup_ssh() {
    gitlab_authenticate
    
    # Check if public key is shared
    publickey_shared=$(curl https://gitlab.com/api/v3/user/keys?private_token=$(git config --global gitlab.token) 2> /dev/null | grep $(cat ~/.ssh/id_rsa.pub | sed -e 's/ssh-rsa \(.*\)=.*/\1/'))
    # If not, share it
    if [[ -z "$publickey_shared" ]]; then
        echo "Sharing public key..."
        curl -i -H -d "{\"title\": \"$(hostname)\", \"key\": \"$(cat ~/.ssh/id_rsa.pub)\"}" https://api.github.com/user/keys?private_token=$(git config --global gitlab.token) 2> /dev/null > /dev/null
    fi
}
