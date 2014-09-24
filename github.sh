#!/bin/bash

# Github non-interactive functions
# ---------------------------------------------------------------------

# Is the name available on Github?
Github_nameAvailable() {
    local username="$1"
    local result="$(curl -i https://api.github.com/users/$username 2> /dev/null)"
    if [[ -z $(echo $result | grep "HTTP/1.1 200 OK") ]]; then
        Utility_success
    else
        Utility_fail
    fi
}

# A valid Github username is not available, by definition
Github_validUsername() {
    local username="$1"
    # If the name is legit, ...
    if [[ $(Utility_nonEmptyValueMatchesRegex "$username" "^[0-9a-zA-Z][0-9a-zA-Z-]*$") ]]; then
        # And unavailable, ...
        if [[ ! $(Github_nameAvailable $username) ]]; then
            # It's valid
            Utility_success
        fi
    fi
    # Otherwise, it's not valid
    Utility_fail
}

# Github CLI-interactive functions
# ---------------------------------------------------------------------

# Ask user to verify email
Github_verifyEmail() {
    echo "Press enter to open https://github.com/settings/emails to add your school email address."
    echo "Open your email inbox and wait a minute for an email from Github."
    echo "Follow its instructions: click the link in the email and click Confirm."
    echo "$(Interactive_paste $(User_getEmail) "your school email")"
    Interactive_fileOpen "https://github.com/settings/emails"
}

# Ask the user to get the discount
Github_getDiscount() {
    echo "Press enter to open https://education.github.com/discount_requests/new to request an individual student educational discount from Github."
    Interactive_fileOpen "https://education.github.com/discount_requests/new"
}

# Ask the user if they have an account yet, and guide them through onboarding
Github_join() {
    local hasAccount="n"
    
    # If we don't have their github login, ...
    if [[ -z "$(git config --global github.login)" ]]; then
        # Ask if they're on github yet
        read -p "Do you have a Github account (yes or No [default])? " hasAccount < /dev/tty

        echo -e "\e[1;37;41mIMPORTANT\e[0m: Before we proceed, you need to complete ALL of these steps:"

        # Let's assume that they don't by default
        if [[ $hasAccount != [Yy]* ]]; then
            echo "1. Join Github, using $(User_getEmail) as your email address."
            echo "2. Open your email inbox and verify your school email address."
            echo "3. Request an individual student educational discount."
            echo ""
            
            echo "Press enter to join Github."
            Interactive_fileOpen "https://github.com/join"
        else
            echo "1. Share and verify your school email address with Github."
            echo "2. Request an individual student educational discount."
            echo ""
        fi
        
        Github_verifyEmail
        Github_getDiscount

    fi
}

# Set the Github username, if not already set
Github_setUsername() {
    Github_join
    if [[ -z "$(git config --global github.token)" ]]; then
        Interactive_setValue "github.login" "$(Host_getUsername "github")" "Github_validUsername" "Github username" "\nNOTE: Usernames are case-sensitive. See: https://github.com"
    fi
}

Github_authorize() {
    local password="$1"; shift
    local code="$1"
    read -r -d '' json <<-EOF
            {
                "scopes": ["repo", "public_repo", "user", "write:public_key", "user:email"],
                "note": "starterupper $(date --iso-8601=seconds)"
            }
EOF
    curl -i -u $(Host_getUsername "github"):$password -H "X-GitHub-OTP: $code" -d "$json" https://api.github.com/authorizations 2> /dev/null
}

