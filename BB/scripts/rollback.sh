
#!/bin/bash
# rollback.sh - Rollback deployment script

set -e

NAMESPACE=${1:-retail-dev}
REVISION=${2:-}

echo "🔄 Rolling back deployment in $NAMESPACE..."

if [ -n "$REVISION" ]; then
    echo "📋 Rolling back to revision $REVISION"
    kubectl rollout undo deployment/retail-app --to-revision=$REVISION -n $NAMESPACE
else
    echo "📋 Rolling back to previous revision"
    kubectl rollout undo deployment/retail-app -n $NAMESPACE
fi

echo "⏳ Waiting for rollback to complete..."
kubectl rollout status deployment/retail-app -n $NAMESPACE --timeout=300s

echo "✅ Rollback completed successfully!"
kubectl get pods -n $NAMESPACE
