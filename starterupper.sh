#!/bin/bash

# Starter Upper: Setup git hosting for classroom use with minimal user interaction.

# Configuration
# ---------------------------------------------------------------------

# The instructor's name
INSTRUCTOR_NAME="Joey Lawrance"
# The instructor's email
INSTRUCTOR_EMAIL="lawrancej@wit.edu"
# The instructor's Github username
INSTRUCTOR_GITHUB=lawrancej

# The repository to clone as upstream (NO SPACES)
REPO=COMP603-2014
# School domain to use in email address example
SCHOOL=wit.edu

# More issues:
# add email to github via api, if not set (e.g., for existing users registered with a different email)
# ask user to validate email if not validated
# check if email is validated via api on github before attempting to create a private repo
# check network connectivity
# determine if username is found via api as a last minute sanity check
# go through all pages when fetching usernames
# grading interface: checkout stuff rapid fire like, possibly use git notes

# Utilities
# ---------------------------------------------------------------------

next_step() {
    read -p "Press enter to continue." < /dev/tty
}

# Cross platform file open
file_open() {
    case $OSTYPE in
        msys | cygwin ) echo "Opening $1"; sleep 1; start "$1" ;;
        linux* ) echo "Opening $1"; sleep 1; gnome-open "$1" ;;
        darwin* ) echo "Opening $1"; sleep 1; open "$1" ;;
        *) echo "Please open $1" ;;
    esac
    next_step
}

# Git functions
# ---------------------------------------------------------------------

# Ask the user if they are the instructor (by default, assume that they aren't)
instructor_check() {
    if [[ ! $user_is_instructor ]]; then
        while ! [[ -z "$value" ]] && [[ "$value" == "$instructor" ]]; do
            read -p "Are you the instructor (yes/no)? " user_is_instructor < /dev/tty
            case "$user_is_instructor" in
                [Yy] | [Yy][Ee][Ss] ) user_is_instructor=true; return 0 ;;
                [Nn] | [Nn][Oo] ) user_is_instructor=false; git config --global --unset $key; value=''; return 0 ;;
                "" ) user_is_instructor=false; git config --global --unset $key; value=''; return 0 ;;
                * ) echo "Please answer yes or no." ;;
            esac
        done
    fi
}

validate_input() {
    instructor_check
    if ! [[ -z "$value" ]] && [[ -z $(echo "$value" | grep -E "$validation_regex" ) ]]; then
        echo "ERROR: '$value' is not a $prompt. $invalid_prompt"
    fi
}

# Ask the user if they want to change their configuration in case of goof-ups (by default, assume that they don't)
change_config() {
    while ! [[ -z "$value" ]]; do
        read -p "Is your $prompt $value (yes/no)? " yn < /dev/tty
        case "$yn" in
            [Yy] | [Yy][Ee][Ss] ) return 0 ;;
            [Nn] | [Nn][Oo] ) git config --global --unset $key; value=''; return 0 ;;
            "" ) return 0 ;;
            * ) echo "Please answer yes or no." ;;
        esac
    done
}

# Ask user to set a key in ~/.gitconfig if it's not already set.
# Sets $value
set_key() {
    key=$1
    prompt=$2
    example=$3
    validation_regex=$4
    invalid_prompt=$5
    instructor=$6
    
    value=$(git config --global $key)

    change_config
    validate_input
    while [[ -z "$value" ]] || [[ -z $(echo "$value" | grep -E "$validation_regex" ) ]]; do
        read -p "Enter your $prompt (e.g., $example): " value < /dev/tty
        validate_input
    done
    git config --global $key "$value"
}

# Sets $fullname and $email
configure_git() {
    set_key user.name "full name" "Jane Smith" "\w+ \w+" "Include your first and last name." "$INSTRUCTOR_NAME"
    fullname=$value
    set_key user.email "school email address" "smithj@$SCHOOL" "edu$" "Use your .edu address." "$INSTRUCTOR_EMAIL"
    email=$value
}

# Setup remotes for repository
# $1 is the SSH origin URL
# $2 is the HTTPS upstream URL
configure_remotes() {
    cd ~/$REPO
    echo "Configuring remotes..."
    # We remove and add again, in case the user goofed up before
    git remote rm origin 2> /dev/null
    git remote rm upstream 2> /dev/null
    git remote add origin $1
    git remote add upstream $2
    git config branch.master.remote origin
    git config branch.master.merge refs/heads/master
    cd ~
}

