#!/bin/bash
# deploy.sh - Main deployment script

set -e

# Configuration
NAMESPACE=${1:-retail-dev}
IMAGE_TAG=${2:-latest}
ENVIRONMENT=${3:-development}

echo "🚀 Starting deployment to $ENVIRONMENT environment..."
echo "📦 Namespace: $NAMESPACE"
echo "🏷️  Image Tag: $IMAGE_TAG"

# Create namespace if it doesn't exist
echo "📁 Creating namespace $NAMESPACE..."
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Apply secrets
echo "🔐 Applying secrets..."
kubectl apply -f k8s/mysql-secret.yaml -n $NAMESPACE

# Apply ConfigMaps
echo "⚙️  Applying configuration..."
envsubst < k8s/configmap.yaml | kubectl apply -f - -n $NAMESPACE

# Deploy MySQL (skip for production)
if [ "$ENVIRONMENT" != "production" ]; then
    echo "🗄️  Deploying MySQL..."
    kubectl apply -f k8s/mysql-deployment.yaml -n $NAMESPACE
    kubectl apply -f k8s/mysql-service.yaml -n $NAMESPACE
    
    echo "⏳ Waiting for MySQL to be ready..."
    kubectl wait --for=condition=available --timeout=300s deployment/mysql -n $NAMESPACE
fi

# Update application deployment with new image
echo "🔄 Updating application image..."
sed "s|IMAGE_TAG|$IMAGE_TAG|g" k8s/retail-app-deployment.yaml > /tmp/retail-app-deployment.yaml

# Deploy application
echo "🚀 Deploying application..."
kubectl apply -f /tmp/retail-app-deployment.yaml -n $NAMESPACE
kubectl apply -f k8s/retail-app-service.yaml -n $NAMESPACE
kubectl apply -f k8s/retail-app-ingress.yaml -n $NAMESPACE

# Wait for deployment to complete
echo "⏳ Waiting for application deployment..."
kubectl rollout status deployment/retail-app -n $NAMESPACE --timeout=600s

# Apply monitoring configuration
if kubectl get crd servicemonitors.monitoring.coreos.com >/dev/null 2>&1; then
    echo "📊 Setting up monitoring..."
    kubectl apply -f k8s/monitoring-setup.yaml -n $NAMESPACE
fi

# Verify deployment
echo "✅ Verifying deployment..."
kubectl get pods -n $NAMESPACE
kubectl get services -n $NAMESPACE
kubectl get ingress -n $NAMESPACE

echo "🎉 Deployment completed successfully!"

# Cleanup
rm -f /tmp/retail-app-deployment.yaml
