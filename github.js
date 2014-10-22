// Quick and dirty Github Javascript API wrapper
var Github = {
    badCredentials: false,
    setOTP: false,
    token: "",
    username: "",
    password: "",
    otp: "",

    getAuthorization: function () {
        if (this.token == "") {
            return "Basic " + btoa(this.username + ":" + this.password)
        } else {
            return "token " + this.token;
        }
    },

    // Generic Github API invoker
    invoke: function (settings) {
        $.ajax({
            crossDomain: true,
            url: "https://api.github.com" + settings.url,
            type: settings.method,
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Authorization", Github.getAuthorization());
                xhr.setRequestHeader("Content-Type", "application/json");
                if (Github.setOTP) {
                    xhr.setRequestHeader("X-Github-OTP", Github.otp);
                    // It is one time, after all
                    Github.setOTP=false;
                }
            },
            contentType: "application/json",
            dataType: "json",
            processData: false,
            data: JSON.stringify(settings.data),
            success: settings.success,
            error: settings.fail
        });
    },

    // Login to Github
    // Github.login({ 
    //  authenticated: function() { /* Do this when authenticated */ },
    //  badCredential: function() { /* Do this if we have a bad credential */ },
    //  twoFactor:     function() { /* Do this if we need a one time password */ }
    // })
    login: function (settings) {
        this.username = settings.username;
        this.password = settings.password;
        this.otp = settings.otp;
        var date = new Date();
        this.invoke({
            url: "/authorizations",
            method: "POST",
            data: {
                scopes: ["repo","public_repo","user","write:public_key","user:email"],
                note: "starterupper " + date.toISOString()
            },
            success: function (data) {
                Github.badCredentials = false;
                Github.token = data.token;
                localStorage["Github.token"] = data.token;
                settings.authenticated();
            },
            fail: function (response) {
                if (response.status == 401) {
                    // We should be looking at the response headers instead, probably: response.getResponseHeader('some_header')
                    if (response.responseJSON.message == "Bad credentials") {
                        Github.badCredentials = true;
                        settings.badCredential();
                    } else if (response.responseJSON.message == "Must specify two-factor authentication OTP code.") {
                        Github.setOTP = true;
                        settings.twoFactor();
                    }
                }
            }
        });
    },
    
    // Get email configuration
    // Github.getEmail({
    // email: "something@domain",
    // added: function() {/* what do we do when the email has been added? */}
    // missing: function() {/* what do we do when the email wasn't even added? */}
    // verified: function() {/* what do we do when verified? */}
    // unverified: function() {/* what do we do when the email is added, but unverified? */}
    // fail: function() {/* what do we do when things aren't working? */}
    // })
    getEmail: function(settings) {
        Github.invoke({
            url: "/user/emails",
            method: "GET",
            data: {},
            success: function (response) {
                for (index in response) {
                    if (response[index].email == settings.email) {
                        settings.added();
                        if (response[index].verified) {
                            settings.verified();
                            return;
                        } else {
                            settings.unverified();
                            return;
                        }
                    }
                }
                settings.missing();
            },
            fail: settings.fail
        });
    },
    
    // Github.setEmail({
    // email: "something@domain",
    // success: function() {/* what do we do if it worked? */},
    // fail: function() {/* what do we do if it didn't */}
    // })
    setEmail: function(settings) {
        Github.invoke({
            url: "/user/emails",
            method: "POST",
            data: [settings.email],
            success: settings.success,
            fail: settings.fail
        });
    },

    // Github.getUser({
    // success: function() {/* what do we do if it worked? */},
    // fail: function() {/* what do we do if it didn't */}
    // })
    getUser: function(settings) {
        Github.invoke({
            url: "/user",
            method: "GET",
            data: {},
            success: settings.success,
            fail: settings.fail
        });
    },
    
    setUser: function(settings) {
        Github.invoke({
            url: "/user",
            method: "PATCH",
            data: settings.data,
            success: settings.success,
            fail: settings.fail
        });
    },
    
    // Github.shareKey({
    // key: "Public SSH key here",
    // title: "some title here",
    // success: function() {/* what to do if it worked */},
    // fail: function() {/* what to do if it didn't */}
    //});
    shareKey: function(settings) {
        Github.invoke({
            url: "/user/keys",
            method: "GET",
            data: {},
            success: function(response) {
                for (index in response) {
                    if (response[index].key == settings.key) {
                        settings.success();
                        return;
                    }
                }
                // Send key
                Github.invoke({
                    url: "/user/keys",
                    method: "POST",
                    data: {
                        title: settings.title,
                        key: settings.key
                    },
                    success: settings.success,
                    fail: settings.fail
                });
            },
            fail: settings.fail
        });
    },

    // Github.createRepo({
    // login: Github username,
    // repo: Repository name,
    // success: function() {/* what to do if it worked */},
    // fail: function() {/* what to do if it didn't */}
    //});
    createRepo: function(settings) {
        Github.invoke({
            url: "/repos/" + settings.login + "/" + settings.repo,
            method: "GET",
            data: {},
            // If the repo is created already, we're done
            success: settings.success,
            // Otherwise, we need to make it
            fail: function(response) {
                Github.invoke({
                    url: "/user/repos",
                    method: "POST",
                    data: {
                        name: settings.repo,
                        "private": true
                    },
                    success: settings.success,
                    fail: settings.fail
                });
            }
        });
    },

    // Github.addCollaborator({
    // login: Github username,
    // repo: Repository name,
    // collaborator: a collaborator,
    // success: function() {/* what to do if it worked */},
    // fail: function() {/* what to do if it didn't */}
    //});
    addCollaborator: function(settings) {
        var url = "/repos/" + settings.login + "/" + settings.repo + "/collaborators/" + settings.collaborator;
        Github.invoke({
            method: "GET",
            url: url,
            data: {},
            success: settings.success,
            fail: function() {
                Github.invoke({
                    method: "PUT",
                    url: url,
                    data: {},
                    success: settings.success,
                    fail: settings.fail
                });
            }
        });
    }
}