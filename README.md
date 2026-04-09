# pickr-telegram

Telegram SDK for [@winwithpickr](https://x.com/winwithpickr) — command parsing, pool building, membership filtering, and Mini App verification for Telegram giveaways.

Open-source, MIT-licensed. Kotlin Multiplatform (JVM + JS).

Part of the [winwithpickr](https://github.com/winwithpickr) ecosystem:
- [pickr-engine](https://github.com/winwithpickr/pickr-engine) — platform-agnostic verification core
- [pickr-twitter](https://github.com/winwithpickr/pickr-twitter) — X/Twitter SDK
- **pickr-telegram** — this repo

## What this library does

pickr-telegram handles everything specific to Telegram:

- **Command parsing** — `/pick 3 members @channel1 @channel2` → structured `TelegramParsedCommand`
- **Pool building** — assembles entry pool from button entries and comments via `TelegramPoolBuilder`
- **Membership filtering** — `MemberFilter`, `MemberOfChannelsFilter` for channel subscription checks
- **Data source interface** — `TelegramDataSource` for Bot API access (implemented by server)
- **JS exports** — `verifyPick()` and `parseCommand()` for Telegram Mini App verification

## Install

### Gradle (JVM / Kotlin Multiplatform)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.winwithpickr:telegram:0.1.0")
}
```

## Modules

| Module | Target | Description |
|---|---|---|
| `TelegramCommandParser` | common | Parses `/pick` commands into `TelegramParsedCommand` |
| `TelegramPoolBuilder` | common | Assembles entry pool pipeline from conditions + tier config |
| `TelegramDataSource` | common | Interface for Telegram Bot API data access |
| `TelegramFeatures` | common | Feature flag constants for tier gating |

### Sources (common)

| Source | Description |
|---|---|
| `ButtonEntrySource` | Fetches users who pressed the inline "Enter Giveaway" button |
| `CommentSource` | Fetches comment authors from discussion group thread |

### Filters (common)

| Filter | Tier | Description |
|---|---|---|
| `MemberFilter` | Pro+ | Requires entrants be members of the host channel |
| `MemberOfChannelsFilter` | Pro+ | Requires entrants be members of all specified channels |

### Models (common)

| Model | Description |
|---|---|
| `TelegramUser` | Telegram user identity (id, username, first/last name, premium status) |
| `TelegramEntryConditions` | Comments, buttons, memberOfHost, memberOfChannels flags |
| `TelegramParsedCommand` | Parsed command: winner count, conditions, trigger mode |

## Commands

Send in any Telegram group where the bot is an admin:

| Command | What it does |
|---|---|
| `/pick` | Pick 1 winner immediately |
| `/pick 3` | Pick 3 winners |
| `/pick setup` | Watch mode — wait for trigger |
| `/pick in 2h` | Scheduled pick (2 hours) |
| `/pick members` | Require host channel membership |
| `/pick members @ch1 @ch2` | Require membership in multiple channels |
| `/pick 3 members @ch1` | Combined: 3 winners, membership required |

## Entry methods

| Method | How it works |
|---|---|
| **Button** | Inline keyboard "Enter Giveaway" button — entries accumulated in Redis |
| **Comments** | Comments in the linked discussion group thread |

## Building

```bash
# Run all tests
./gradlew allTests

# Build JS bundle
./gradlew jsBrowserProductionWebpack

# Publish to Maven local
./gradlew publishToMavenLocal
```

## License

MIT — see [LICENSE](LICENSE)
