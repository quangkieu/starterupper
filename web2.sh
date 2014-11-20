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
    local githubLoggedIn=$(utility::asTrueFalse $(github::loggedIn))
    local githubEmailVerified=$(utility::asTrueFalse $(github::emailVerified "$email"))
    local githubUpgradedPlan=$(utility::asTrueFalse $(github::upgradedPlan))
    local githubEmailAdded=$(utility::asTrueFalse $(github::emailAdded "$email"))

    sed -e "s/REPOSITORY/$REPO/g" \
    -e "s/USER_EMAIL/$(user::getEmail)/g" \
    -e "s/FULL_NAME/$(user::getFullName)/g" \
    -e "s/GITHUB_LOGIN/$(Host_getUsername github)/g" \
    -e "s/INSTRUCTOR_GITHUB/$INSTRUCTOR_GITHUB/g" \
    -e "s/PUBLIC_KEY/$(ssh::getPublicKeyForSed)/g" \
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
                user::setFullName "$value"
                ;;
            "user.email" )
                email="$value"
                user::setEmail "$value"
                github::addEmail "$value"
                ;;
#            "github.login" )
#                Github
        esac
    done
    
    app::make_index
    server::send_file "temp.html"
    rm temp.html
}

# Return the browser to the browser for disabled JavaScript troubleshooting
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

# Setup local repositories
app::setup() {
    local request="$1"
    case "$(request::method "$request")" in
        # Respond to preflight request
        "OPTIONS" )
            # response should be a thing we build up, with the status, headers, and the send-off
            # NOT something we do manually
            echo -n -e "HTTP/1.1 204 No Content\r\nDate: $(date '+%a, %d %b %Y %T %Z')\r\nAccess-Control-Allow-Origin: *\r\nAccess-Control-Allow-Methods: GET, POST\r\nAccess-Control-Allow-Headers: $(request::lookup "$request" "Access-Control-Request-Headers")\r\nServer: Starter Upper\r\n\r\n"
            echo "SENT RESPONSE" >&2
            ;;
        # Get that glorious data from the user and do what we set out to accomplish
        "POST" )
            echo "hi" >&2
            local data="$(json::unpack "$(request::payload "$request")")"
            local github_login="$(json::lookup "$data" "github.login")"
            local user_name="$(json::lookup "$data" "user.name")"
            local user_email="$(json::lookup "$data" "user.email")"
            # Git configuration
            user::setEmail "$user_email"
            user::setFullName "$user_name"
            # Github configuration
            github::set_login "$github_login"
            
            # The response needs to set variables: git-config, git-clone, git-push
            ;;
        # If we get here, something terribly wrong has happened...
        * )
            echo "the request was '$request'" >&2
            echo "$(request::method "$request")" >&2
            ;;
    esac
}

# Handle requests from the browser
app::router() {
    local request="$1"
    local target="$(request::file "$request")"
    case "$target" in
        "/" )           app::index "$request" ;;
        "browser.css" ) app::browser "$request" ;;
        "setup" )       app::setup "$request" ;;
        * )             server::send_file "$target"
    esac
}

app::make_index
utility::fileOpen temp.html > /dev/null
server::start "app::router"

# if [[ "$(utility::fileOpen http://localhost:8080)" ]]; then
    # echo -e "Opened web browser to http://localhost:8080                                [\e[1;32mOK\e[0m]" >&2
# else
    # echo -e "Please open web browser to http://localhost:8080              [\e[1;32mACTION REQUIRED\e[0m]" >&2
# fi

