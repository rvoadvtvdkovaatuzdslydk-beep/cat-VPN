# Cat VPN

Android VPN app starter project.

## Current status

- Android project structure and a simple Jetpack Compose home screen are added.
- No VPN tunnel or server is configured yet. The app does **not** currently route or protect network traffic.

## Build

Open this repository in Android Studio, allow Gradle sync, then build the `app` module.

## Next steps

1. Choose a VPN protocol and a server provider.
2. Set up a server and generate client configuration securely.
3. Implement Android's `VpnService` and a vetted protocol implementation.
4. Test DNS handling, IPv4/IPv6 routing, reconnect behavior, and leak protection.

Never commit private keys, access tokens, or real client configuration files to this repository.
