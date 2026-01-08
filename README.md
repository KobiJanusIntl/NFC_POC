# NFC Proof of Concept (POC)

This project demonstrates a basic NFC read and write proof of concept for Android using Kotlin. It includes a simple UI to write text to an NFC tag and read text from an NFC tag.

## Features

- Check for NFC availability on the device.
- Write arbitrary text to an NFC tag.
- Read text from an NFC tag.
- Simple navigation between read and write screens.

## Requirements

- Android Studio
- Minimum SDK: 21
- NFC-enabled Android device
- Kotlin 1.8+
- AndroidX libraries

## Project Structure

- **MainActivity**: Provides navigation to `WriteActivity` and `ReadActivity`.
- **WriteActivity**: Allows the user to enter text and write it to an NFC tag.
- **ReadActivity**: Listens for NFC tags and displays their contents.

## Usage

### Main Screen

The main screen has two buttons:

1. **Write**: Opens the write activity.
2. **Read**: Opens the read activity.

### Writing to NFC

1. Enter the text you want to write in the text box.
2. Tap an NFC tag to write the message.
3. A toast notification confirms success or failure.

### Reading from NFC

1. Bring an NFC tag near the device.
2. If the tag contains a text message, it will be displayed in the `TextView`.

## Implementation Details

### NFC Setup

- `NfcAdapter` is used to detect NFC hardware and manage foreground dispatch.
- `PendingIntent` is configured differently for Android S+ versus older versions.
- `IntentFilter` listens for `ACTION_NDEF_DISCOVERED` events with MIME type `"text/plain"`.

### Foreground Dispatch

Both `WriteActivity` and `ReadActivity` use foreground dispatch to ensure NFC intents are delivered to the app when it is active.

```kotlin
nfcAdapter?.enableForegroundDispatch(
    this,
    pendingIntent,
    intentFiltersArray,
    techListsArray
)
