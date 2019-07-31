# IMPROVED TWO FACTOR AUTHENTICATION
An improved two-factor authentication app. Currently, two-factor auth is done through SMS texting or TOTP (Google Auth). With SMS, a hacker can use SIM cloning or spoofing to get the authentication code. With TOTP, a hacker can use fishing attacks. To solve this, we combine the best of SMS and TOTP. In essence, we created a separate app with functionality of Google auth with the ability to communicate to the user the nature of the request so you know what you’re accepting.

![Architecture Diagram](https://github.com/alexander-manes/improved-2fa/blob/master/Diagram.png)
