#!/bin/bash
# Test suite
# ---------------------------------------------------------------------

Test() {
    username=$(user::getUsername)
    fullname=$(user::getFullName)
    email=$(user::getEmail)
    echo $fullname
    echo $username
    echo $email
    
    echo "Valid email: $(valid::email "LAWRANCEJ@WIT.EDU")"

    verified=$(utility::nonEmptyValueMatchesRegex "$fullname" "")

    public_key=$(ssh::getPublicKey)
    echo $public_key
    connected=$(ssh::connected "github.com")
    echo "SSH connected: $connected"
    connected=$(ssh::connected "bitbucket.org")
    echo "SSH connected: $connected"
    connected=$(ssh::connected "gitlab.com")
    echo "SSH connected: $connected"
    # git::showRepositories
    echo "$(github::nameAvailable $(Host_getUsername "github"))"
    echo "$(github::nameAvailable "asdlfkjawer2")"
}

query=$(Request_query "GET /?user.name=Joey+Lawrance&user.email=lawrancej%40wit.edu HTTP/1.1\n")
Query_lookup "$query" "user.email"
