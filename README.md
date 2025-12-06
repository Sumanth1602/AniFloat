# AniFloat

AniFloat is an Android overlay companion for AniList users. Log in with AniList, pick an anime, and you get a draggable floating control bubble that stays on top of streaming apps so you can increment or edit progress without leaving the video. Features includes:

- AniList login
- “Watching” list and live progress bars
- Floating overlay service and instant sequel toggle
- Quick App Shortcut (“Quick overlay”) to resume the most recently updated series

## Releases
- [Latest Releases](https://github.com/Sumanth1602/AniFloat/releases/latest)

## Development
```
./gradlew assembleDebug
```

Push the floating overlay permission in Settings if prompted. To log in, register an AniList OAuth app (implicit grant) and set `Constants.CLIENT_ID`.

