Acquire_software() {
    # If we don't have netcat, get it
    if [[ -z "$(which nc)" ]]; then
        curl http://nmap.org/dist/ncat-portable-5.59BETA1.zip 2> /dev/null > ncat.zip
        unzip -p ncat.zip ncat-portable-5.59BETA1/ncat.exe > nc.exe
        rm ncat.zip
    fi
    # If we don't have mkfifo, get it (along with its dependencies)
    if [[ -z "$(which mkfifo)" ]]; then
        curl -L http://gnuwin32.sourceforge.net/downlinks/coreutils-bin-zip.php 2> /dev/null > coreutils.zip
        unzip -p coreutils.zip bin/mkfifo.exe > mkfifo.exe
        curl -L http://gnuwin32.sourceforge.net/downlinks/coreutils-dep-zip.php 2> /dev/null > coreutils-dep.zip
        unzip -p coreutils-dep.zip bin/libintl3.dll > libintl3.dll
        unzip -p coreutils-dep.zip bin/libiconv2.dll > libiconv2.dll
        rm coreutils.zip
        rm coreutils-dep.zip
    fi
}

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
