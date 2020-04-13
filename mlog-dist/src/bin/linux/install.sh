#!/usr/bin/env sh

BASE_DIR="$(cd "$(dirname "$0")"; pwd)" || exit 2

mkdir -p $HOME/.local/share/applications

cat <<EOF > $HOME/.local/share/applications/Mlog.desktop
[Desktop Entry]
Name=Mlog
Exec=$BASE_DIR/mlog.sh
Icon=$BASE_DIR/mlog-icon.png
Terminal=false
Type=Application
Keywords=mlog;log;logging;java;kubernetes;k8s
EOF
