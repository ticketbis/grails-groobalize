#!/bin/bash -e

./grailsw refresh-dependencies -non-interactive -plain-output
./grailsw test-app -non-interactive -plain-output

cd test/projects/demo

./grailsw refresh-dependencies -non-interactive -plain-output
./grailsw test-app -non-interactive -plain-output
