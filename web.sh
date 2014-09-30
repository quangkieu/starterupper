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

WebServer_makeResponse() {
    local file="$1"; shift
    if [[ -z "$file" ]]; then
        file="index.html"
    fi
    local type="$(WebServer_MIMEType $file)"
    if [[ -f "$file" ]]; then
        local fileData=$(<"$file")
        local response="HTTP/1.1 200 OK
Date: $(date '+%a, %d %b %Y %T %Z')
Cache-Control: private
Server: Starter Upper 2014-09-29
Content-Type: $type
Content-Length: ${#fileData}
Content-Encoding: binary

$fileData"
        cat >>httpipe << EOF
$response
EOF
    else
        local response="HTTP/1.1 404 Not Found
Content-Type: text/html

<h1>404 Not Found</h1>
The requested resource was not found"
        cat >>httpipe << EOF
$response
EOF
    fi
}

WebServer() {
    Acquire_software
    rm -f httpipe
#    touch httpipe
    mknod httpipe p 2> /dev/null
#    mkfifo httpipe
    cat >httpipe <<EOF
EOF
#    WebServer_makeResponse index.html
#    while true; do
    (tail -c 1048576 -f httpipe) | nc -k -lv 8080 | (
        while read line; do
            if [[ $line == GET* ]]; then
                echo "$line"
                local url="${line#GET /}"
                url="${url% HTTP/*}"
                while read header; do
                    echo "$header"
                    if [[ -z "$header" ]]; then
                        break
                    fi
                done
                WebServer_makeResponse "$url"
            fi
        done
    )
#    done
#    while true; do
#        header=$({ echo -ne "HTTP/1.0 200 OK\r\nContent-Length: $(wc -c $file)\r\nContent-Encoding: binary\r\nContent-Type: $type\r\n\r\n"; cat $file; } | nc -l 8080 2>&1)
#        echo "the header is $header"
#    done
}

start http://localhost:8080
WebServer index.html "text/html"
# WebServer_MIMEType index.html