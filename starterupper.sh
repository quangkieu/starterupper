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

Github_setup() {
    User_setup
    echo $(User_getSchool)
    Github_setUsername
    Git_configureRepository "github.com" "$(Host_getUsername "github")" "$INSTRUCTOR_GITHUB"
    Github_authenticate
    Github_setFullName
    Github_createPrivateRepo
    Github_addCollaborator $INSTRUCTOR_GITHUB
    Github_sharePublicKey
    Git_pushRepo
    if [[ $(Utility_lastSuccess) ]]; then
        Git_showRepositories
        echo "Done"
    else
        echo -e "\e[1;37;41mERROR\e[0m: Unable to push."
        echo "Failed."
    fi
}

if [ $# == 0 ]; then
    Github_setup
elif [[ $1 == "clean" ]]; then
    Github_clean
elif [[ $1 == "collaborators" ]]; then
    Github_addCollaborators
fi
