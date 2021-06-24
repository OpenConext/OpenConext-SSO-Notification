# Test the service

Note: AJAX/JSONP-based SSO Notification requires a browser to accept third-party cookies. Since modern browsers 
actively prevent this behaviour, the redirect-functionality of the SSO Notification service should be used.

To test the service, a webserver which calls the service is required. The SSO Notification example can be used
for this. Note that the browser should run without security measures (which will prevent the script from running).

For Linux:
    
    google-chrome --disable-web-security
    
For MacOS:
    
    open -n -a /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --args --user-data-dir="/tmp/chrome_dev_test" --disable-web-security

For Windows:

    "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" --disable-web-security --disable-gpu --user-data-dir=~/chromeTemp

An example of the code is shown below:

    <html>
    <head>
        <title>OC SSO Notification with Ajax call (JSONP)</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    </head>
    <body>
    <p><a href="javascript:setCookie();">Set cookie</a></p>
    <p><a href="https://engine.vm.openconext.org/" target="_blank">Open Engineblock</a></p>
    <div id="ssonot"></div>
    
    <script type="text/javascript" charset="utf-8">
        var idpId = 'xxx';             // EntityId
        var idpUrl = 'https://domain';
        var ssoService = 'https://sso.vm.openconext.org';
    
        function createCookie(name, value) {
            document.cookie = name + "=" + value + "; path=/";
        }
    
        function readCookie(name) {
            var nameEQ = name + "=";
            var ca = document.cookie.split(';');
            console.log(ca);
    
            for (var i=0;i < ca.length;i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') {
                    c = c.substring(1,c.length);
                }
    
                if (c.indexOf(nameEQ) == 0) {
                    return c.substring(nameEQ.length, c.length);
                }
            }
    
            return null;
        }
    
        function setCookie() {
            if (readCookie('ssonot') != 'true') {
                setSsoNotification();
            } else {
                write("Cookie was already set!");
            }
        }
    
        function setSsoNotification() {
            var url = ssoService + "/?id=" +
                idpId + "&url=" + encodeURIComponent(idpUrl);
    
            // Check if jQuery is avaiable
            if (window.jQuery) {
                write("Using jQuery to set SSO Notification");
    
                // Use jQuery to ask the SSO Notification Service to set a SSO Notification
                $.ajax({
                    url: url,
                    dataType: 'jsonp'
                }).always(function(data) {
                    write("Cookie is set");
                });
            } else {
                write ("Using POJO to set SSO Notification");
    
                // This code can be used if no jQuery is available
                loadJSONP(url);
                write("Cookie is set");
            }
        }
    
        function write(text) {
            document.getElementById('ssonot').innerText += text + "\r\n";
        }
    
        // This function can be used if jQuery is not available
        function loadJSONP(url) {
            var script = document.createElement('script');
            script.type = 'text/javascript';
            script.src = url;
    
            // Setup handler
            window[name] = function(data) {
                callback.call((context || window), data);
                document.getElementsByTagName('head')[0].removeChild(script);
                script = null;
                delete window[name];
            };
    
            // Load JSON
            document.getElementsByTagName('head')[0].appendChild(script);
        }
    </script>
    </body>
    </html>
    
Be sure to update the values for `idpId`, `idpUrl` and `ssoService` to match your 
Identity Provider settings and environment where the SSO Notification service is 
running.
