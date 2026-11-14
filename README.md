# JOKER SCALPER PRO — Android starter project

This project recreates the main visual/workflow elements visible in your uploaded screen recording:
- dark black/teal neon dashboard and scanner
- MT5 broker connection form
- symbol / lot / execution direction / max-trades configuration
- scanner status and live-trading safety lock

## Important: live trading is NOT connected yet
The recording appears to show a separate MT5 terminal and an AlgoPro/Cortex-style companion interface. An Android app cannot safely or reliably place MT5 orders merely by collecting an MT5 login. This starter therefore does **not** send real orders and deliberately keeps live execution locked.

To complete a genuine live system, you need:
1. A private HTTPS backend/bridge that authenticates the user and validates every order.
2. An MT5 Expert Advisor installed in the user's MT5 desktop/VPS terminal. It polls the bridge for approved commands and reports positions/status.
3. Server-side risk limits (max lot, max positions, allowed symbols, spread/slippage limits, stop-loss requirement, daily loss limit, kill switch).
4. Broker demo testing, then explicit broker-side live authorization.

Do not put broker passwords, API keys, or bridge secrets in the Android APK. This starter intentionally does not persist the password field. Never enable live execution without independent testing and a reliable emergency stop.

## Build
Open this folder in Android Studio (Hedgehog or newer), allow Gradle sync, then Build > Build Bundle(s) / APK(s) > Build APK(s).
Requires JDK 17 and Android SDK 35. The project does not include a Gradle wrapper; Android Studio can import and sync it.

## Next implementation
Implement authenticated HTTPS API calls in a repository layer, then add an MT5 EA + backend. API contract suggestion:
- `POST /v1/session` — broker/bridge session handshake (no raw trading password in app)
- `GET /v1/account` — account equity, margin, balance, currency
- `GET /v1/market/{symbol}` — bid/ask/time
- `POST /v1/orders` — validated order intent with idempotency key
- `GET /v1/positions` — open positions
- `POST /v1/positions/{ticket}/close` — close request
- `POST /v1/kill-switch` — disable new orders and request flattening

Every endpoint must enforce authentication, authorization, risk limits, audit logging, replay protection and server-side validation.
