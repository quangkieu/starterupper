#!/bin/bash

source utility.sh

readonly PIPE=.httpipe

# http://mywiki.wooledge.org/NamedPipes
# Also, simultaneous connections

# Is this a request line?
Request_line() {
    local line="$1"
    if [[ -z "$(echo "$line" | grep -E "^GET|^HEAD|^POST|^PUT|^DELETE|^CONNECT|^OPTIONS|^TRACE")" ]]; then
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
}

# Get the query portion of the request target URL, and return the results line by line
Request_query() {
    Request_target "$1" | sed -e 's/.*[?]\(.*\)$/\1/' | tr '&' '\n'
}

Request_postFormData() {
    local request="$1"
    local payload="$(Request_payload "$request")"
    echo -e "REQUEST $request" >&2
    if [[ "$(Request_lookup "$request" "Content-Type")" == "application/x-www-form-urlencoded" ]]; then
        echo "$payload" | tr '&' '\n'
    fi
}

# Given a query key, return the URL decoded value
Query_lookup() {
    local query="$1"; shift
    local key="$1"
    echo -e "$(printf "$query" | grep "$key" | sed -e "s/^$key=\(.*\)/\1/" -e 'y/+/ /; s/%/\\x/g')"
}

# Returh the key corresponding to the field
Parameter_key() {
    local parameter="$1"
    echo "$parameter" | cut -d '=' -f 1
}

# Return the URL decoded value corresponding to the field
Parameter_value() {
    local parameter="$1"
    echo -e "$(echo "$parameter" | cut -d '=' -f 2 | sed 'y/+/ /; s/%/\\x/g')"
}

# Get the file from the request target URL
Request_file() {
    local request="$1"
    local target="$(Request_target "$request")"
    # Leave the root request alone
    if [[ "$target" == "/" ]]; then
        printf "/"
    elif [[ "$target" == /?* ]]; then
        printf "/"
    # Remove attempts to look outside the current folder, strip off the leading slash and the query
    else
        echo "$target" | sed -e 's/[.][.]//g' -e 's/^[/]*//g' -e 's/[?].*$//'
    fi
}

# Given a header key, return the value
Request_lookup() {
    local request="$1"; shift
    local key="$1"
    echo -e "$request" | grep "$key" | sed -e "s/^$key: \(.*\)/\1/"
}

# Return the payload of the request, if any (e.g., for POST requests)
Request_payload() {
    local request="$1"; shift
    echo -e "$request" | sed -n -e '/^$/,${p}'
}

# Pipe HTTP request into a string
Request_new() {
    local line="$1"
    # If we got a request, ...
    if [[ $(Request_line "$line") ]]; then
        local request="$line"
        # Read all headers
        while read -r header; do
            request="$request\n$header"
            if [[ -z "$header" ]]; then
                break
            fi
        done
        # Sometimes, we have a payload in the request, so handle that, too...
        local length="$(Request_lookup "$request" "Content-Length")"
        local payload=""
        if [[ -n "$length" ]] && [[ "$length" != "0" ]]; then
            read -r -n "$length" payload
            request="$request\n$payload"
        fi
    fi
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
    Response_send "$response" "$(Utility_fileSize "$file")" "$type"
    cat "$file"
    echo "SENT $file" >&2
}

# Listen for requests
WebServer_listen() {
    local request=""
    while read -r line; do
        request=$(Request_new "$line")
        # Send the request through 
        Pipe_write "$PIPE" "$request\n"
    done
}

# Respond to requests, using supplied route function
# The route function is a command that takes a request argument: it should send a response
WebServer_respond() {
    local routeFunction="$1"
    local request=""
    Pipe_await "$PIPE"
    while true; do
        request="$(Pipe_read "$PIPE")"
        # Pass the request to the route function
        "$routeFunction" "$request"
    done
}

Acquire_netcat() {
    local netcat=""
    # Look for netcat
    for program in "nc" "ncat" "netcat"; do
        if [[ -n "$(which $program)" ]]; then
            netcat=$program
            break
        fi
    done
    # Get netcat, if it's not already installed
    if [[ -z "$netcat" ]]; then
        curl http://nmap.org/dist/ncat-portable-5.59BETA1.zip 2> /dev/null > ncat.zip
        unzip -p ncat.zip ncat-portable-5.59BETA1/ncat.exe > nc.exe
        netcat="nc"
        rm ncat.zip
    fi
    printf $netcat
}

# Start the web server, using the supplied routing function
WebServer_start() {
    local routes="$1"
    Pipe_new "$PIPE"
    local nc=$(Acquire_netcat)
    
    WebServer_respond "$routes" | "$nc" -k -l 8080 | WebServer_listen
}
