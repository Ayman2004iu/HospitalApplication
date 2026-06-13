#!/usr/bin/env bash

set -e

host="$1"
shift
port="$1"
shift

echo "Waiting for $host:$port..."

until nc -z "$host" "$port"; do
  echo "Still waiting..."
  sleep 2
done

echo "Database is up!"
exec "$@"