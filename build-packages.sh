#!/usr/bin/env bash
# Requires JDK 17, Android SDK 35/build-tools 35.0.0, Gradle 8.9, openssl.
set -euo pipefail
cd "$(dirname "$0")"
ROOT=$PWD
KEYS="$ROOT/.local-signing"
OUT="$ROOT/installers"
mkdir -p "$OUT"
keys() {
  umask 077
  mkdir -p "$KEYS"
  if [[ ! -f "$KEYS/private.pem" && ! -f "$KEYS/certificate.pem" ]]; then
    openssl req -newkey rsa:2048 -nodes -keyout "$KEYS/private.pem" -x509 -days 3650 -out "$KEYS/certificate.pem" -subj '/CN=BMS Ride20 Development Test/'
  fi
  test -s "$KEYS/private.pem" && test -s "$KEYS/certificate.pem"
  openssl pkcs8 -topk8 -inform PEM -outform DER -in "$KEYS/private.pem" -nocrypt -out "$KEYS/private.pk8"

}
android() {
  keys
  local sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
  if [[ -z "$sdk" ]]; then echo 'Set ANDROID_HOME to your Android SDK path.' >&2; exit 1; fi
  local bt="$sdk/build-tools/35.0.0"
  test -x "$bt/apksigner" && test -x "$bt/zipalign"
  (cd android && gradle --no-daemon :app:assembleRelease)
  local apk="$ROOT/android/app/build/outputs/apk/release/app-release-unsigned.apk"
  test -s "$apk"
  "$bt/zipalign" -f 4 "$apk" "$OUT/.aligned.apk"
  "$bt/apksigner" sign --key "$KEYS/private.pk8" --cert "$KEYS/certificate.pem" --out "$OUT/BMS_Ride20_Android_v0.3.3.apk" "$OUT/.aligned.apk"
  rm -f "$OUT/.aligned.apk"
  "$bt/apksigner" verify --verbose --print-certs "$OUT/BMS_Ride20_Android_v0.3.3.apk" | tee "$OUT/APK-signature.txt"
  openssl x509 -in "$KEYS/certificate.pem" -noout -fingerprint -sha256 > "$OUT/test-certificate.txt"
}
case "${1:-android}" in
  keys) keys;;
  android) android;;
  *) echo 'Usage: bash build-packages.sh [android|keys]' >&2; exit 2;;
esac
