#!/bin/bash
# Update Prometheus file_sd target files with current k3s ClusterIPs
# Usage: ./update-targets.sh

set -euo pipefail

TARGETS_DIR="$(cd "$(dirname "$0")/targets" && pwd)"
NAMESPACE="commerce"

SERVICES=("auth-service" "user-service" "catalog-service" "inventory-service" "order-service" "payment-service")

echo "Updating Prometheus targets from k3s ClusterIPs..."

for SERVICE in "${SERVICES[@]}"; do
  CLUSTER_IP=$(kubectl get svc "$SERVICE" -n "$NAMESPACE" -o jsonpath='{.spec.clusterIP}' 2>/dev/null)

  if [[ -z "$CLUSTER_IP" ]]; then
    echo "  WARN: $SERVICE not found in namespace $NAMESPACE, skipping"
    continue
  fi

  cat > "$TARGETS_DIR/$SERVICE.json" <<EOF
[
  {
    "targets": ["${CLUSTER_IP}:80"],
    "labels": {
      "application": "${SERVICE}",
      "environment": "production",
      "namespace": "${NAMESPACE}"
    }
  }
]
EOF

  echo "  OK: $SERVICE -> $CLUSTER_IP:80"
done

echo "Done. Prometheus will pick up changes within 30s."
