#!/bin/bash

set -e  # Exit on error

create_local_repos() {
    echo "🔄 Copying local com.ghrer.commerce Maven repository to ./local-repo in each project ..."
    cp -r ~/.m2/repository/com/ghrer/commerce ./orders/local-repo
    cp -r ~/.m2/repository/com/ghrer/commerce ./payments/local-repo
    cp -r ~/.m2/repository/com/ghrer/commerce ./inventory/local-repo
}

cleanup_local_repos() {
    echo "Cleaning up created local repos"
    rm -rf ./orders/local-repo
    rm -rf ./payments/local-repo
    rm -rf ./inventory/local-repo
}

echo "Building and publishing events-starter..."

if ./events-starter/gradlew --project-dir=events-starter --full-stacktrace clean assemble publish; then

    create_local_repos
    echo "🐳 Building and starting Docker containers ..."
    if docker compose up $@; then
        cleanup_local_repos
        echo "✅ Done."
    else
        echo "❌ Docker compose failed. Cleaning up anyway..."
        cleanup_local_repos
        exit 1
    fi
else
    echo "❌ Building events starter failed"
    exit 1
fi