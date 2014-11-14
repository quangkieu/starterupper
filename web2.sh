#!/bin/bash

# Configuration
# ---------------------------------------------------------------------

# The repository to clone as upstream (NO SPACES)
readonly REPO=starterupper
# Default domain for school email
readonly SCHOOL=wit.edu
# The instructor's Github username
readonly INSTRUCTOR_GITHUB=lawrancej

# Runtime flags (DO NOT CHANGE)
# ---------------------------------------------------------------------
readonly PROGNAME=$(basename $0)
readonly ARGS="$@"

source web.sh
source github.sh

# Make the index page
app::make_index() {
    local githubLoggedIn=$(Utility_asTrueFalse $(Github_loggedIn))
    local githubEmailVerified=$(Utility_asTrueFalse $(Github_emailVerified "$email"))
    local githubUpgradedPlan=$(Utility_asTrueFalse $(Github_upgradedPlan))
    local githubEmailAdded=$(Utility_asTrueFalse $(Github_emailAdded "$email"))

    sed -e "s/REPOSITORY/$REPO/g" \
    -e "s/USER_EMAIL/$(User_getEmail)/g" \
    -e "s/FULL_NAME/$(User_getFullName)/g" \
    -e "s/GITHUB_LOGIN/$(Host_getUsername github)/g" \
    -e "s/INSTRUCTOR_GITHUB/$INSTRUCTOR_GITHUB/g" \
    -e "s/PUBLIC_KEY/$(SSH_getPublicKeyForSed)/g" \
    -e "s/HOSTNAME/$(hostname)/g" \
    -e "s/GITHUB_LOGGED_IN/$githubLoggedIn/g" \
    -e "s/GITHUB_UPGRADED_PLAN/$githubUpgradedPlan/g" \
    -e "s/GITHUB_EMAIL_ADDED/$githubEmailAdded/g" \
    -e "s/GITHUB_EMAIL_VERIFIED/$githubEmailVerified/g" \
    index.html > temp.html
}

app::index() {
    local request="$1"
    
    echo "$(request::payload "$request")" >&2
#    printf "$(request::query "$request")" >&2
    local email
    
    request::post_form_data "$request" | while read parameter; do
        local key="$(Parameter_key "$parameter")"
        local value="$(Parameter_value "$parameter")"
        case "$key" in
            "user.name" )
                User_setFullName "$value"
                ;;
            "user.email" )
                email="$value"
                User_setEmail "$value"
                Github_addEmail "$value"
                ;;
#            "github.login" )
#                Github
        esac
    done
    
    app::make_index
    server::send_file "temp.html"
    rm temp.html
}

# Return the browser to the browser so you can browse while you browse
app::browser() {
    local request="$1"
    local agent="$(request::lookup "$request" "User-Agent")"
    case "$agent" in
        *MSIE* | *Trident* ) # Internet explorer
            cat << 'EOF' > browser.css
.firefox { display: none; }
.chrome { display: none; }
EOF
            ;;
        *Firefox* )
            cat << 'EOF' > browser.css
.chrome { display: none; }
.msie { display: none; }
EOF
            ;;
        *Chrome* )
            cat << 'EOF' > browser.css
.firefox { display: none; }
.msie { display: none; }
EOF
            ;;
    esac
    server::send_file browser.css
    rm browser.css

}

app::router() {
    local request="$1"
    local target="$(request::file "$request")"
    case "$target" in
        "/" ) app::index "$request" ;;
        "browser.css" ) app::browser "$request" ;;
        * ) server::send_file "$target"
    esac
}

app::make_index
Utility_fileOpen temp.html > /dev/null
server::start "app::router"

# if [[ "$(Utility_fileOpen http://localhost:8080)" ]]; then
    # echo -e "Opened web browser to http://localhost:8080                                [\e[1;32mOK\e[0m]" >&2
# else
    # echo -e "Please open web browser to http://localhost:8080              [\e[1;32mACTION REQUIRED\e[0m]" >&2
# fi

