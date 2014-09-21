github_share_key() {
    echo "Share your public key with Github."
    echo
    echo "1. Click Add SSH Key"
    echo "2. Title: machine nickname"
    echo "3. Key: Paste in your public key (Ctrl-V or right-click and paste)"
    echo
    sleep 3
    file_open "https://github.com/settings/ssh"
}