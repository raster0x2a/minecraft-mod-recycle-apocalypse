#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
. "$SCRIPT_DIR/env.sh"

gradle --no-daemon -Djava.net.preferIPv4Stack=true build "$@"
