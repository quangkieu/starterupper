
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
    // login({ 
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
            success: function (response) {
                Github.badCredentials = false;
                Github.token = response.token;
                settings.authenticated();
            },
            fail: function (response) {
                if (response.status == 401) {
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
    }
}