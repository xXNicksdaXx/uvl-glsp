#!/bin/bash

set -e

SERVER_DIR="server"
CLIENT_DIR="client"
PROFILE="${2:-uvl}"

build_server() {
  echo "Building server..."
  pushd "$SERVER_DIR" > /dev/null
  mvn --batch-mode clean verify
  popd > /dev/null
}

build_client() {
  echo "Building client..."
  pushd "$CLIENT_DIR" > /dev/null
  yarn
  popd > /dev/null
}

build_vscode() {
  echo "Building VS Code extension for profile '$PROFILE'..."
  pushd "$CLIENT_DIR" > /dev/null
  yarn "package:$PROFILE"
  popd > /dev/null
}

clean_server() {
  echo "Cleaning server..."
  pushd "$SERVER_DIR" > /dev/null
  mvn clean
  popd > /dev/null
}

clean_client() {
  echo "Cleaning client..."
  pushd "$CLIENT_DIR" > /dev/null
  yarn clean || true
  popd > /dev/null
}

case "$1" in
  server)
    build_server
    ;;
  client)
    build_client
    ;;
  vscode)
    build_server
    build_vscode
    ;;
  clean)
    clean_server
    clean_client
    ;;
  "" )
    build_server
    build_client
    ;;
  *)
    echo "Usage: $0 [server|client|vscode|clean] [profile]"
    exit 1
    ;;
esac