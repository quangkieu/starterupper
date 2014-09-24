#!/bin/bash

# Non-interactive Functions
# ---------------------------------------------------------------------

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

# Utilities
# ---------------------------------------------------------------------

# "return" failure
Utility_fail() {
    echo -n
    return 1
}

# "return" success
Utility_success() {
    printf true
    return 0
}

# Return whether the last command was successful
Utility_lastSuccess() {
    if [[ $? -eq 0 ]]; then
        Utility_success
    else
        Utility_fail
    fi
}

# Cross-platform paste to clipboard
# Return success if we pasted to the clipboard, fail otherwise
Utility_paste() {
    case $OSTYPE in
        msys | cygwin ) echo "$1" > /dev/clipboard; Utility_lastSuccess ;;
        linux* | bsd* ) echo "$1" | xclip -selection clipboard; Utility_lastSuccess ;;
        darwin* ) echo "$1" | pbcopy; Utility_lastSuccess ;;
        *) Utility_fail ;;
    esac
}

# Cross-platform file open
# Return success if we opened the file, fail otherwise
Utility_fileOpen() {
    case $OSTYPE in
        msys | cygwin ) start "$1"; Utility_lastSuccess ;;
        linux* | bsd* ) xdg-open "$1"; Utility_lastSuccess ;;
        darwin* ) open "$1"; Utility_lastSuccess ;;
        *) Utility_fail ;;
    esac
}

# Validate nonempty value matches a regex
# Return success if the value is not empty and matches regex, fail otherwise
Utility_nonEmptyValueMatchesRegex() {
    local value="$1"; shift
    local regex="$1"
    
    # First, check if value is empty
    if [[ -z "$value" ]]; then
        Utility_fail
    # Then, check whether value matches regex
    elif [[ -z "$(echo "$value" | grep -E "$regex" )" ]]; then
        Utility_fail
    else
        Utility_success
    fi
}

# SSH
# ---------------------------------------------------------------------

# Get the user's public key
SSH_getPublicKey() {
    # If the public/private keypair doesn't exist, make it.
    if ! [[ -f ~/.ssh/id_rsa.pub ]]; then
        # Use default location, set no phassphrase, no questions asked
        printf "\n" | ssh-keygen -t rsa -N '' 2> /dev/null > /dev/null
    fi
    cat ~/.ssh/id_rsa.pub
}

# Test connection
SSH_connected() {
    local hostDomain="$1"; shift
    local sshTest=$(ssh -oStrictHostKeyChecking=no git@$hostDomain 2>&1)
    if [[ 255 -eq $? ]]; then
        Utility_fail
    else
        Utility_success
    fi
}

# User functions
# ---------------------------------------------------------------------

# Get the user's username
User_getUsername() {
    local username="$USERNAME"
    if [[ -z "$username" ]]; then
        username="$(id -nu 2> /dev/null)"
    fi
    if [[ -z "$username" ]]; then
        username="$(whoami 2> /dev/null)"
    fi
    printf "$username"
}

# A full name needs a first and last name
Valid_fullName() {
    local fullName="$1"
    Utility_nonEmptyValueMatchesRegex "$fullName" "\w+ \w+"
}

# Get the user's full name (Firstname Lastname); defaults to OS-supplied full name
# Side effect: set ~/.gitconfig user.name if unset and full name from OS validates.
User_getFullName() {
    # First, look in the git configuration
    local fullName="$(git config user.name)"
    
    # Ask the OS for the user's full name, if it's not valid
    if [[ ! $(Valid_fullName "$fullName") ]]; then
        local username="$(User_getUsername)"
        case $OSTYPE in
            msys | cygwin )
                cat << 'EOF' > getfullname.ps1
