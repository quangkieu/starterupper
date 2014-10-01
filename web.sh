#!/bin/bash
source utility.sh

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

# Get the MIME type from a file
WebServer_MIMEType() {
    local fileName="$1";
    case $fileName in
        *.html | *.htm ) printf "text/html" ;;
        *.ico ) printf "image/x-icon" ;;
        *.css ) printf "text/css" ;;
        *.js ) printf "text/javascript" ;;
        *.txt ) printf "text/plain" ;;
        *.jpg ) printf "image/jpeg" ;;
        *.png ) printf "image/png" ;;
        *.svg ) printf "image/svg+xml" ;;
        *.pdf ) printf "application/pdf" ;;
        * ) printf "application/octet-stream" ;;
    esac
}

# Parse the request
WebServer_handleRequest() {
    while read line; do
        if [[ $line == GET* ]]; then
            echo "$line" >&2
            local url="${line#GET /}"
            url="${url% HTTP/*}"
            if [[ -z "$url" ]]; then
                url="index.html"
            fi
            while read header; do
                echo "$header" >&2
                if [[ -z "$header" ]]; then
                    echo "GOT REQUEST $url" >&2
                    Utility_pipeWrite .request "$url\n"
                    break
                fi
            done
        fi
    done
}

WebServer_sendResponse() {
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
    local type="$(WebServer_MIMEType $file)"
    echo "SENDING $file" >&2
    echo -ne "$response\r\nContent-Length: $(Utility_fileSize "$file")\r\nContent-Encoding: binary\r\nContent-Type: $type\r\n\r\n"
    cat "$file"
    printf "\n"
    echo "FINISHED $file" >&2
}

WebServer_makeResponse() {
    local file=""
    Utility_waitForPipe .request
    while sleep 1; do
        file="$(Utility_pipeRead .request)"
        echo "FILE $file" >&2
        WebServer_sendResponse "$file"
    done
}

WebServer_start() {
    Acquire_software
    rm debug 2> /dev/null
    Utility_makePipe .request
    WebServer_makeResponse | nc -o debug -k -lvv 8080 | WebServer_handleRequest
}

# PrintIndex
Utility_fileOpen http://localhost:8080
WebServer_start index.html "text/html"