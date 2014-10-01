#!/bin/bash
source utility.sh

WebServer_MIMEType() {
    local fileName="$1";
    case $fileName in
        *.html | *.htm ) printf "text/html" ;;
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
                if [[ -z "$header" ]]; then
                    break
                fi
            done
            echo "$url" >.request
        fi
    done
}

WebServer_makeResponse() {
    sleep 5
    while read file < .request; do
        local type="$(WebServer_MIMEType $file)"
        if [[ -f "$file" ]]; then
            echo -ne "HTTP/1.0 200 OK\r\nContent-Length: $(wc -c $file | sed -r -e 's/ +([0-9]+) .*/\1/')\r\nContent-Encoding: binary\r\nContent-Type: $type\r\n\r\n"
            cat "$file"
            sleep 5
#            cat "$file"
#            local response="HTTP/1.1 200 OK
#Date: $(date '+%a, %d %b %Y %T %Z')
#Cache-Control: private
#Server: Starter Upper 2014-09-29
#Content-Type: $type
#Connection: close
#Content-Length: ${#fileData}
#Content-Encoding: binary
#
#$fileData"
#            cat << EOF
#$response
#EOF
        else
            local response="HTTP/1.1 404 Not Found
Content-Type: text/html
Connection: close

<h1>404 Not Found</h1>
The requested resource was not found"
            cat << EOF
$response
EOF
        fi
    done
}

WebServer() {
    Acquire_software
    rm -f .request
    rm debug
#    touch httpipe
    mknod .request p 2> /dev/null
    cat > .request <<EOF
EOF
#    WebServer_makeResponse index.html
#    while true; do
    WebServer_makeResponse | nc -o debug -k -lvv 8080 | WebServer_handleRequest
#    done
#    while true; do
#        header=$({ echo -ne "HTTP/1.0 200 OK\r\nContent-Length: $(wc -c $file)\r\nContent-Encoding: binary\r\nContent-Type: $type\r\n\r\n"; cat $file; } | nc -l 8080 2>&1)
#        echo "the header is $header"
#    done
}

start http://localhost:8080
WebServer index.html "text/html"