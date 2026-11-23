# Cat VPN server template

This folder is a **deployment template**, not a running server. GitHub stores these files but does not run a VPN server.

## Requirements

- A Linux host that you own or are explicitly allowed to administer.
- Docker Compose support.
- A public IPv4 address (or a correctly configured public IPv6 setup).
- UDP port 51820 allowed by the host and network firewall.
- Enough memory and CPU for the expected number of clients.

A hosting account, public address, and firewall access are still required. This template does not create free hosting and does not bypass a provider's registration or network rules.

## Before deployment

1. Have a parent/guardian help choose and review the hosting provider and its age, billing, and acceptable-use terms.
2. Create a Linux server only with the provider's permission.
3. Set the `SERVERURL` environment variable to that server's public IP address or DNS name.
4. Review the firewall rules and provider policy. Do not expose any admin panel or private keys.
5. Start the service only after the host and network are ready.

## Start

From this directory, after setting `SERVERURL`:

```sh
docker compose up -d
```

The WireGuard image generates client configuration under `./config`. Treat that directory as secret: do not commit it to GitHub, share its contents, or put private keys in issues or chat.

## Current status

- This is a starter deployment template only.
- No server has been provisioned or started.
- No client configuration or private keys are included.
- The Android app is not connected to this template yet.
