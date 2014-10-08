#!/bin/bash
# Test suite
# ---------------------------------------------------------------------

Test() {
    username=$(User_getUsername)
    fullname=$(User_getFullName)
    email=$(User_getEmail)
    echo $fullname
    echo $username
    echo $email
    
    echo "Valid email: $(Valid_email "LAWRANCEJ@WIT.EDU")"

    verified=$(Utility_nonEmptyValueMatchesRegex "$fullname" "")

    public_key=$(SSH_getPublicKey)
    echo $public_key
    connected=$(SSH_connected "github.com")
    echo "SSH connected: $connected"
    connected=$(SSH_connected "bitbucket.org")
    echo "SSH connected: $connected"
    connected=$(SSH_connected "gitlab.com")
    echo "SSH connected: $connected"
    # Git_showRepositories
    echo "$(Github_nameAvailable $(Host_getUsername "github"))"
    echo "$(Github_nameAvailable "asdlfkjawer2")"
}

query=$(Request_query "GET /?user.name=Joey+Lawrance&user.email=lawrancej%40wit.edu HTTP/1.1\n")
Query_lookup "$query" "user.email"
