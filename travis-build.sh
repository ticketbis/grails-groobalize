#!/bin/bash -e

./grailsw refresh-dependencies --non-interactive
./grailsw test-app --non-interactive

cd test/projects/demo

./grailsw refresh-dependencies --non-interactive
./grailsw test-app --non-interactive
