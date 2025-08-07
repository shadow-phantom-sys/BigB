
#!/bin/bash
# cleanup.sh - Cleanup script

set -e

NAMESPACE=${1:-retail-dev}
CONFIRM=${2:-false}

if [ "$CONFIRM" != "true" ]; then
    echo "⚠️  This will delete all resources in namespace $NAMESPACE"
    echo "To confirm, run: $0 $NAMESPACE true"
    exit 1
fi

echo "🗑️  Cleaning up resources in $NAMESPACE..."

# Delete application resources
kubectl delete deployment retail-app -n $NAMESPACE --ignore-not-found=true
kubectl delete service retail-app-service -n $NAMESPACE --ignore-not-found=true
kubectl delete ingress retail-app-ingress -n $NAMESPACE --ignore-not-found=true
kubectl delete hpa retail-app-hpa -n $NAMESPACE --ignore-not-found=true
kubectl delete pdb retail-app-pdb -n $NAMESPACE --ignore-not-found=true

# Delete MySQL resources
kubectl delete deployment mysql -n $NAMESPACE --ignore-not-found=true
kubectl delete service mysql-service -n $NAMESPACE --ignore-not-found=true
kubectl delete pvc mysql-pv-claim -n $NAMESPACE --ignore-not-found=true

# Delete ConfigMaps and Secrets
kubectl delete configmap retail-app-config -n $NAMESPACE --ignore-not-found=true
kubectl delete configmap mysql-init-config -n $NAMESPACE --ignore-not-found=true
kubectl delete secret mysql-secret -n $NAMESPACE --ignore-not-found=true

# Delete monitoring resources
kubectl delete servicemonitor retail-app-metrics -n $NAMESPACE --ignore-not-found=true
kubectl delete prometheusrule retail-app-alerts -n $NAMESPACE --ignore-not-found=true

echo "✅ Cleanup completed!"