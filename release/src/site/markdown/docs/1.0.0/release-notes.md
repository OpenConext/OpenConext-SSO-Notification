# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 1.0.0                       |
| Release date              | dd-mm-yyyy (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

This is the first release.

## Configuration

For this release a new application has been released for setting an SSO notification on the OpenConext applications.
Please refer to [installation-manual.md](../installation-manual.md) for setting up the correct parameters.

When setting the parameters for the OpenConext SSO notification cookies make sure that the right domain is set and the
cookie path is the default path `/`:

    # Set the correct domain
    notification.cookie.domain=<domain>
    # The path to set for the notification cookie should not contain a path
    notification.cookie.path=/

The encryption used has been updated to AES-256 for which an update to the key used and an 
additional salt value is required. Please update the value of `crypto.secure.key` to a 
random string of 32 characters and add `crypto.secure.salt` with a random value of
16 characters. Below is an example of the values used on the development environment.

    crypto.encrypt.algorithm=AES/CBC/PKCS5Padding
    crypto.secure.key=<key>
    crypto.secure.key.type=PBKDF2WithHmacSHA256
    crypto.secure.key.algorithm=AES
    crypto.secure.salt=<salt>