# Idea: refactor to use interactive_setValue instead.
# Acquire authentication token and store in github.token
Github_authenticate() {
    # Don't bother if we already got the authentication token
    if [[ -n "$(git config --global github.token)" ]]; then
        return 0
    fi
    local token="HTTP/1.1 401 Unauthorized"
    local code=''
    local password=''
    # As long as we're unauthorized, ...
    while [[ ! -z "$(echo $token | grep "HTTP/1.1 401 Unauthorized" )" ]]; do
        # Ask for a password
        if [[ -z "$password" ]]; then
            read -s -p "Enter Github password (not shown or saved): " password < /dev/tty
            echo # We need this, otherwise it'll look bad
        fi
        # Generate authentication token request
        token=$(Github_authorize "$password" "$code")
        # If we got a bad credential, we need to reset the password and try again
        if [[ ! -z $(echo $token | grep "Bad credential") ]]; then
            echo -e "\e[1;37;41mERROR\e[0m: Incorrect password for user $(Host_getUsername "github"). Please wait."
            password=''
            sleep 1
        fi
        # If the user has two-factor authentication, ask for it.
        if [[ ! -z $(echo $token | grep "two-factor" ) ]]; then
            read -p "Enter Github two-factor authentication code: " code < /dev/tty
        fi
    done
    # By now, we're authenticated, ...
    if [[ ! -z $(echo $token | grep "HTTP/... 20." ) ]]; then
        # So, extract the token and store it in github.token
        token=$(echo $token | tr '"' '\n' | grep -E '[0-9a-f]{40}')
        git config --global github.token "$token"
        echo "Authenticated!"
    # Or something really bad happened, in which case, github.token will remain unset...
    else
        # When bad things happen, degrade gracefully.
        echo -n -e "\e[1;37;41mERROR\e[0m: "
        echo "$token" | grep "HTTP/..."
        echo
        echo "I encountered a problem and need your help to finish these setup steps:"
        echo
        echo "1. Update your Github profile to include your full name."
        echo "2. Create private repository $REPO on Github."
        echo "3. Add $INSTRUCTOR_GITHUB as a collaborator."
        echo "4. Share your public SSH key with Github."
        echo "5. Push to your private repository."
        echo
    fi
}

# Invoke a Github API method requiring authorization using curl
Github_invoke() {
    local method=$1; shift
    local url=$1; shift
    local data=$1;
    local header="Authorization: token $(git config --global github.token)"
    curl -i --request "$method" -H "$header" -d "$data" "https://api.github.com$url" 2> /dev/null
}

# Share full name with Github
Github_setFullName() {
    local fullName="$(User_getFullName)"
    # If authentication failed, degrade gracefully
    if [[ -z $(git config --global github.token) ]]; then
        echo "Press enter to open https://github.com/settings/profile to update your Github profile."
        echo "On that page, enter your full name. $(Interactive_paste "$fullName" "your full name")"
        echo "Then, click Update profile."
        Interactive_fileOpen "https://github.com/settings/profile"
    # Otherwise, use the API
    else
        echo "Updating Github profile information..."
        Github_invoke PATCH "/user" "{\"name\": \"$fullName\"}" > /dev/null
    fi
}

