
#!/bin/bash
# logs.sh - Log collection script

set -e

NAMESPACE=${1:-retail-dev}
APP_NAME=${2:-retail-app}
LINES=${3:-100}

echo "📋 Collecting logs for $APP_NAME in $NAMESPACE (last $LINES lines)..."

# Get all pods for the app
PODS=$(kubectl get pods -n $NAMESPACE -l app=$APP_NAME -o jsonpath='{.items[*].metadata.name}')

if [ -z "$PODS" ]; then
    echo "❌ No pods found for app $APP_NAME"
    exit 1
fi

for POD in $PODS; do
    echo "📝 Logs for pod $POD:"
    echo "===================="
    kubectl logs $POD -n $NAMESPACE --tail=$LINES
    echo ""
done

echo "✅ Log collection completed!"
