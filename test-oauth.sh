#!/bin/bash

# JobHunt OAuth Test Script
# Usage: ./test-oauth.sh

BASE_URL="http://localhost:8080/profile/api/v1"

echo "=== JobHunt OAuth Test Script ==="
echo "Base URL: $BASE_URL"
echo ""

# Test 1: Get Google OAuth URL
echo "Test 1: Getting Google OAuth URL..."
echo "curl -X GET $BASE_URL/auth/oauth/google/url"
echo ""

response=$(curl -s -X GET "$BASE_URL/auth/oauth/google/url")
echo "Response:"
echo "$response" | jq '.' 2>/dev/null || echo "$response"
echo ""

# Extract authUrl from response
auth_url=$(echo "$response" | jq -r '.data.authUrl' 2>/dev/null)
if [ "$auth_url" != "null" ] && [ "$auth_url" != "" ]; then
    echo "✅ Google OAuth URL generated successfully"
    echo "Auth URL: $auth_url"
    echo ""
    echo "To test complete flow:"
    echo "1. Open the auth URL in browser"
    echo "2. Login with Google"
    echo "3. Check the callback URL for the authorization code"
else
    echo "❌ Failed to generate Google OAuth URL"
fi
echo ""

# Test 2: Test OAuth Callback with mock code
echo "Test 2: Testing OAuth callback with mock code..."
echo "curl -X GET \"$BASE_URL/auth/oauth/callback?code=test-code&state=test-state\""
echo ""

response=$(curl -s -X GET "$BASE_URL/auth/oauth/callback?code=test-code&state=test-state")
echo "Response:"
echo "$response" | jq '.' 2>/dev/null || echo "$response"
echo ""

# Test 3: Test OAuth Callback with error
echo "Test 3: Testing OAuth callback with error..."
echo "curl -X GET \"$BASE_URL/auth/oauth/callback?error=access_denied\""
echo ""

response=$(curl -s -X GET "$BASE_URL/auth/oauth/callback?error=access_denied")
echo "Response:"
echo "$response" | jq '.' 2>/dev/null || echo "$response"
echo ""

# Test 4: Test OAuth Callback without code
echo "Test 4: Testing OAuth callback without code..."
echo "curl -X GET \"$BASE_URL/auth/oauth/callback\""
echo ""

response=$(curl -s -X GET "$BASE_URL/auth/oauth/callback")
echo "Response:"
echo "$response" | jq '.' 2>/dev/null || echo "$response"
echo ""

echo "=== OAuth Test Complete ===" 