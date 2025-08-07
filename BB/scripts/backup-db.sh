
#!/bin/bash
# backup-db.sh - Database backup script

set -e

NAMESPACE=${1:-retail-dev}
BACKUP_NAME="retail-db-backup-$(date +%Y%m%d-%H%M%S)"

echo "💾 Creating database backup: $BACKUP_NAME"

# Get MySQL pod
MYSQL_POD=$(kubectl get pods -n $NAMESPACE -l app=mysql -o jsonpath='{.items[0].metadata.name}')

if [ -z "$MYSQL_POD" ]; then
    echo "❌ MySQL pod not found"
    exit 1
fi

echo "🗄️  MySQL pod: $MYSQL_POD"

# Create backup
kubectl exec $MYSQL_POD -n $NAMESPACE -- mysqldump \
    -u retailuser -pretailpass \
    --single-transaction \
    --routines \
    --triggers \
    retaildb > "$BACKUP_NAME.sql"

echo "✅ Database backup created: $BACKUP_NAME.sql"

# Optionally upload to cloud storage
if [ -n "$AWS_S3_BUCKET" ]; then
    echo "☁️  Uploading backup to S3..."
    aws s3 cp "$BACKUP_NAME.sql" "s3://$AWS_S3_BUCKET/backups/"
    echo "✅ Backup uploaded to S3"
fi
