#!/bin/bash

set -e

SERVER_DIR="server"
CLIENT_DIR="client"

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
  clean)
    clean_server
    clean_client
    ;;
  "" )
    build_server
    build_client
    ;;
  *)
    echo "Usage: $0 [server|client|clean]"
    exit 1
    ;;
esac