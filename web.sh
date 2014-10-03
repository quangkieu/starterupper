#!/bin/bash

source utility.sh

readonly PIPE=.httpipe

# http://mywiki.wooledge.org/NamedPipes
# Also, simultaneous connections
# Also, windows pipes aren't
# http://hak5.org/episodes/haktip-53

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

# Get the file from the request
Request_file() {
    local request="$1"
    # Remove attempts to look outside the current folder, strip off the leading slash and the query
    local target="$(Request_target "$request")"
    if [[ "$target" == "/" ]]; then
        printf "/"
    else
        echo "$target" | sed -e 's/[.][.]//g' -e 's/^[/]*//g' -e 's/[?].*$//'
    fi
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
        local length="$(Request_lookup "$request" "Content-Length")"
        local payload=""
#        echo "length: $length" >&2
        if [[ -n "$length" ]] && [[ "$length" != "0" ]]; then
            read -n "$length" payload
            request="$request\n$payload"
        fi
    fi
    # Uncomment to debug
#    printf "$request" >&2
    # Return single line string
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
    local request="$1"; shift
    local file="$(Request_file "$request")";
    local response="HTTP/1.1 200 OK"
    if [[ -z "$file" ]]; then
        return 0
    fi
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
    echo -e "Listening for HTTP requests...                                             [\e[1;32mOK\e[0m]" >&2
    local request=""
    while read line; do
        request=$(Request_new "$line")
        Pipe_write "$PIPE" "$request\n"
    done
}

PrintIndex() {
    sed -e "s/REPOSITORY/$REPO/g" \
    -e "s/EMAIL/$(User_getEmail)/g" \
    -e "s/FULL_NAME/$(User_getFullName)/g" \
    -e "s/GITHUB_LOGIN/$(Host_getUsername github)/g" \
    -e "s/INSTRUCTOR_GITHUB/$INSTRUCTOR_GITHUB/g" \
    index.html > temp.html

    WebServer_sendFile "temp.html"
    rm temp.html
}


# Given a routing table and the request target, return name of function to call
Router_lookup() {
    # The routing table maps targets to bash functions
    local table="$1"; shift
    # However, keys can have special characters (e.g., / and *)
    # Therefore, we must first make them sed-friendly
    local key="$(echo $1 | sed -e 's/[/]/\\\//g' -e 's/[*]/[*]/g')"
    # Lookup the function
    local value="$(Request_lookup "$table" "$key")"
    if [[ -z "$value" ]]; then
        key="[*]"
        value="$(Request_lookup "$table" "$key")"
    fi
    printf "$value"
}

# Route requests to appropriate responses
WebServer_route() {
    local table="$1"; shift
    local request="$1"
    local target="$(Request_file "$request")"
    local function="$(Router_lookup "$table" "$target")"
    echo "ROUTING: $target using $function" >&2
    "$function" "$request"
#    if [[ "$target" == "/" ]]; then
#        PrintIndex
#    else
#        WebServer_sendFile "$target"
#    fi
}

# Respond to requests
WebServer_respond() {
    local routingTable="$1"
    local request=""
    Pipe_await "$PIPE"
    while sleep 1; do
        request="$(Pipe_read "$PIPE")"
        WebServer_route "$routingTable" "$request"
    done
}

# Start the web server, using routing table supplied through standard input
# The routing table maps targets to functions.
# It has the same syntax as HTTP headers: keys are targets, values are functions
# * is the catch-all (default) target if nothing else matches
WebServer_start() {
    # Read in routing table from standard in
    printf "Starting web server (please wait)..." >&2
    local routingTable=""
    while read line; do
        routingTable="$routingTable\n$line"
    done
    # Get netcat, if it's not already installed
    Acquire_software
    rm debug 2> /dev/null
    Pipe_new "$PIPE"
    echo -e "                                       [\e[1;32mOK\e[0m]" >&2
    echo -e "Opening web browser to http://localhost:8080                               [\e[1;32mOK\e[0m]" >&2
    
    Utility_fileOpen http://localhost:8080 > /dev/null
    WebServer_respond "$routingTable" | nc -k -l 8080 | WebServer_listen
}

WebServer_start <<EOF
/: PrintIndex
*: WebServer_sendFile
EOF


