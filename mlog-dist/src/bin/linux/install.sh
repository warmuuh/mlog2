#!/usr/bin/env sh

BASE_DIR="$(cd "$(dirname "$0")"; pwd)" || exit 2

mkdir -p $HOME/.local/share/applications

cat <<EOF > $HOME/.local/share/applications/Milkman.desktop
[Desktop Entry]
Name=Mlog
Exec=$BASE_DIR/mlog.sh
Icon=$BASE_DIR/mlog-icon.png
Terminal=false
Type=Application
Keywords=milkman;postman;rest;http;sql;jdbc;java;
EOF
