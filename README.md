# EasyTPA

> **Built for the European Hytale survival server at `play.hyfyve.net`**

Let your players teleport to each other! Simple TPA plugin for Hytale servers.

---

## Quick Start

1. Download the latest `EasyTPA.jar` from [Releases](../../releases)
2. Put it in your server's `plugins` folder
3. Restart your server
4. Done! Players can now use `/tpa`

---

## Commands

| Command | What it does |
|---------|--------------|
| `/tpa <player>` | Ask to teleport to someone |
| `/tpahere <player>` | Ask someone to come to you |
| `/tpaccept` | Accept a teleport request |
| `/tpdeny` | Say no to a request |
| `/tpacancel` | Cancel your request |
| `/tpatoggle` | Turn requests on/off for yourself |
| `/tpahelp` | Show all commands |

---

## How It Works

1. **Player A** types `/tpa Player_B`
2. **Player B** sees: "Player_A wants to teleport to you!"
3. **Player B** types `/tpaccept`
4. **Player A** teleports after a short countdown

That's it!

---

## Settings (For Server Owners)

Check your current settings:
```
/easytpa admin config
```

Change settings:
```
/easytpa admin set warmup 5     # Teleport delay (seconds)
/easytpa admin set timeout 120  # How long requests last (seconds)
/easytpa admin set cooldown 10  # Time between requests (seconds)
```

---

## Permissions

**For regular players:**
| Permission | What it does |
|------------|--------------|
| `tpa.use` | Can use TPA commands |
| `tpa.bypass.cooldown` | No wait between requests |
| `tpa.bypass.warmup` | Instant teleport (no countdown) |

**For admins:**
| Permission | What it does |
|------------|--------------|
| `tpa.admin` | Access to `/easytpa admin` |

---

## FAQ

**Q: How do I change the teleport delay?**
```
/easytpa admin set warmup 5
```
This sets it to 5 seconds. Use `0` for instant teleports.

**Q: How do I make requests last longer?**
```
/easytpa admin set timeout 300
```
This makes requests last 5 minutes (300 seconds).

**Q: Can players turn off TPA requests?**
Yes! Players can use `/tpatoggle` to stop receiving requests.

---

## License

MIT - Do whatever you want with it!
