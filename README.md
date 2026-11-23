# Cat VPN

Android VPN app starter project.

## Current status

- Android project structure and Jetpack Compose home screen.
- Android `VpnService` entry point registered in the manifest.
- The service is currently a scaffold only: it stops without opening a tunnel.
- No VPN protocol engine, server endpoint, or client configuration is installed. The app does **not** currently route or protect network traffic.

## Build

Open this repository in Android Studio, allow Gradle sync, then build the `app` module.

## Next steps

1. Select a supported VPN protocol and a server we are allowed to use.
2. Set up the server and generate client configuration securely.
3. Integrate a maintained protocol engine and connect it to Android's `VpnService`.
4. Add the Android VPN-consent flow and connection controls.
5. Test DNS handling, IPv4/IPv6 routing, reconnect behavior, and leak protection.

Never commit private keys, access tokens, or real client configuration files to this repository.