# SSH functions
# ---------------------------------------------------------------------

generate_key() {
    if [[ ! -f ~/.ssh/id_rsa.pub ]]; then
        printf "\n" | ssh-keygen -t rsa -N '' # Default location, no phassphrase, no questions asked
    fi
}

copy_key_to_clipboard() {
    case $OSTYPE in
        msys | cygwin ) echo "Copied your public key to clipboard."; cat ~/.ssh/id_rsa.pub > /dev/clipboard ;;
        linux* ) echo "Copied your public key to clipboard."; cat ~/.ssh/id_rsa.pub | xclip -selection clipboard ;; # -i ;;
        darwin* ) echo "Copied your public key to clipboard."; cat ~/.ssh/id_rsa.pub | pbcopy ;;
        * ) echo "Copy your public key (below) to the clipboard:"; cat ~/.ssh/id_rsa.pub ;;
    esac
}

# Github functions
# ---------------------------------------------------------------------

github_join() {
    if [ -z $(git config --global github.login) ]; then
        read -p "Do you have a Github account (yes or No [default])? " has_github_account < /dev/tty
        # Let's assume that they don't by default
        if [[ $has_github_account != [Yy]* ]]; then
            echo "Join Github. IMPORTANT: Use $email as your email address."
            sleep 2
            file_open "https://github.com/join"
            echo "Open your school email inbox and verify your email with Github."
            sleep 2
            next_step
        fi
    fi
}

