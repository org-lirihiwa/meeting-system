#!/bin/sh
set -e

until mc alias set local http://minio:9000 "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD" >/dev/null 2>&1; do
  echo "waiting for minio..."
  sleep 2
done

mc mb --ignore-existing "local/$MINIO_BUCKET"
echo "bucket $MINIO_BUCKET is ready"