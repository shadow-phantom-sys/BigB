
#!/bin/bash
# setup-environment.sh - Environment setup script

set -e

ENVIRONMENT=${1:-development}

case $ENVIRONMENT in
    development)
        NAMESPACE="retail-dev"
        REPLICAS=2
        RESOURCES_REQUEST_CPU="100m"
        RESOURCES_REQUEST_MEMORY="256Mi"
        RESOURCES_LIMIT_CPU="500m"
        RESOURCES_LIMIT_MEMORY="512Mi"
        ;;
    staging)
        NAMESPACE="retail-staging"
        REPLICAS=3
        RESOURCES_REQUEST_CPU="250m"
        RESOURCES_REQUEST_MEMORY="512Mi"
        RESOURCES_LIMIT_CPU="500m"
        RESOURCES_LIMIT_MEMORY="1Gi"
        ;;
    production)
        NAMESPACE="retail-prod"
        REPLICAS=5
        RESOURCES_REQUEST_CPU="500m"
        RESOURCES_REQUEST_MEMORY="1Gi"
        RESOURCES_LIMIT_CPU="1000m"
        RESOURCES_LIMIT_MEMORY="2Gi"
        ;;
    *)
        echo "❌ Unknown environment: $ENVIRONMENT"
        echo "Available environments: development, staging, production"
        exit 1
        ;;
esac

echo "🏗️  Setting up $ENVIRONMENT environment..."
echo "📦 Namespace: $NAMESPACE"
echo "🔢 Replicas: $REPLICAS"

# Export variables for use by other scripts
export NAMESPACE
export REPLICAS
export RESOURCES_REQUEST_CPU
export RESOURCES_REQUEST_MEMORY
export RESOURCES_LIMIT_CPU
export RESOURCES_LIMIT_MEMORY

# Create namespace
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Label namespace for monitoring
kubectl label namespace $NAMESPACE monitoring=enabled --overwrite

echo "✅ Environment $ENVIRONMENT setup completed!"
echo "🚀 You can now deploy using: ./deploy.sh $NAMESPACE"