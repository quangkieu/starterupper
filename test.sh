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

#curl -H 'Content-Type: text/xml' -d '<methodCall><methodName>grav.addresses</methodName><params><param><value><struct><member><name>password</name><value><string>l</string></value></member></struct></value></param></params></methodCall>' https://secure.gravatar.com/xmlrpc?user=2d2ab5c40ea7b5ed9c532f45026d7f2f
query=$(Request_query "GET /?user.name=Joey+Lawrance&user.email=lawrancej%40wit.edu HTTP/1.1\n")
Query_lookup "$query" "user.email"