# Share the public key
Github_sharePublicKey() {
    local githubLogin="$(Host_getUsername "github")"
    # If authentication failed, degrade gracefully
    if [[ -z "$(git config --global github.token)" ]]; then
        echo "Press enter to open https://github.com/settings/ssh to share your public SSH key with Github."
        echo "On that page, click Add SSH Key, then enter these details:"
        echo "Title: $(hostname)"
        echo "Key: $(Interactive_paste "$(SSH_getPublicKey)" "your public SSH key")"
        Interactive_fileOpen "https://github.com/settings/ssh"
    # Otherwise, use the API
    else
        # Check if public key is shared
        local publickeyShared=$(curl -i https://api.github.com/users/$githubLogin/keys 2> /dev/null)
        # If not shared, share it
        if [[ -z $(echo "$publickeyShared" | grep $(SSH_getPublicKey | sed -e 's/ssh-rsa \(.*\)=.*/\1/')) ]]; then
            echo "Sharing public key..."
            Github_invoke POST "/user/keys" "{\"title\": \"$(hostname)\", \"key\": \"$(SSH_getPublicKey)\"}" > /dev/null
        fi
    fi
    # Test SSH connection on default port (22)
    if [[ ! $(SSH_connected "github.com") ]]; then
        echo "Your network has blocked port 22; trying port 443..."
        printf "Host github.com\n  Hostname ssh.github.com\n  Port 443\n" >> ~/.ssh/config
        # Test SSH connection on port 443
        if [[ ! $(SSH_connected "github.com") ]]; then
            echo "WARNING: Your network has blocked SSH."
            ssh_works=false
        fi
    fi
}

# Create a private repository manually
Github_manualCreatePrivateRepo() {
    echo "Press enter to open https://github.com/new to create private repository $REPO on Github."
    echo "On that page, for Repository name, enter: $REPO. $(Interactive_paste "$REPO" "the repository name")"
    echo "Then, select Private and click Create Repository (DON'T tinker with other settings)."
    Interactive_fileOpen "https://github.com/new"
}

# Create a private repository on Github
Github_createPrivateRepo() {
    # If authentication failed, degrade gracefully
    if [[ -z "$(git config --global github.token)" ]]; then
        Github_manualCreatePrivateRepo
        return 0
    fi
    
    local githubLogin="$(Host_getUsername "github")"
    # Don't create a private repo if it already exists
    if [[ -z $(Github_invoke GET "/repos/$githubLogin/$REPO" "" | grep "Not Found") ]]; then
        return 0
    fi
    
    echo "Creating private repository $githubLogin/$REPO on Github..."
    local result="$(Github_invoke POST "/user/repos" "{\"name\": \"$REPO\", \"private\": true}")"
    if [[ ! -z $(echo $result | grep "HTTP/... 4.." ) ]]; then
        echo -n -e "\e[1;37;41mERROR\e[0m: "
        echo "Unable to create private repository."
        echo
        echo "Troubleshooting:"
        echo "* Make sure you have verified your school email address."
        echo "* Apply for the individual student educational discount if you haven't already done so."
        echo "* If you were already a Github user, free up some private repositories."
        echo
        
        Github_verifyEmail
        Github_getDiscount
        Github_manualCreatePrivateRepo
    fi
}

# Add a collaborator
Github_addCollaborator() {
    local githubLogin="$(Host_getUsername "github")"
    # If authentication failed, degrade gracefully
    if [[ -z "$(git config --global github.token)" ]]; then
        echo "Press enter to open https://github.com/$githubLogin/$REPO/settings/collaboration to add $1 as a collaborator."
        echo "$(Interactive_paste "$1" "$1")"
        echo "Click Add collaborator."
        Interactive_fileOpen "https://github.com/$githubLogin/$REPO/settings/collaboration"
    # Otherwise, use the API
    else
        echo "Adding $1 as a collaborator..."
        Github_invoke PUT "/repos/$githubLogin/$REPO/collaborators/$1" "" > /dev/null
    fi
}

# Clean up everything but the repo (BEWARE!)
Github_clean() {
    echo "Delete starterupper-script under Personal access tokens"
    Interactive_fileOpen "https://github.com/settings/applications"
    sed -i s/.*github.com.*// ~/.ssh/known_hosts
    git config --global --unset user.name
    git config --global --unset user.email
    git config --global --unset github.login
    git config --global --unset github.token
    rm -f ~/.ssh/id_rsa*
}

# Add collaborators
Github_addCollaborators() {
    cd ~/$REPO
    for repository in $(Github_invoke GET "/user/repos?type=member\&sort=created\&page=1\&per_page=100" "" | grep "full_name.*$REPO" | sed s/.*full_name....// | sed s/..$//); do
        git remote add ${repository%/*} git@github.com:$repository.git 2> /dev/null
    done
    git fetch --all
}

# Setup a verified .edu email on github
github_configure_email() {
    # Check if email is validated via api
    local emails="$(Github_invoke GET "/user/emails" "" | tr '\n}[]{' ' \n   ')"
    local email="$(User_getEmail)"

    # Add email to github via api, if not set (FIXME: doesn't work)
    if [[ -z $(echo "$emails" | grep "$email") ]]; then
        Github_invoke POST "/user/emails" "\"$email\"" > /dev/null
    fi
    # Ask user to validate email if not validated
    if [[ -z $(echo "$emails" | grep "verified...true") ]]; then
        echo "IMPORTANT: Open your inbox and verify $email with Github."
        sleep 3
        Interactive_fileOpen "https://github.com/settings/emails"
    else
        return 0
    fi
    # Nag the user until they get it right
    while [[ -z $(Github_invoke GET "/user/emails" "" | tr '\n}[]{' ' \n   ' | grep "verified...true") ]]; do
        read -p "ERROR: $email is not verified yet. Verify, then press enter to continue." < /dev/tty
    done
}

