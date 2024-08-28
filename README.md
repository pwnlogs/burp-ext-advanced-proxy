# Burp Suite Extension - Advanced Proxy

A Burp Suite Extension that drops/redirects requests from proxy by regex matching hostname, path, and method.

### Example
![diagram](res/diagram1.png)
In the above example, `a.com/match.*` is configured to be routed to `b.com`. Any request that matches the regex will be routed to `b.com`. Note that the `Host` header of the request is not modified. Adv-proxy only changes the target protocol (`http`/`https`), the IP address (and the SNI), and the port number. These changes lies in the network and transport layer. The application layer (HTTP) of the request remains unchanged. 

`a.com/drop.*` is configued to be dropped. So, requests that match this regex will not go out from Burp Suite.

### Screenshot
![screenshot](res/screenshot.png)
