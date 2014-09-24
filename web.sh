
WebServer_static() {
    local file="$1"; shift
    local type="$1"
    local header=""
    Acquire_software
    while true; do
        { echo -ne "HTTP/1.1 200 OK\r\nContent-Length: $(wc -c $file)\r\nContent-Type: $type\r\n\r\n"; cat $file; } | nc -l 8080 2>&1 | read header
        echo "the header is $header"
    done
}

start http://localhost:8080
WebServer_static ~/website/index.html "text/html"
