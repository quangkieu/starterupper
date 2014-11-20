#!/bin/bash

source utility.sh

readonly PIPE=.httpipe

# http://mywiki.wooledge.org/NamedPipes
# Also, simultaneous connections

json::unpack() {
    local json="$1"
    echo "$json" | tr -d '"{}' | tr ',' '\n'
}

# Given a header key, return the value
json::lookup() {
    local json="$1"; shift
    local key="$1"
    echo -e "$json" | grep "$key" | sed -e "s/^$key:\(.*\)$/\1/"
}


# Is this a request line?
request::line() {
    local line="$1"
    if [[ -z "$(echo "$line" | grep -E "^GET|^HEAD|^POST|^PUT|^DELETE|^CONNECT|^OPTIONS|^TRACE")" ]]; then
        utility::fail
    fi
    utility::success
}

# Get the method (e.g., GET, POST) of the request
request::method() {
    local request="$1"
    echo "$request" | sed -e "s/\(^[^ ]*\).*/\1/" | head -n 1
}

# Get the target (URL) of the request
request::target() {
    local request="$1"
    echo "$request" | sed -e 's/^[^ ]* \(.*\) HTTP\/.*/\1/' | head -n 1
}

# Get the file from the request target URL
request::file() {
    local request="$1"
    local target="$(request::target "$request")"
    # Leave the root request alone
    if [[ "$target" == "/" ]]; then
        printf "/"
    # Remove attempts to look outside the current folder, strip off the leading slash and the query
    else
        printf "$target" | sed -e 's/[.][.]//g' -e 's/^[/]*//g' -e 's/[?].*$//'
    fi
}

# Get the query portion of the request target URL, and return the results line by line
request::query() {
    request::target "$1" | sed -e 's/.*[?]\(.*\)$/\1/' | tr '&' '\n'
}

# Parse the request payload as form-urlencoded data
request::post_form_data() {
    local request="$1"
    local payload="$(request::payload "$request")"
    echo -e "REQUEST $request" >&2
    if [[ "$(request::lookup "$request" "Content-Type")" == "application/x-www-form-urlencoded" ]]; then
        echo "$payload" | tr '&' '\n'
    fi
}

# Given a query key, return the URL decoded value
Query_lookup() {
    local query="$1"; shift
    local key="$1"
    echo -e "$(printf "$query" | grep "$key" | sed -e "s/^$key=\(.*\)/\1/" -e 'y/+/ /; s/%/\\x/g')"
}

# Return the key corresponding to the field
Parameter_key() {
    local parameter="$1"
    echo "$parameter" | cut -d '=' -f 1
}

# Return the URL decoded value corresponding to the field
Parameter_value() {
    local parameter="$1"
    echo -e "$(echo "$parameter" | cut -d '=' -f 2 | sed 'y/+/ /; s/%/\\x/g')"
}

# Given a header key, return the value
request::lookup() {
    local request="$1"; shift
    local key="$1"
    echo -e "$request" | grep "$key" | sed -e "s/^$key: \(.*\)/\1/"
}

# Return the payload of the request, if any (e.g., for POST requests)
request::payload() {
    local request="$1"; shift
    echo -e "$request" | sed -n -e '/^$/,${p}'
}

# Pipe HTTP request into a string
request::new() {
    local line="$1"
    # If we got a request, ...
    if [[ $(request::line "$line") ]]; then
        local request="$line"
        # Read all headers
        while read -r header; do
            request="$request\n$header"
            if [[ -z "$header" ]]; then
                break
            fi
        done
        # Sometimes, we have a payload in the request, so handle that, too...
        local length="$(request::lookup "$request" "Content-Length")"
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
response::send() {
    local response="$1"; shift
    local length="$1"; shift
    local type="$1";

    echo -ne "$response\r\nDate: $(date '+%a, %d %b %Y %T %Z')\r\nServer: Starter Upper\r\nContent-Length: $length\r\nContent-Encoding: binary\r\nContent-Type: $type\r\n\r\n"
}

# Send file with HTTP response headers
server::send_file() {
    local file="$1"; shift
    local response="HTTP/1.1 200 OK"
    if [[ -z "$file" ]]; then
        return 0
    fi
    if [[ ! -f "$file" ]]; then
        response="HTTP/1.1 404 Not Found"
        file="404.html"
    fi
    local type="$(utility::MIMEType $file)"
    response::send "$response" "$(utility::fileSize "$file")" "$type"
    cat "$file"
    echo "SENT $file" >&2
}

# Listen for requests
server::listen() {
    local request=""
    while read -r line; do
        request=$(request::new "$line")
        # Send the request through 
        pipe::write "$PIPE" "$request\n"
    done
}

# Respond to requests, using supplied route function
# The route function is a command that takes a request argument: it should send a response
server::respond() {
    local routeFunction="$1"
    local request=""
    pipe::await "$PIPE"
    while true; do
        request="$(pipe::read "$PIPE")"
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

server::works() {
    local nc=$(Acquire_netcat)
    "$nc" -l 8080 &
    sleep 10
    echo "works" > /dev/tcp/localhost/8080
}

# Start the web server, using the supplied routing function
server::start() {
    local routes="$1"
    pipe::new "$PIPE"
    local nc=$(Acquire_netcat)
    
    server::respond "$routes" | "$nc" -k -l 8080 | server::listen
}