# Wow, it's complicated
# https://github.com/sessions/forgot_password
# Sets $github_login and generates ~/.token with authentication token
github_authenticate() {
    set_key github.login "Github username" "smithj" "^[0-9a-zA-Z][0-9a-zA-Z-]*$" "See: https://github.com" "$INSTRUCTOR_GITHUB"
    github_login=$(git config --global github.login)
    if [[ ! -f ~/.token ]]; then
        token="HTTP/1.1 401 Unauthorized"
        code=''
        password=''
        while [[ ! -z $(echo $token | grep "HTTP/1.1 401 Unauthorized" ) ]]; do
            if [[ -z "$password" ]]; then
                read -s -p "Enter Github password: " password < /dev/tty
            fi
            token=$(curl -i -u $github_login:$password -H "X-GitHub-OTP: $code" -d '{"scopes": ["repo", "public_repo", "user", "write:public_key", "user:email"], "note": "starterupper-script"}' https://api.github.com/authorizations 2> /dev/null)
            echo
            if [[ ! -z $(echo $token | grep "Bad credential") ]]; then
                echo "Incorrect password. Please wait a moment."
                password=''
                sleep 3
            fi
            if [[ ! -z $(echo $token | grep "two-factor" ) ]]; then
                read -p "Enter Github two-factor authentication code: " code < /dev/tty
            fi
        done
        if [[ ! -z $(echo $token | grep "HTTP/... 20." ) ]]; then
            # Extract token and save to ~/.token
            token=$(echo $token | tr '"' '\n' | grep -E '[0-9a-f]{40}')
            echo $token > ~/.token
            echo "Authenticated!"
        else
            printf "Error: "
            echo $token | grep "HTTP/..."
            echo "Sorry, try again later."
            exit 1
        fi
    fi
}

github_share_key() {
    echo "Sharing public key..."
    curl -i -H "Authorization: token $(cat ~/.token)" -d "{\"title\": \"$(hostname)\", \"key\": \"$(cat ~/.ssh/id_rsa.pub)\"}" https://api.github.com/user/keys 2> /dev/null > /dev/null
}

github_set_name() {
    echo "Updating Github profile information..."
    curl --request PATCH -H "Authorization: token $(cat ~/.token)" -d "{\"name\": \"$(git config --global user.name)\"}" https://api.github.com/user 2> /dev/null > /dev/null
}

github_setup_ssh() {
    generate_key
    
    # Force accept host key
    ssh_test=$(ssh -oStrictHostKeyChecking=no git@github.com 2>&1)
    if [[ -z $(echo $ssh_test | grep $github_login) ]]; then
        github_share_key
    fi
}

github_request_discount() {
    github_check_email
    if [[ -z "$github_verified_email" ]]; then
        github_verify_email
    fi
    echo "Request an individual student discount."
    sleep 2
    file_open "https://education.github.com/discount_requests/new"
}

github_create_private_repo() {
    result=$(curl -H "Authorization: token $(cat ~/.token)" https://api.github.com/repos/$github_login/$REPO 2> /dev/null)
    if [[ ! -z $(echo $result | grep "Not Found") ]]; then
        echo "Creating private repository $github_login/$REPO on Github..."
        result=$(curl -H "Authorization: token $(cat ~/.token)" -d "{\"name\": \"$REPO\", \"private\": true}" https://api.github.com/user/repos 2> /dev/null)
        if [[ ! -z $(echo $result | grep "over your quota" ) ]]; then
            echo "Unable to create private repository because you are over quota."
            github_request_discount
            result=$(curl -H "Authorization: token $(cat ~/.token)" -d "{\"name\": \"$REPO\", \"private\": true}" https://api.github.com/user/repos 2> /dev/null)
            if [[ ! -z $(echo $result | grep "over your quota" ) ]]; then
                echo "Unable to create private repository because you are over quota."
                echo "Wait for the discount and try again."
                if [[ $has_github_account == [Yy]* ]]; then
                    echo "You may need to free up some private repositories."
                    sleep 1
                    file_open "https://github.com/settings/repositories"
                fi
                echo "Failed"
                exit 1
            fi
        fi
    fi
}

github_add_collaborator() {
    echo "Adding $1 as a collaborator..."
    curl --request PUT -H "Authorization: token $(cat ~/.token)" -d "" https://api.github.com/repos/$github_login/$REPO/collaborators/$1 2> /dev/null > /dev/null
}

github_add_collaborators() {
    cd ~/$REPO
    for repository in $(curl -i -H "Authorization: token $(cat ~/.token)" https://api.github.com/user/repos?type=member\&sort=created\&page=1\&per_page=100 2> /dev/null | grep "full_name.*$REPO" | sed s/.*full_name....// | sed s/..$//); do
        git remote add ${repository%/*} git@github.com:$repository.git
    done
    git fetch --all
}

# Ask the user to verify their email address
github_verify_email() {
    echo "IMPORTANT: Add/verify $email at Github."
    sleep 3
    file_open "https://github.com/settings/emails"
}

# Sets $github_verified_email if the user did things right
github_check_email() {
    github_verified_email=$(curl -H "Authorization: token $(cat ~/.token)" https://api.github.com/user/emails | tr '\n}[]{' ' \n   ' | grep "$email" | grep "verified...true")
}

github_user() {
    curl -i https://api.github.com/users/$github_login 2> /dev/null
}

github_setup() {
    github_join
    github_authenticate
    github_set_name
    github_setup_ssh
    github_create_private_repo
    github_add_collaborator $INSTRUCTOR_GITHUB
    setup_repo
}

setup_repo() {
    cd ~
    origin="git@github.com:$github_login/$REPO.git"
    upstream="https://github.com/$INSTRUCTOR_GITHUB/$REPO.git"
    if [ ! -d $REPO ]; then
        git clone "$upstream"
    fi
    configure_remotes "$origin" "$upstream"
    file_open $REPO
    cd $REPO
    git push origin master
    result=$(echo $?)
    if [[ $result != 0 ]]; then
        echo "Unable to push. Your network blocked SSH."
        echo "Failed. Try again when you have full network access."
    else
        file_open "https://github.com/$github_login/$REPO"
        echo "Done"
    fi
}

github_revoke() {
    echo "Delete starterupper-script under Personal access tokens"
    file_open "https://github.com/settings/applications"
}


# Clean up everything but the repo (BEWARE!)
clean() {
    github_revoke
    sed -i s/.*github.com.*// ~/.ssh/known_hosts
    git config --global --unset user.name
    git config --global --unset user.email
    git config --global --unset github.login
    rm -f ~/.ssh/id_rsa*
    rm -f ~/.token
}

if [ $# == 0 ]; then
    configure_git
    github_setup
elif [[ $1 == "clean" ]]; then
    clean
elif [[ $1 == "collaborators" ]]; then
    github_add_collaborators
fi

# github_user
