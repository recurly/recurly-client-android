#!/bin/sh

rm -rf .git

git init
git add .
git commit -m "Initial commit"

git remote add origin https://github.com/recurly/recurly-client-android.git
git push -u --force origin master

