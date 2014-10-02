#!/bin/bash

source utility.sh

readonly PIPE=.httpipe

# http://mywiki.wooledge.org/NamedPipes
# Also, simultaneous connections
# Also, windows pipes aren't
# http://hak5.org/episodes/haktip-53

PrintIndex() {
    sed -e "s/REPOSITORY/$REPO/g" \
    -e "s/EMAIL/$(User_getEmail)/g" \
    -e "s/FULL_NAME/$(User_getFullName)/g" \
    -e "s/GITHUB_LOGIN/$(Host_getUsername github)/g" \
    -e "s/INSTRUCTOR_GITHUB/$INSTRUCTOR_GITHUB/g" \
    index.html
}

# Is this a request line?
Request_line() {
    local line="$1"
    if [[ -z "$(printf "$line" | grep -E "GET|HEAD|POST|PUT|DELETE|CONNECT|OPTIONS|TRACE")" ]]; then
        Utility_fail
    fi
    Utility_success
}

# Get the method (e.g., GET, POST) of the request
Request_method() {
    local request="$1"
    echo "$request" | sed -e "s/\(^[^ ]*\).*/\1/"
}

# Get the target (URL) of the request
Request_target() {
    local request="$1"
    echo "$request" | sed -e 's/^[^ ]* \(.*\) HTTP\/.*/\1/'
#    if [[ -z "$url" ]]; then
#        url="index.html"
#    fi
}

# Given a header key, return the value
Request_lookup() {
    local request="$1"; shift
    local key="$1"
    printf "$request" | grep "$key" | sed -e "s/^$key: \(.*\)/\1/"
}

# Return the payload of the request, if any (e.g., for POST requests)
Request_payload() {
    local request="$1"; shift
    printf "$request" | sed -n -e '/^$/,${p}'
}

# Pipe HTTP request into a string
Request_new() {
    local line="$1"
    # If we got a request, ...
    if [[ $(Request_line "$line") ]]; then
        local request="$line"
        # Read all headers
        while read header; do
            request="$request\n$header"
            if [[ -z "$header" ]]; then
                break
            fi
        done
        # Sometimes, we have a payload in the request, so handle that, too...
        local length=$(Request_lookup "$request" "Content-Length")
        local payload=""
        if [[ "$length" != "0" ]]; then
            read -n "$length" payload
            request="$request\n$payload"
        fi
    fi
    # Return single line string
    echo "$request" >&2
    echo "$request"
}

# Send out HTTP response and headers
Response_send() {
    local response="$1"; shift
    local length="$1"; shift
    local type="$1";

    echo -ne "$response\r\nDate: $(date '+%a, %d %b %Y %T %Z')\r\nServer: Starter Upper\r\nContent-Length: $length\r\nContent-Encoding: binary\r\nContent-Type: $type\r\n\r\n"
}

# Send file with HTTP response headers
WebServer_sendFile() {
    local file="$1";
    local response="HTTP/1.1 200 OK"
    if [[ -z "$file" ]]; then
        return 0
    fi
    echo "OPEN FILE: $file" >&2
    if [[ ! -f "$file" ]]; then
        response="HTTP/1.1 404 Not Found"
        file="404.html"
    fi
    local type="$(Utility_MIMEType $file)"
    echo "SENDING $file" >&2
    Response_send "$response" "$(Utility_fileSize "$file")" "$type"
    cat "$file"
    printf "\n"
    echo "FINISHED $file" >&2
}

# Listen for requests
WebServer_listen() {
    local request=""
    while read line; do
        request=$(Request_new "$line")
        Pipe_write "$PIPE" "$request\n"
    done
}

WebServer_route() {
    local request="$1"
    local target=""
    echo "FILE $(Request_target "$request")" >&2
    target="$(Request_target "$request")"
    if [[ "$target" == "/" ]]; then
        target="index.html"
    fi
    WebServer_sendFile "$target"
}

# Respond to requests
WebServer_respond() {
    local request=""
    Pipe_await "$PIPE"
    while sleep 1; do
        request="$(Pipe_read "$PIPE")"
        WebServer_route "$request"
    done
}

WebServer_start() {
    Acquire_software
    rm debug 2> /dev/null
    Pipe_new "$PIPE"
    WebServer_respond | nc -o debug -k -lvv 8080 | WebServer_listen
}

Utility_fileOpen http://localhost:8080 > /dev/null
WebServer_start
