#!/bin/bash

./grailsw refresh-dependencies --non-interactive
./grailsw test-app --non-interactive

cd test/projects/demo

./grailsw refresh-dependencies --non-interactive
./grailsw compile --non-interactive
