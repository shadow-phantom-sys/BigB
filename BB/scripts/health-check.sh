
#!/bin/bash
# health-check.sh - Health check script

set -e

NAMESPACE=${1:-retail-dev}
SERVICE_NAME=${2:-retail-app-service}

echo "🏥 Performing health check for $SERVICE_NAME in $NAMESPACE..."

# Get service endpoint
SERVICE_IP=$(kubectl get svc $SERVICE_NAME -n $NAMESPACE -o jsonpath='{.spec.clusterIP}')
SERVICE_PORT=$(kubectl get svc $SERVICE_NAME -n $NAMESPACE -o jsonpath='{.spec.ports[0].port}')

if [ -z "$SERVICE_IP" ] || [ -z "$SERVICE_PORT" ]; then
    echo "❌ Service not found or not ready"
    exit 1
fi

echo "🔍 Service endpoint: $SERVICE_IP:$SERVICE_PORT"

# Port forward for testing
kubectl port-forward svc/$SERVICE_NAME 8080:80 -n $NAMESPACE &
PF_PID=$!

# Wait for port forward to be ready
sleep 5

# Health check
echo "🏥 Checking application health..."
if curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
    echo "✅ Health check passed"
    HEALTH_STATUS=0
else
    echo "❌ Health check failed"
    HEALTH_STATUS=1
fi

# API endpoint test
echo "🧪 Testing API endpoints..."
if curl -f http://localhost:8080/api/products >/dev/null 2>&1; then
    echo "✅ API endpoints accessible"
    API_STATUS=0
else
    echo "❌ API endpoints not accessible"
    API_STATUS=1
fi

# Cleanup
kill $PF_PID 2>/dev/null || true

if [ $HEALTH_STATUS -eq 0 ] && [ $API_STATUS -eq 0 ]; then
    echo "🎉 All health checks passed!"
    exit 0
else
    echo "💥 Health checks failed!"
    exit 1
fi
