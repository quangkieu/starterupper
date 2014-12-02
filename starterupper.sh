#!/bin/bash
# Starter Upper: Setup git hosting for classroom use with minimal user interaction.

# Configuration
# ---------------------------------------------------------------------

# The repository to clone as upstream (NO SPACES)
readonly REPOSITORY=starterupper
# Default domain for school email
readonly SCHOOL=wit.edu
# The instructor's Github username
readonly INSTRUCTOR_GITHUB=lawrancej

# Runtime flags (DO NOT CHANGE)
# ---------------------------------------------------------------------
readonly PROGNAME=$(basename $0)
readonly ARGS="$@"

# Download a script and source it
Import() {
    local url="$1"
    local file="$(printf "$url" | sed "s/.*\/\(.*\)$/\1/")"
    curl -L "$url" > "$file"
    source "$file"
}

source utility.sh
source cli.sh
source gravatar.sh
source github.sh

github::setup() {
    user::setup
    echo $(user::getSchool)
    github::setUsername
    git::configureRepository "github.com" "$(Host_getUsername "github")" "$INSTRUCTOR_GITHUB"
    github::authenticate
    github::setFullName
    github::createPrivateRepo
    github::addCollaborator $INSTRUCTOR_GITHUB
    github::sharePublicKey
    git::pushRepo
    if [[ $(utility::lastSuccess) ]]; then
        git::showRepositories
        echo "Done"
    else
        echo -e "\e[1;37;41mERROR\e[0m: Unable to push."
        echo "Failed."
    fi
}

if [ $# == 0 ]; then
    github::setup
elif [[ $1 == "clean" ]]; then
    github::clean
elif [[ $1 == "collaborators" ]]; then
    github::addCollaborators
fi