$MethodDefinition = @'
[DllImport("secur32.dll", CharSet=CharSet.Auto, SetLastError=true)]
public static extern int GetUserNameEx (int nameFormat, System.Text.StringBuilder userName, ref uint userNameSize);
'@
$windows = Add-Type -MemberDefinition $MethodDefinition -Name 'Secur32' -Namespace 'Win32' -PassThru
$sb = New-Object System.Text.StringBuilder
$num=[uint32]256
$windows::GetUserNameEx(3, $sb, [ref]$num) | out-null
$sb.ToString()
EOF
                fullName=$(powershell -executionpolicy remotesigned -File getfullname.ps1 | sed -e 's/\(.*\), \(.*\)/\2 \1/')
                rm getfullname.ps1 > /dev/null
                ;;
            linux* )
                fullName=$(getent passwd "$username" | cut -d ':' -f 5 | cut -d ',' -f 1)
                ;;
            darwin* )
                fullName=$(dscl . read /Users/`whoami` RealName | grep -v RealName | cut -c 2-)
                ;;
            *) fullName="" ;;
        esac
        
        # If we got a legit full name from the OS, update the git configuration to reflect it.
        if [[ $(Valid_fullName "$fullName") ]]; then
            git config --global user.name "$fullName"
        fi
    fi
    printf "$fullName"
}

# We're assuming that students have a .edu email address
Valid_email() {
    local email="$(printf "$1" | tr '[:upper:]' '[:lower:]' | tr -d ' ')"
    Utility_nonEmptyValueMatchesRegex "$email" "edu$"
}

# Get the user's email; defaults to username@school
# Side effect: set ~/.gitconfig user.email if unset
User_getEmail() {
    # Try to see if the user already stored the email address
    local email="$(git config user.email | tr '[:upper:]' '[:lower:]' | tr -d ' ')"
    # If the stored email is bogus, ...
    if [[ ! $(Valid_email "$email") ]]; then
        # Guess an email address and save it
        email="$(User_getUsername)@$SCHOOL"
    fi
    # Resave, just in case of goofups
    git config --global user.email "$email"
    printf "$email"
}

# Get the domain name out of the user's email address
User_getEmailDomain() {
    printf "$(User_getEmail)" | sed 's/.*[@]//'
}

# Is the school valid?
Valid_school() {
    local school="$1"
    Utility_nonEmptyValueMatchesRegex "$school" "\w+"
}

# Get the user's school from their email address
User_getSchool() {
    local school="$(git config user.school)"
    Acquire_software
    if [[ ! "$(Utility_nonEmptyValueMatchesRegex "$school" "\w+")" ]]; then
        school="$(echo -e "$(User_getEmailDomain)\r\n" | nc whois.educause.edu 43 | sed -n -e '/Registrant:/,/   .*/p' | sed -n -e '2,2p' | sed 's/^[ ]*//')"
    fi
    printf "$school"
}

# Generic project host configuration functions
# ---------------------------------------------------------------------

# Get the project host username; defaults to machine username
Host_getUsername() {
    local host="$1"
    local username="$(git config $host.login)"
    if [[ -z "$username" ]]; then
        username="$(User_getUsername)"
    fi
    printf "$username"
}

# Git
# ---------------------------------------------------------------------

# Clone repository and configure remotes
Git_configureRepository() {
    local hostDomain="$1"
    local originLogin="$2"
    local upstreamLogin="$3"
    local origin="git@$hostDomain:$originLogin/$REPO.git"
    local upstream="https://$hostDomain/$upstreamLogin/$REPO.git"
    
    # It'll go into the user's home directory
    cd ~
    if [ ! -d $REPO ]; then
        git clone "$upstream"
    fi

    # Configure remotes
    cd ~/$REPO
    git remote rm origin 2> /dev/null
    git remote rm upstream 2> /dev/null
    git remote add origin "$origin"
    git remote add upstream "$upstream"
    git config branch.master.remote origin
    git config branch.master.merge refs/heads/master
}

# Show the local and remote repositories
Git_showRepositories() {
    local remote="$(git remote -v | grep origin | sed -e 's/.*git@\(.*\):\(.*\)\/\(.*\)\.git.*/https:\/\/\1\/\2\/\3/' | head -n 1)"
    cd ~
    # Open local repository in file browser
    Utility_fileOpen $REPO
    # Open remote repository in web browser
    Utility_fileOpen "$remote"
}

# Push repository, and show the user local/remote repositories
# Preconditions:
# 1. SSH public/private keypair was generated
# 2. The project host username was properly set
# 3. SSH public key was shared with host
# 4. SSH is working
# 5. The private repo exists
Git_pushRepo() {
    cd ~/$REPO
    git fetch --all
    git push -u origin master # 2> /dev/null
}
