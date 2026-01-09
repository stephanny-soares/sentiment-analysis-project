#!/bin/bash

# Security Check Script for Sentiment Analysis Project
# Run this script to verify security configurations

echo "🔒 Security Check for Sentiment Analysis Project"
echo "================================================"

# Check if .env is in .gitignore
if grep -q "\.env" .gitignore 2>/dev/null; then
    echo "✅ .env is properly ignored by git"
else
    echo "❌ .env is NOT in .gitignore - FIX IMMEDIATELY!"
fi

# Check if .env file exists
if [ -f ".env" ]; then
    echo "✅ .env file exists"
else
    echo "❌ .env file missing - copy from .env.example"
fi

# Check for hardcoded credentials in docker-compose.yml
if grep -q "password:" docker-compose.yml; then
    echo "❌ Hardcoded passwords found in docker-compose.yml"
else
    echo "✅ No hardcoded passwords in docker-compose.yml"
fi

# Check Redis configuration
if grep -q "requirepass" docker-compose.yml; then
    echo "✅ Redis password authentication configured"
else
    echo "❌ Redis password authentication missing"
fi

# Check for exposed database ports
if grep -q "5432:5432" docker-compose.yml; then
    echo "⚠️  PostgreSQL port exposed (remove for production)"
else
    echo "✅ PostgreSQL port not exposed"
fi

if grep -q "6379:6379" docker-compose.yml; then
    echo "⚠️  Redis port exposed (remove for production)"
else
    echo "✅ Redis port not exposed"
fi

# Check CORS configuration
if grep -q 'origins = "\*"' backend/src/main/java/com/api/sentiment/controller/SentimentController.java; then
    echo "❌ CORS allows all origins - restrict for production"
else
    echo "✅ CORS properly restricted"
fi

# Check input validation
if grep -q "10000" data_science/sentiment_api.py; then
    echo "✅ Input length validation implemented"
else
    echo "❌ Input length validation missing"
fi

echo ""
echo "Security check completed!"
echo "Review any ❌ or ⚠️ items above."