#!/bin/bash
# scale.sh - Scaling script

set -e

NAMESPACE=${1:-retail-dev}
REPLICAS=${2:-3}
DEPLOYMENT=${3:-retail-app}

echo "⚖️  Scaling $DEPLOYMENT to $REPLICAS replicas in $NAMESPACE..."

kubectl scale deployment/$DEPLOYMENT --replicas=$REPLICAS -n $NAMESPACE

echo "⏳ Waiting for scaling to complete..."
kubectl rollout status deployment/$DEPLOYMENT -n $NAMESPACE --timeout=300s

echo "✅ Scaling completed!"
kubectl get pods -l app=$DEPLOYMENT -n $NAMESPACE
