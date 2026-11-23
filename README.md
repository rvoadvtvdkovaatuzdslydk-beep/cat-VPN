# Cat VPN

Android VPN app built with Jetpack Compose and the official embeddable WireGuard tunnel library.

## Current status

- Android app shell with a Netherlands server label.
- WireGuard tunnel engine dependency integrated.
- Android VPN consent flow integrated.
- User can import a WireGuard configuration file from device storage.
- Imported configuration is parsed in memory and is not committed to the repository.
- Connect and disconnect controls call the WireGuard backend.
- **No VPN server is provisioned by this repository.** A working server endpoint and valid client `.conf` file are still required before a connection can succeed.

## Build

Open this repository in Android Studio, allow Gradle sync, then build the `app` module. The GitHub Actions workflow can also build a debug APK.

## Importing a configuration

On the phone, open Cat VPN and choose **Import config**. Select the client configuration file supplied by a WireGuard server administrator. Then approve Android's VPN permission and tap Connect.

Treat the configuration as a password: it contains private key material. Do not post it in chat, commit it to GitHub, or share it publicly.

## Important limitations

A connected tunnel indicator means the WireGuard backend accepted the tunnel configuration; it does not by itself prove that the server is reachable or that internet traffic is flowing. Verify the server, DNS, routing, and IPv4/IPv6 behavior before relying on the VPN.

Never commit private keys, access tokens, or real client configuration files to this repository.
